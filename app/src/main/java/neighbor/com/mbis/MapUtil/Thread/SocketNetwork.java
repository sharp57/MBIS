//package neighbor.com.mbis.MapUtil.Thread;
//
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.util.Log;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.net.SocketAddress;
//
//import neighbor.com.mbis.Function.Func;
//import neighbor.com.mbis.MapUtil.BytePosition;
//import neighbor.com.mbis.MapUtil.Data;
//import neighbor.com.mbis.MapUtil.HandlerPosition;
//import neighbor.com.mbis.MapUtil.OPUtil;
//import neighbor.com.mbis.MapUtil.Value.MapVal;
//import neighbor.com.mbis.Network.NetworkUtil;
//
//import static java.lang.Thread.sleep;
//
///**
// * Created by user on 2016-09-27.
// */
//
//public class SocketNetwork extends Thread {
//
//    private String IP;
//    private int PORT;
//    private Socket socket;
//
//    public static boolean runFlag = true;
//
//    private Handler mHandler;
//    private SocketAddress sAddress;
//
//    private InputStream is;
//    private DataInputStream dis;
//    private OutputStream os;
//    private DataOutputStream dos;
//
//    MapVal mv = MapVal.getInstance();
//
//
//    //    private static ToServer ourInstance = null;
////
////    public static ToServer getInstance(String IP, int PORT, Handler mHandler) {
////        if (ourInstance == null) {
////            ourInstance = new ToServer(IP, PORT, mHandler);
////        }
////        return ourInstance;
////    }
//    public SocketNetwork() {
//        runFlag = true;
//        this.IP = NetworkUtil.IP;
//        this.PORT = NetworkUtil.PORT;
//
//        socket = new Socket();
//        sAddress = new InetSocketAddress(this.IP, this.PORT);
//    }
//
//
//    public SocketNetwork(String IP, int PORT) {
//        runFlag = true;
//        this.IP = IP;
//        this.PORT = PORT;
//
//        socket = new Socket();
//        sAddress = new InetSocketAddress(IP, PORT);
//    }
//
//    public SocketNetwork(String IP, int PORT, Handler mHandler) {
//        runFlag = true;
//        this.IP = IP;
//        this.PORT = PORT;
//        this.mHandler = mHandler;
//
//        socket = new Socket();
//        sAddress = new InetSocketAddress(IP, PORT);
//    }
//
//    @SuppressWarnings("null")
//    public synchronized void run() {
//        try {
//            //타임아웃
//            socket.connect(sAddress, HandlerPosition.SERVER_CONNECT_TIMEOUT);
////            socket = new Socket(this.IP, this.PORT);
//
//            is = socket.getInputStream();
//            dis = new DataInputStream(is);
//            os = socket.getOutputStream();
//            dos = new DataOutputStream(os);
//
//            Log.d("[Client]", " Server connected !!");
//        } catch (IOException e) {
//            try {
//                sleep(HandlerPosition.SERVER_CONNECT_TIMEOUT);
//                mHandler.sendEmptyMessage(HandlerPosition.SOCKET_CONNECT_ERROR);
//            } catch (InterruptedException e1) {
//            } catch (NullPointerException e2) {
//            }
//
//        }
//        if (dis != null) {
//            mHandler.sendEmptyMessage(HandlerPosition.SOCKET_CONNECT_SUCCESS);
//            readData();
//        }
//    }
//
//    public void writeData(byte[] data) {
//        if (socket != null && dos != null && data != null) {
//            try {
//                Log.e("[sendData]", "222");
//                mv.setSr_cnt(mv.getSr_cnt() + 1);
//                dos.write(data);
//                Log.e("[sendData]", " send byte Data !!");
//            } catch (IOException e) {
//                mHandler.sendEmptyMessage(HandlerPosition.WRITE_SERVER_DISCONNECT_ERROR);
//                Log.d("[sendData]", " Failed send byte Data !!");
//            }
//        }
//    }
//
//    public void writeData() {
//        if (socket != null && dos != null && Data.writeData != null) {
//            try {
//                Log.e("[sendData]", "222");
//                mv.setSr_cnt(mv.getSr_cnt() + 1);
//                dos.write(Data.writeData);
//                Log.e("[sendData]", " send byte Data !!");
//            } catch (IOException e) {
//                mHandler.sendEmptyMessage(HandlerPosition.WRITE_SERVER_DISCONNECT_ERROR);
//                Log.d("[sendData]", " Failed send byte Data !!");
//            }
//        }
//    }
//
//    public void close() {
//        try {
//            runFlag = false;
//            if (os != null && is != null &&
//                    dos != null && dis != null) {
//                os.close();
//                is.close();
//                dos.close();
//                dis.close();
//            }
//            socket.close();
//        } catch (IOException e) {
//        }
//    }
//
//    private void readData() {
//        while (runFlag) {
//            try {
//                //바이트 크기는 넉넉하게 잡아서 할 것.
//                //가변적으로 못바꾸니 넉넉하게 잡고 알아서 fix 하기
//                byte[] headerData = new byte[BytePosition.HEADER_SIZE];
//                dis.read(headerData);
//                byte[] dataLengthBuf = new byte[4];
//                for (int i = 0; i < 4; i++) {
//                    dataLengthBuf[i] = headerData[i + BytePosition.HEADER_DATALENGTH];
//                }
//                //헤더의 op 검사
//                if (OPUtil.opCheck(headerData[BytePosition.HEADER_OPCODE])) {
//                    int dataLength = Func.byteToInteger(dataLengthBuf, 4);
//                    byte[] bodyData = new byte[dataLength];
//                    dis.read(bodyData);
//
//                    if (bodyData.length > 0) {
//                        //정상적인 데이터 수신
//                        mv.setSr_cnt(mv.getSr_cnt() + 1);
//                        Data.readData = Func.mergyByte(headerData, bodyData);
//                        mHandler.sendEmptyMessage(HandlerPosition.DATA_READ_SUCESS);
//                    } else {
//                        //잘못된 값이 서버에서 들어왔을 때
//                        if (dis.read() == -1) {
//                            //만약 -1이면 서버가 끊어진거임.
//                            runFlag = false;
//                            mHandler.sendEmptyMessage(HandlerPosition.READ_SERVER_DISCONNECT_ERROR);
//                        } else {
//                            mHandler.sendEmptyMessage(HandlerPosition.READ_DATA_ERROR);
//                        }
//                    }
//                } else {
//                    //기존에 저장된 op 아니라면 연결을 끊어버림
//                    runFlag = false;
//                    mHandler.sendEmptyMessage(HandlerPosition.READ_SERVER_DISCONNECT_ERROR);
//                }
//            } catch (IOException e) {
//            }
//
//        }
//    }
//
//    public Socket getSocket() {
//        return socket;
//    }
//
//    public void setSocket(Socket socket) {
//        this.socket = socket;
//    }
//
//    private void readErrorCheck() {
//        try {
//            if (dis.read() == -1) {
//                //서버의 연결이 끊김 = 재연결 시도 메세지를 핸들러에 보냄
//                close();
//                mHandler.sendEmptyMessage(HandlerPosition.READ_SERVER_DISCONNECT_ERROR);
//            }
//        } catch (IOException e) {
//        }
//    }
//
//    public void setHandler(Handler handler) {
//        mHandler = handler;
//    }
//}
