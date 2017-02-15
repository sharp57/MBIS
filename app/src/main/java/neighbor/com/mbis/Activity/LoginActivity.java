package neighbor.com.mbis.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

import neighbor.com.mbis.Function.FileManager;
import neighbor.com.mbis.Function.Func;
import neighbor.com.mbis.Function.Setter;
import neighbor.com.mbis.MapUtil.BytePosition;
import neighbor.com.mbis.MapUtil.Data;
import neighbor.com.mbis.MapUtil.Form.Form_Header;
import neighbor.com.mbis.MapUtil.HandlerPosition;
import neighbor.com.mbis.MapUtil.MakeFile.MakeRouteFile;
import neighbor.com.mbis.MapUtil.MakeFile.MakeRouteStationFile;
import neighbor.com.mbis.MapUtil.MakeFile.MakeStationFile;
import neighbor.com.mbis.MapUtil.OPUtil;
import neighbor.com.mbis.MapUtil.Receive_OP;
import neighbor.com.mbis.MapUtil.Thread.FTPInfoThread;
import neighbor.com.mbis.MapUtil.Thread.FTPThread;
import neighbor.com.mbis.MapUtil.Thread.SocketReadTimeout;
import neighbor.com.mbis.MapUtil.Util;
import neighbor.com.mbis.MapUtil.Value.MapVal;
import neighbor.com.mbis.Network.NetworkIntentService;
import neighbor.com.mbis.Network.NetworkUtil;
import neighbor.com.mbis.R;

import static java.lang.Thread.sleep;


public class LoginActivity extends AppCompatActivity {

    //통신 변수들
//    TextView recvText;

    final String tag = getClass().toString();
    EditText getPhone;
    EditText getBusNum;
    CheckBox chk_auto;

    FileManager eventFileManager;
//    SocketNetwork sNetwork;

    MapVal mv = MapVal.getInstance();
    Form_Header h = Form_Header.getInstance();
    static byte[] headerBuf = null;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    boolean socketFlag = false;

    public static NetworkIntentService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // CALL_PHONE 권한을 Android OS 에 요청한다.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        mService = new NetworkIntentService(NetworkUtil.IP, NetworkUtil.PORT, mHandler);

        Intent intent = getIntent();
        //그냥 접속
        if (intent.getBooleanExtra("flag", true)) {
            mv.setDeviceID(setting.getLong("deviceID", 0));
            startService(new Intent(LoginActivity.this, mService.getClass()));
            bindService(new Intent(LoginActivity.this, mService.getClass()), mConnection, Context.BIND_AUTO_CREATE);
        }
        //Select 화면에서 로그아웃 버튼 눌렀을 때
        else {
            editor.remove("deviceID");
            editor.remove("chk_auto");
            editor.commit();
            mv.setDeviceID(0);
        }
//        sNetwork = new SocketNetwork(NetworkUtil.IP, NetworkUtil.PORT, mHandler);
//        sNetwork.start();

        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);
        String packetFileName = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE)) + " packet";

        eventFileManager = new FileManager(packetFileName);

//        recvText = (TextView) findViewById(R.id.recvText);
        getPhone = (EditText) findViewById(R.id.phoneNum);
        getBusNum = (EditText) findViewById(R.id.busNum);
        chk_auto = (CheckBox) findViewById(R.id.chk_auto);
        getPhone.setText("12341234");
        getBusNum.setText("5678");

//        findViewById(R.id.zzzzz).performClick();

//        tv = (TextView) findViewById(R.id.textView);

