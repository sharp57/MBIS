package neighbor.com.mbis.network;

import android.os.Handler;
import android.util.Log;

import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import neighbor.com.mbis.function.Func;
import neighbor.com.mbis.maputil.BytePosition;
import neighbor.com.mbis.maputil.Data;
import neighbor.com.mbis.maputil.HandlerPosition;
import neighbor.com.mbis.maputil.Util;

import static neighbor.com.mbis.maputil.Data.readData;

/**
 * Created by ts.ha on 2017-02-17.
 */

public class SocketConnect extends Thread {


    private Socket socket;
    private Handler mHandler;
    private InetSocketAddress sAddress;
    private InputStream is;
    private DataInputStream dis;
    private OutputStream os;
    private DataOutputStream dos;
    static boolean runFlag = true;
    private String mIp;
    private int mPort;
    private static String TAG = SocketConnect.class.getSimpleName();
    private Data mData;
    private byte[] mWriteData;

    public void setSocket(Handler handler, byte[] writeData) {
        this.mIp = NetworkUtil.IP;
        this.mPort = NetworkUtil.PORT;
        this.mHandler = handler;
        this.mWriteData = writeData;
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
            Logger.getLogger(TAG).debug("run~~~~~~~~~~~~~");
            if (!socket.isConnected()) {
                socket.connect(sAddress, HandlerPosition.SERVER_CONNECT_TIMEOUT);
            }

            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
            dos.write(mWriteData);
//            while (runFlag )
            {
                Log.e(TAG, " Server connected !!" + socket + " / " + sAddress + " / " + socket
                        .isConnected());
                byte[] headerData = new byte[BytePosition.HEADER_SIZE];
                dis.read(headerData);
                byte[] dataLengthBuf = new byte[4];
                for (int i = 0; i < 4; i++) {
                    dataLengthBuf[i] = headerData[i + BytePosition.HEADER_DATALENGTH];
                }
                int dataLength = Func.byteToInteger(Util.byteReverse(dataLengthBuf), 4);
//            Util.log(tag, "read length." + dataLength);
                byte[] bodyData = new byte[dataLength];
                dis.read(bodyData);

                if (dataLength > 0) {
                    //정상적인 데이터 수신
                    readData = Func.mergyByte(headerData, bodyData);
                    if (headerData[BytePosition.HEADER_OPCODE] == 0x33) {
                        Data.readFTPData = readData;
                        sleep(3000);
                    }
                    Log.e(TAG, "network : data read success: " + String.format("%02d", headerData[BytePosition.HEADER_OPCODE]));
                    mHandler.sendEmptyMessage(HandlerPosition.DATA_READ_SUCESS);
                } else {
                    //잘못된 값이 서버에서 들어왔을 때
//                    if (headerData[BytePosition.HEADER_OPCODE] == 0x01) {
//                    } else
                    Logger.getLogger(TAG).debug("잘못된 값이 서버에서 들어왔을 때");
                    if (dis.read() == -1) {
                        Logger.getLogger(TAG).debug("만약 -1이면 서버가 끊어진거임.");
                        runFlag = false;

                        mHandler.sendEmptyMessage(HandlerPosition.READ_SERVER_DISCONNECT_ERROR);
                    } else {
                        mHandler.sendEmptyMessage(HandlerPosition.READ_DATA_ERROR);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

            runFlag = false;
            mHandler.sendEmptyMessage(HandlerPosition.READ_SERVER_DISCONNECT_ERROR);
        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            try {
                dis.close();
                dos.close();
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
