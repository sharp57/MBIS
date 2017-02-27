package neighbor.com.mbis.network;

import android.os.Message;
import android.util.Log;

import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import neighbor.com.mbis.common.SocketHanderMessageDfe;
import neighbor.com.mbis.util.Func;
import neighbor.com.mbis.views.maputil.Util;

import static java.lang.String.format;


/**
 * Created by ts.ha on 2017-02-24.
 */

public class SocketSend extends Thread {


    private static final String TAG = "SocketSend";
    private final Socket mSocket;
    private byte[] mWriteData;

    public SocketSend(Socket socket) {
        super();
        mSocket = socket;
    }


    public void setData(byte[] writeData) {
        mWriteData = writeData;
    }


    @Override
    public void run() {

        InputStream is = null;

        Logger.getLogger(TAG).debug("run ~~~~~~~~~~~~~~~~");
        try {
            if (mSocket != null) {
                is = mSocket.getInputStream();

                DataInputStream dis = new DataInputStream(is);
                OutputStream os = mSocket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.write(mWriteData);


//                Log.e(TAG, " Server connected !!" + mSocket + " / " + sAddress + " / " + socket
//                        .isConnected());
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
//                    message.what = mSuccessCode;
//                    mHandler.sendMessage(message);
                } else {
                    Logger.getLogger(TAG).debug("잘못된 값이 서버에서 들어왔을 때");
                    Message message = new Message();
//                    message.what = mFailCode;
                    if (dis.read() == -1) {
                        Logger.getLogger(TAG).debug("만약 -1이면 서버가 끊어진거임.");
                        message.obj = SocketHanderMessageDfe.ERROR_READ_SERVER_DISCONNECT;
//                        mHandler.sendMessage(message);
                    } else {
//                        message.obj = mFailCode;
//                        mHandler.sendMessage(message);
                    }
                }
            } else {
                Logger.getLogger(TAG).debug("mSocket  null ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