//        Toast.makeText(this, mv.getDeviceID() + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (setting.getBoolean("chk_auto", false)
                && socketFlag
                ) {
            getPhone.setText(setting.getString("ID", ""));
            getBusNum.setText(setting.getString("PW", ""));
            chk_auto.setChecked(true);
//            findViewById(R.id.sendButton).performClick();
//            findViewById(R.id.cheat).performClick();
        }


        if (mv.getDeviceID() != 0) {
            finish();
            startActivity(new Intent(getApplicationContext(), SelectRouteActivity.class));
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerPosition.DATA_READ_SUCESS:
//                    Toast.makeText(getApplicationContext(), "DATA_READ_SUCESS", Toast.LENGTH_SHORT).show();
//                    for(int i=0 ; i<Data.readData.length ; i++) {
//                        recvText.append(String.format("%02d ", Data.readData[i]));
//                        if(i % 10 == 9) {
//                            recvText.append("\n");
//                        }
//                    }

                    recvData(Data.readData[BytePosition.HEADER_OPCODE]);
                    break;

                case HandlerPosition.TIME_CHANGE:
                    break;
                //소켓 연결 성공!
                case HandlerPosition.SOCKET_CONNECT_SUCCESS:
                    socketFlag = true;
//                    Toast.makeText(getApplicationContext(), "SOCKET_CONNECT_SUCCESS", Toast.LENGTH_SHORT).show();
                    retryCountdownTimer();
                    Data.writeData
                            = new byte[]{
                            0x01, 0x11, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x00, 0x00, 0x00, (byte) 0x32
                            , 0x31, 0x36, 0x31, 0x30, 0x32, 0x37, 0x31, 0x34, 0x33, 0x36, 0x30, 0x30
                            , 0x31, 0x36, 0x31, 0x30, 0x32, 0x37, 0x31, 0x34, 0x33, 0x36, 0x30, 0x30
                            , 0x00, 0x39, 0x36, 0x73, 0x00, (byte) 0xC1, (byte) 0xF9, 0x7F, 0x00, 0x00, 0x00, 0x00
                            , 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01
                            , 0x00, 0x00, 0x00, 0x00
                    };
                    try {
                        h = Util.setHeader(h,LoginActivity.this,(byte)0x01,(byte)0x11, new byte[]{0x00, 0x01}, new byte[]{0x01, 0x02}, new byte[]{0x00, 0x00, 0x00, 0x00});
                        Data.writeData = Util.makeHeader(h, headerBuf);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    sendData();
                    break;
                //소켓 연결 실패!
                case HandlerPosition.SOCKET_CONNECT_ERROR:
                    socketFlag = false;
//                    Toast.makeText(getApplicationContext(), "SOCKET_CONNECT_ERROR", Toast.LENGTH_SHORT).show();
                    retryConnection();
                    break;
                //연결 중 서버가 죽었을 때
                case HandlerPosition.READ_SERVER_DISCONNECT_ERROR:
//                    Toast.makeText(getApplicationContext(), "READ_SERVER_DISCONNECT_ERROR", Toast.LENGTH_SHORT).show();
                    retryConnection();
                    break;
                //데이터 전송이 실패했을 때
                case HandlerPosition.WRITE_SERVER_DISCONNECT_ERROR:
//                    Toast.makeText(getApplicationContext(), "WRITE_SERVER_DISCONNECT_ERROR", Toast.LENGTH_SHORT).show();
                    retryConnection();
                    break;
                //잘못된 데이터가 왔을 때
                case HandlerPosition.READ_DATA_ERROR:
//                    Toast.makeText(getApplicationContext(), "READ_DATA_ERROR", Toast.LENGTH_SHORT).show();
                    retryConnection();
                    retryCountdownTimer();
                    break;
                //타임아웃!
                case HandlerPosition.READ_TIMEOUT_ERROR:
//                    Toast.makeText(getApplicationContext(), "READ_TIMEOUT_ERROR", Toast.LENGTH_SHORT).show();
                    retryConnection();
                    retryCountdownTimer();
                    break;
            }
        }
    };

    public void nextPage(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                // test // 2017.02.08 // 읨의로 화면 이동
                startActivity(new Intent(getApplicationContext(), SelectMenuActivity.class));
                finish();
                if(false)   // test // 실 사용시 이 한 라인만 삭제 // 2016.02.08
                if (getPhone.length() == 8 || getPhone.length() == 7) {
                    if (getBusNum.length() > 0 && getBusNum.length() < 5) {

                        byte[] op = new byte[]{0x03};
                        mv.setDataLength(BytePosition.BODY_USER_CERTIFICATION_SIZE - BytePosition.HEADER_SIZE);
                        h.setOp_code(op);
                        Setter.setHeader();
                        byte[] otherBusInfo = makeBodyOtherBusInfo();
                        Util.makeHeader(h, headerBuf);

                        Data.writeData = Func.mergyByte(headerBuf, otherBusInfo);

                        sendData();
                    } else {
                        Toast.makeText(getApplicationContext(), "차량번호를 다시 한 번 확인 해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "전화번호를 다시 한 번 확인 해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cheat:
//                sNetwork.close();
                cTimer.cancel();
                mv.setDeviceID(2567812341234L);
                finish();
                startActivity(new Intent(getApplicationContext(), SelectRouteActivity.class));
                break;
            case R.id.zzzzz:
                // test // 2017.02.08 // 읨의로 화면 이동
                startActivity(new Intent(getApplicationContext(), RunActivity.class));


                Data.writeData
                        //RouteStation
                        = new byte[]{
                        0x01, 0x33, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x00, 0x00, 0x00, (byte) 0xF7
                        , 0x31, 0x36, 0x31, 0x30, 0x32, 0x37, 0x31, 0x34, 0x33, 0x36, 0x30, 0x30
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01
                        , 0x00, 0x31, 0x39, 0x37, 0x2E, 0x31, 0x36, 0x38, 0x2E, 0x31, 0x30, 0x30, 0x2E, 0x30, 0x32, 0x30
                        , 0x00, 0x15
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x61, 0x64, 0x6D, 0x69, 0x6E
                        , 0x00, 0x00, 0x74, 0x65, 0x73, 0x74, 0x31, 0x32, 0x33, 0x34
                        , 0x01
                        //path_sw_app
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                        //path_data
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x68, 0x6F, 0x6D, 0x65, 0x2F
                        //path_upload
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                        //STATION
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x6d, 0x79, 0x5f, 0x73
                        , 0x74, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x2e, 0x63, 0x73, 0x76
                        //ROUTE
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x6d, 0x79
                        , 0x5f, 0x72, 0x6f, 0x75, 0x74, 0x65, 0x2e, 0x63, 0x73, 0x76
                        //ROUTE_STATION
                        , 0x6d, 0x79, 0x5f, 0x72, 0x6f, 0x75, 0x74, 0x65, 0x5f, 0x73
                        , 0x74, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x2e, 0x63, 0x73, 0x76

                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                        , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00


//                        0x01, 0x11, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x00, 0x00, 0x00, (byte) 0x32
//                        , 0x31, 0x36, 0x31, 0x30, 0x32, 0x37, 0x31, 0x34, 0x33, 0x36, 0x30, 0x30
//                        , 0x31, 0x36, 0x31, 0x30, 0x32, 0x37, 0x31, 0x34, 0x33, 0x36, 0x30, 0x30
//                        , 0x00, 0x39, 0x36, 0x73, 0x00, (byte) 0xC1, (byte) 0xF9, 0x7F, 0x00, 0x00, 0x00, 0x00
//                        , 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01
//                        , 0x00, 0x00, 0x00, 0x00


//                        0x01, 0x73, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x00, 0x00, 0x00, (byte) 0xff
//
//                        , 0x31, 0x36, 0x31, 0x30, 0x32, 0x30, 0x31, 0x32, 0x30, 0x30, 0x30, 0x30
//
//                        , 0x00, 0x01
//
//                        , 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xab, (byte) 0xcd, (byte) 0xef
//                        , 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x31, 0x30, 0x30
//
//                        , 0x00, 0x11
//                        //적용일자, 시각
//                        , 0x31, 0x36, 0x31, 0x30, 0x32, 0x37, 0x31, 0x34, 0x33, 0x36, 0x30, 0x30
//
//                        , 0x00, 0x01, 0x00, 0x07, 0x39, (byte) 0x8C, (byte) 0xD9, 0x00, 0x01, 0x00, 0x01
//                        , 0x00, 0x02, 0x00, 0x0D, (byte) 0xD8, (byte) 0xF8, (byte) 0xA0, 0x00, 0x02, 0x00, 0x02
//                        , 0x00, 0x03, 0x00, 0x14, 0x78, 0x64, 0x67, 0x00, 0x03, 0x00, 0x03
//                        , 0x00, 0x04, 0x00, 0x1B, 0x17, (byte) 0xD0, 0x2E, 0x00, 0x04, 0x00, 0x04
//                        , 0x00, 0x05, 0x00, 0x21, (byte) 0xB7, (byte) 0x3B, (byte) 0xF5, 0x00, 0x05, 0x00, 0x05
//                        , 0x00, 0x06, 0x00, 0x28, (byte) 0x56, (byte) 0xA7, (byte) 0xBC, 0x00, 0x06, 0x00, 0x06
//                        , 0x00, 0x01, 0x00, 0x07, 0x39, (byte) 0x8C, (byte) 0xD9, 0x00, 0x01, 0x00, 0x01
//                        , 0x00, 0x02, 0x00, 0x0D, (byte) 0xD8, (byte) 0xF8, (byte) 0xA0, 0x00, 0x02, 0x00, 0x02
//                        , 0x00, 0x03, 0x00, 0x14, 0x78, 0x64, 0x67, 0x00, 0x03, 0x00, 0x03
//                        , 0x00, 0x04, 0x00, 0x1B, 0x17, (byte) 0xD0, 0x2E, 0x00, 0x04, 0x00, 0x04
//                        , 0x00, 0x05, 0x00, 0x21, (byte) 0xB7, (byte) 0x3B, (byte) 0xF5, 0x00, 0x05, 0x00, 0x05
//                        , 0x00, 0x06, 0x00, 0x28, (byte) 0x56, (byte) 0xA7, (byte) 0xBC, 0x00, 0x06, 0x00, 0x06
//                        , 0x00, 0x01, 0x00, 0x07, 0x39, (byte) 0x8C, (byte) 0xD9, 0x00, 0x01, 0x00, 0x01
//                        , 0x00, 0x02, 0x00, 0x0D, (byte) 0xD8, (byte) 0xF8, (byte) 0xA0, 0x00, 0x02, 0x00, 0x02
//                        , 0x00, 0x03, 0x00, 0x14, 0x78, 0x64, 0x67, 0x00, 0x03, 0x00, 0x03
//                        , 0x00, 0x04, 0x00, 0x1B, 0x17, (byte) 0xD0, 0x2E, 0x00, 0x04, 0x00, 0x04
//                        , 0x00, 0x05, 0x00, 0x21, (byte) 0xB7, (byte) 0x3B, (byte) 0xF5, 0x00, 0x05, 0x00, 0x05
//
//                        , 0x00, 0x00, 0x00, 0x00

                };
                sendData();
                Log.e("[sendData]", "333");
//                Toast.makeText(this, "send data", Toast.LENGTH_SHORT).show();
        }
        Log.e("[sendData]", "444");
    }

    private byte[] makeBodyOtherBusInfo() {
        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);

        String date = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE));
        String time = String.format("%02d", ((cal.get(Calendar.HOUR_OF_DAY)) + 9)) + String.format("%02d", (cal.get(Calendar.MINUTE))) + String.format("%02d", cal.get(Calendar.SECOND));
        byte[] dt = Func.stringToByte(date + time);
        byte[] phone = Func.integerToByte(Integer.parseInt(getPhone.getText().toString()), 4);
        byte[] bus = Func.integerToByte(Integer.parseInt(getBusNum.getText().toString()), 2);
        byte[] res = Func.integerToByte(mv.getReservation(), 4);

        return Func.mergyByte(Func.mergyByte(dt, phone), Func.mergyByte(bus, res));
    }

