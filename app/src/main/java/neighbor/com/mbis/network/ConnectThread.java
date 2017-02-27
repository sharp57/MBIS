package neighbor.com.mbis.network;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import neighbor.com.mbis.common.ConnectInfo;

/**
 * Created by ts.ha on 2017-02-24.
 */

public class ConnectThread extends Thread {

    private Socket socket;
    private InetSocketAddress sAddress;
    private Callback mCallback;
    private byte[] mWriteData;

    interface Callback {
        void onConnected(Socket socket);

        void onNotConnected();
    }

    ConnectThread(Context context) {
        super();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setData(byte[] writeData) {
        mWriteData = writeData;
    }

    @Override
    public void run() {
//        super.run();

        try {
            socket = new Socket(ConnectInfo.IP, ConnectInfo.PORT);
            socket.setKeepAlive(true);
            socket.setReuseAddress(true);
            socket.setSoLinger(true, 0);
            sAddress = new InetSocketAddress(ConnectInfo.IP, ConnectInfo.PORT);
            if (!socket.isConnected()) {
                socket.connect(sAddress, ConnectInfo.SERVER_CONNECT_TIMEOUT);
            }
            mCallback.onConnected(socket);

        } catch (IOException e) {
            e.printStackTrace();
            mCallback.onNotConnected();
        }
    }
}
