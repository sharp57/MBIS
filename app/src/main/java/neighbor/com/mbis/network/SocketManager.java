package neighbor.com.mbis.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.apache.log4j.Logger;

import java.net.Socket;

/**
 * Created by ts.ha on 2017-02-24.
 */

public class SocketManager extends Handler {
    private static final String TAG = SocketManager.class.getSimpleName();
    private final Context mContext;
    private ConnectThread connectThread;
    private Socket mSocket;
    private SocketSend socketSend;

    public SocketManager(Context applicationContext, Looper looper) {
        super(looper);
        mContext = applicationContext;
    }


    public void connect() {
        connectThread = new ConnectThread(mContext);
        connectThread.setCallback(callbackConnect);
        connectThread.start();
    }

    ConnectThread.Callback callbackConnect = new ConnectThread.Callback() {

        @Override
        public void onConnected(Socket socket) {

            mSocket = socket;
            Logger.getLogger(TAG).debug("onConnected : " + mSocket.isConnected());
            socketSend = new SocketSend(mSocket);
        }

        @Override
        public void onNotConnected() {
            Logger.getLogger(TAG).debug("onNotConnected");
        }
    };


    public void sendData(final byte[] data) {
        try {
            connectThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.getLogger(TAG).debug("sendData ");
        socketSend.setData(data);
        socketSend.start();
    }

}