//    private byte[] makeHeader() {
//        headerBuf = new byte[BytePosition.HEADER_SIZE];
//
//        putHeader(h.getVersion(), BytePosition.HEADER_VERSION_START);
//        putHeader(h.getOp_code(), BytePosition.HEADER_OPCODE);
//        putHeader(h.getSr_cnt(), BytePosition.HEADER_SRCNT);
//        putHeader(h.getDeviceID(), BytePosition.HEADER_DEVICEID);
//        putHeader(h.getLocalCode(), BytePosition.HEADER_LOCALCODE);
//        putHeader(h.getDataLength(), BytePosition.HEADER_DATALENGTH);
//
//        return headerBuf;
//    }
//
//    private void putHeader(byte[] b, int position) {
//        System.arraycopy(b, 0, headerBuf, position, b.length);
//    }

    private void retryConnection() {
        cTimer.cancel();
        mService.close();
        mService.stopSelf();
        startService(new Intent(LoginActivity.this, mService.getClass()));
        bindService(new Intent(LoginActivity.this, mService.getClass()), mConnection, Context.BIND_AUTO_CREATE);

//        sNetwork.close();
//        sNetwork = new SocketNetwork(NetworkUtil.IP, NetworkUtil.PORT, mHandler);
//        sNetwork.start();
    }

    private void retryCountdownTimer() {
        cTimer.cancel();
        cTimer.start();
    }

    SocketReadTimeout cTimer = new SocketReadTimeout(HandlerPosition.SERVER_READ_TIMEOUT, mHandler);

    private void sendData() {
        String dd = "";
        for (int j = 0; j < Data.writeData.length; j++) {
            dd = dd + String.format("%02X ", Data.writeData[j]);
        }
        eventFileManager.saveData("\n(" + (mv.getSendYear()-2000) + "." + mv.getSendMonth() + "." + mv.getSendDay() +
                " - " + (mv.getSendHour()+9) + ":" + mv.getSendMin() + ":" + mv.getSendSec() +
                ")\n[SEND:" + Data.writeData.length + "] - " + dd);
        Log.e("[sendData]", "111");
        mService.writeData();
    }

    private synchronized void recvData(byte opCode) {
        String dd = "";
        for (int i = 0; i < Data.readData.length; i++) {
            dd = dd + String.format("%02x ", Data.readData[i]);
//            readText.append(String.format("%02x ", Data.readData[i]));
        }
        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);
        String packetFileName = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE)) + " packet";

        eventFileManager.saveData("\n(" + cal.get(Calendar.YEAR) + ":" + (cal.get(Calendar.MONTH) + 1) + ":" + cal.get(Calendar.DATE) +
                " - " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) +
                ")\n[RECV:" + Data.readData.length + "] - " + dd);
