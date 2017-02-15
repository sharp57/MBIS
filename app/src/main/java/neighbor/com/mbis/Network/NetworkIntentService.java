package neighbor.com.mbis.Network;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import neighbor.com.mbis.Function.Func;
import neighbor.com.mbis.MapUtil.BytePosition;
import neighbor.com.mbis.MapUtil.Data;
import neighbor.com.mbis.MapUtil.HandlerPosition;
import neighbor.com.mbis.MapUtil.Util;

import static java.lang.Thread.sleep;

public class NetworkIntentService extends IntentService {

    private final String tag = getClass().toString();
    private String IP;
    private int PORT;
    private Socket socket;

    public static boolean runFlag = true;

    public static Handler mHandler;
    private SocketAddress sAddress;

    private InputStream is;
    private DataInputStream dis;
    private OutputStream os;
    private DataOutputStream dos;


    //    private static ToServer ourInstance = null;
//
//    public static ToServer getInstance(String IP, int PORT, Handler mHandler) {
//        if (ourInstance == null) {
//            ourInstance = new ToServer(IP, PORT, mHandler);
//        }
//        return ourInstance;
//    }
    public NetworkIntentService() {
        super("NetworkIntentService");
        runFlag = true;
        this.IP = NetworkUtil.IP;
        this.PORT = NetworkUtil.PORT;

        socket = new Socket();
        sAddress = new InetSocketAddress(this.IP, this.PORT);
    }


    public NetworkIntentService(String IP, int PORT) {
        super("NetworkIntentService");
        runFlag = true;
        this.IP = IP;
        this.PORT = PORT;

        socket = new Socket();
        sAddress = new InetSocketAddress(IP, PORT);
    }

    public NetworkIntentService(String IP, int PORT, Handler mHandler) {
        super("NetworkIntentService");
        runFlag = true;
        this.IP = IP;
        this.PORT = PORT;
        this.mHandler = mHandler;

        socket = new Socket();
        sAddress = new InetSocketAddress(IP, PORT);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate", "onCreate");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        soc.close();
    }


//    public NetworkIntentService() {
//        super("NetworkIntentService");
//        Log.e("NetworkIntentService", "NetworkIntentService");
//    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
//            if (socket == null) {
//                socket = new Socket();
//            }
//            if (sAddress == null) {
//                sAddress = new InetSocketAddress(IP, PORT);
//            }

            socket = new Socket();
            sAddress = new InetSocketAddress(IP, PORT);
            Log.e("[Client]", " Server connected !!" + socket + " / " + sAddress);
            //타임아웃
            socket.connect(sAddress, HandlerPosition.SERVER_CONNECT_TIMEOUT);

//            socket = new Socket(IP, PORT);
            Log.e("[Client]", " Server connected !!" + socket + " / " + sAddress);

            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                sleep(HandlerPosition.SERVER_CONNECT_TIMEOUT);
                mHandler.sendEmptyMessage(HandlerPosition.SOCKET_CONNECT_ERROR);
            } catch (InterruptedException e1) {
            } catch (NullPointerException e2) {
            }

        }
        if (dis != null) {
            mHandler.sendEmptyMessage(HandlerPosition.SOCKET_CONNECT_SUCCESS);
            readData();
        }
        Log.e("onHandleIntent", "onHandleIntent");
    }

//    public void sendData() {
////        soc.writeData();
//    }

//    public void setHandler(Handler handler) {
////        soc.setHandler(handler);
//    }

    public void writeData(byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket != null && dos != null && Data.writeData != null) {
                    try {
                        Log.e("[sendData]", "222");
                        dos.write(Data.writeData);
                        Log.e("[sendData]", " send byte Data !!");
                    } catch (IOException e) {
                        mHandler.sendEmptyMessage(HandlerPosition.WRITE_SERVER_DISCONNECT_ERROR);
                        Log.d("[sendData]", " Failed send byte Data !!");
                    }
                }
            }
        }).start();
    }

    public void writeData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket != null && dos != null && Data.writeData != null) {
                    try {
                        Log.e("[sendData]", "222");
                        dos.write(Data.writeData);
                        Log.e("[sendData]", " send byte Data !!: " + new java.math.BigInteger(Data.writeData).toString(16));
                    } catch (IOException e) {
                        mHandler.sendEmptyMessage(HandlerPosition.WRITE_SERVER_DISCONNECT_ERROR);
                        Log.d("[sendData]", " Failed send byte Data !!");
                    }
                }
            }
        }).start();
    }

    public void close() {
        try {
            if (os != null && is != null &&
                    dos != null && dis != null) {
                os.close();
                is.close();
                runFlag = false;
                dos.close();
                dis.close();
            }
            if(socket != null){
                socket.close();
            }
            socket = null;
            sAddress = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void readData() {
        while (runFlag) {
            try {
                //바이트 크기는 넉넉하게 잡아서 할 것.
                //가변적으로 못바꾸니 넉넉하게 잡고 알아서 fix 하기
                byte[] headerData = new byte[BytePosition.HEADER_SIZE];
                dis.read(headerData);
                byte[] dataLengthBuf = new byte[4];
                for (int i = 0; i < 4; i++) {
                    dataLengthBuf[i] = headerData[i + BytePosition.HEADER_DATALENGTH];
                }
                int dataLength = Func.byteToInteger(dataLengthBuf, 4);
                byte[] bodyData = new byte[dataLength];
                dis.read(bodyData);

                if (dataLength > 0) {
                    //정상적인 데이터 수신
                    Data.readData = Func.mergyByte(headerData, bodyData);
                    if(headerData[BytePosition.HEADER_OPCODE] == 0x33) {
                        Data.readFTPData = Data.readData;
                        sleep(3000);
                    }
                    //Log.e("recv", "network : data read success: " + String.format("%02d", headerData[BytePosition.HEADER_OPCODE]));
                    mHandler.sendEmptyMessage(HandlerPosition.DATA_READ_SUCESS);
                } else {
                    //잘못된 값이 서버에서 들어왔을 때
//                    if (headerData[BytePosition.HEADER_OPCODE] == 0x01) {
//                    } else
                    if (dis.read() == -1) {
                        //만약 -1이면 서버가 끊어진거임.
                        Util.log(tag,"read -1 / disconnected..");
                        runFlag = false;
                        mHandler.sendEmptyMessage(HandlerPosition.READ_SERVER_DISCONNECT_ERROR);
                    } else {
                        mHandler.sendEmptyMessage(HandlerPosition.READ_DATA_ERROR);
                    }
                }
            } catch (IOException e) {
                runFlag = false;
                mHandler.sendEmptyMessage(HandlerPosition.READ_SERVER_DISCONNECT_ERROR);
            } catch (InterruptedException e) {
            }

        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    private void readErrorCheck() {
        try {
            if (dis.read() == -1) {
                //서버의 연결이 끊김 = 재연결 시도 메세지를 핸들러에 보냄
                close();
                mHandler.sendEmptyMessage(HandlerPosition.READ_SERVER_DISCONNECT_ERROR);
            }
        } catch (IOException e) {
        }
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }


    private final IBinder mBinder = new NetworkIntentService.MainServiceBinder();

    //서비스 바인더 내부 클래스 선언
    public class MainServiceBinder extends Binder {
        public NetworkIntentService getService() {
            return NetworkIntentService.this; //현재 서비스를 반환.
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
