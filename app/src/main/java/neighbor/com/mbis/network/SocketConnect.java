package neighbor.com.mbis.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import neighbor.com.mbis.common.ConnectInfo;
import neighbor.com.mbis.common.SocketHanderMessageDfe;
import neighbor.com.mbis.util.Func;
import neighbor.com.mbis.views.maputil.Util;

import static java.lang.String.format;

/**
 * Created by ts.ha on 2017-02-17.
 */

public class SocketConnect extends Thread {


    private Socket socket;
    private Handler mHandler;
    private InetSocketAddress sAddress;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String mIp;
    private int mPort;
    private static String TAG = SocketConnect.class.getSimpleName();
    private byte[] mWriteData;
    private int mSuccessCode, mFailCode;

    public void setSocket(Handler handler, byte[] writeData, int successCode, int failCode) {
        this.mIp = ConnectInfo.IP;
        this.mPort = ConnectInfo.PORT;
        this.mHandler = handler;
        this.mWriteData = writeData;
        this.mFailCode = failCode;
        this.mSuccessCode = successCode;

        String dd = "";
        for (int i = 0; i < writeData.length; i++) {
            dd = dd + String.format("%02x ", writeData[i]);
        }
        Logger.getLogger(TAG).error("send: " + dd);
    }

    @Override
    public void run() {
        super.run();
        try {
            socket = new Socket(mIp, mPort);
            socket.setKeepAlive(true);
            socket.setReuseAddress(true);
            socket.setSoLinger(true, 0);

            sAddress = new InetSocketAddress(mIp, mPort);
            if (!socket.isConnected()) {
                socket.connect(sAddress, ConnectInfo.SERVER_CONNECT_TIMEOUT);
            }

            InputStream is = socket.getInputStream();
            dis = new DataInputStream(is);
            OutputStream os = socket.getOutputStream();
            dos = new DataOutputStream(os);
            dos.write(mWriteData);
            Log.e(TAG, " Server connected !!" + socket + " / " + sAddress + " / " + socket
                    .isConnected());
            byte[] headerData = new byte[BytePosition.HEADER_SIZE];
            dis.read(headerData);

            String dd = "";
            for (byte aData : headerData) {
                dd = dd + format("%02x ", aData);
            }
            Log.e(TAG, "network : header data read success: " + dd);

            byte[] dataLengthBuf = new byte[4];
            System.arraycopy(headerData, 14, dataLengthBuf, 0, 4);
            int dataLength = Func.byteToInteger(Util.byteReverse(dataLengthBuf), 4);
            Util.log(TAG, "read length." + dataLength);
            byte[] bodyData = new byte[dataLength];
            dis.read(bodyData);

            if (dataLength > 0) {
                //정상적인 데이터 수신
                byte[] responseData = Func.mergeByte(headerData, bodyData);

                 dd = "";
                for (byte aData : responseData) {
                    dd = dd + format("%02x ", aData);
                }
                Log.e(TAG, "network : responseData data read success: " + dd);

                Log.e(TAG, "network : data read success: " + String.format("%02d", headerData[BytePosition.HEADER_OPCODE]));
                Message message = new Message();
                message.obj = responseData;
                message.what = mSuccessCode;
                mHandler.sendMessage(message);
            } else {
                Logger.getLogger(TAG).debug("잘못된 값이 서버에서 들어왔을 때");
                Message message = new Message();
                message.what = mFailCode;
                if (dis.read() == -1) {
                    Logger.getLogger(TAG).debug("만약 -1이면 서버가 끊어진거임.");
                    message.obj = SocketHanderMessageDfe.ERROR_READ_SERVER_DISCONNECT;
                    mHandler.sendMessage(message);
                } else {
                    message.obj = mFailCode;
                    mHandler.sendMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = mFailCode;
            message.obj = mFailCode;
            mHandler.sendMessage(message);
        } finally {
            try {
                dis.close();
                dos.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