//        tv.append("\n");
        Log.e(tag, "data read success: " + opCode + " / " + String.format("%02x ", Data.readData[1]));
//        new Receive_OP(Data.readData[BytePosition.HEADER_OPCODE]);
        new Receive_OP(opCode);
        synchronized (this) {
            if (opCode == OPUtil.OP_USER_CERTIFICATION_AFTER_DEVICEID_SEND) {
                userCertificationSuccess();
            } else if (opCode == OPUtil.OP_ROUTE_DATA_INFO) {
                new MakeRouteFile();
            } else if (opCode == OPUtil.OP_STATION_DATA_INFO) {
                new MakeStationFile();
            } else if (opCode == OPUtil.OP_ROUTE_STATION_DATA_INFO) {
                new MakeRouteStationFile();
            }
//            else if(Data.readFTPData != null) {
//            }
            else if (opCode == OPUtil.OP_FTP_INFO) {
                FTPInfoThread ftpInfoThread = new FTPInfoThread();
                ftpInfoThread.setPriority(Thread.MAX_PRIORITY);
                ftpInfoThread.start();
            } else if (opCode == OPUtil.OP_CONTROL_INFO) {
                if (Data.readData[BytePosition.BODY_CONTROL_CONTROLCODE] == 0x12) {
                    FTPThread ftpThread = new FTPThread();
                    ftpThread.setPriority(Thread.MIN_PRIORITY);
                    ftpThread.start();
                }
            }
        }
    }


    public void userCertificationSuccess() {
        if (mv.getDeviceID() != 0) {
//            sNetwork.close();

            editor.putLong("deviceID", mv.getDeviceID());
            editor.commit();
            cTimer.cancel();
            finish();
            startActivity(new Intent(getApplicationContext(), SelectRouteActivity.class));
            Toast.makeText(getApplicationContext(), "[인증 성공] from. Server : " + mv.getDeviceID(), Toast.LENGTH_SHORT).show();
        }
    }


    public void onCheckBoxClick(View v) {
        if (chk_auto.isChecked()) {
            String ID = getPhone.getText().toString();
            String PW = getBusNum.getText().toString();

            editor.putString("ID", ID);
            editor.putString("PW", PW);
            editor.putBoolean("chk_auto", true);
            editor.commit();
        } else {
            editor.clear();
            editor.commit();
        }
    }

    //서비스 커넥션 선언.
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            NetworkIntentService.MainServiceBinder binder = (NetworkIntentService.MainServiceBinder) service;
            mService = binder.getService(); //서비스 받아옴
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
//            mService = null;
        }
    };
}
