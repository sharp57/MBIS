package neighbor.com.mbis.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ForkJoinPool;

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
import neighbor.com.mbis.Util.MbisUtil;

/**
 * Created by 권오철 on 2017-02-08.
 */

public class LoginActivityNew extends Activity implements View.OnClickListener{


    //통신 변수들

    final String tag = getClass().toString();
    EditText noButton;
    EditText busNumButton;

    FileManager eventFileManager;
    MapVal mv = MapVal.getInstance();
    Form_Header h = Form_Header.getInstance();
    static byte[] headerBuf = null;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    boolean socketFlag = false;

    public static NetworkIntentService mService;


    private Button authButton;
    private ImageView key12;
    private ToggleButton switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // CALL_PHONE 권한을 Android OS 에 요청한다.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        if(mService != null) {
            mService.close();   // 2017.02.13
            mService.stopSelf();
        }
        mService = new NetworkIntentService(NetworkUtil.IP, NetworkUtil.PORT, mHandler);

        initConnection();

        Intent intent = getIntent();
        //그냥 접속
        if (intent.getBooleanExtra("flag", true)) {
            mv.setDeviceID(setting.getLong("deviceID", 0));
//            startService(new Intent(LoginActivityNew.this, mService.getClass()));
//            bindService(new Intent(LoginActivityNew.this, mService.getClass()), mConnection, Context.BIND_AUTO_CREATE);
        }
        //Select 화면에서 로그아웃 버튼 눌렀을 때
        else {
            editor.remove("deviceID");
            editor.remove("chk_auto");
            editor.commit();
            mv.setDeviceID(0);
        }

        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);
        String packetFileName = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE)) + " packet";

        eventFileManager = new FileManager(packetFileName);

        noButton = (EditText) findViewById(R.id.noButton);
        busNumButton = (EditText) findViewById(R.id.busNumButton);

        noButton.setText("12341234");
        busNumButton.setText("5678");


        setInit();
    }
    private void setInit(){
        authButton = (Button) findViewById(R.id.authButton);
        key12 = (ImageView) findViewById(R.id.key12);
        switchButton = (ToggleButton) findViewById(R.id.switchButton);

        authButton.setOnClickListener(this);
        key12.setOnClickListener(this);

        if( MbisUtil.getPreferences(this, "mode")){
            switchButton.setChecked(true);
        }else{
            switchButton.setChecked(false);
        }
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    MbisUtil.setPreferences(LoginActivityNew.this,"mode",true);
                }else{
                    MbisUtil.setPreferences(LoginActivityNew.this,"mode",false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.authButton:
                startActivity(new Intent(LoginActivityNew.this, SelectMenuActivity.class));
//                if(mService.getSocket()  == null){
//                    return;
//                }
//                if(mService.getSocket().isConnected() == false){
//                    return;
//                }
                if (noButton.length() == 8 || noButton.length() == 7) {
                    if (busNumButton.length() > 0 && busNumButton.length() < 5) {

//                        setAuthButtonEnable(false);
                        //retryConnection();

                        byte[] op = new byte[]{0x03};
                        mv.setDataLength(BytePosition.BODY_USER_CERTIFICATION_SIZE - BytePosition.HEADER_SIZE);
                        h.setOp_code(op);
                        Setter.setHeader();
                        byte[] otherBusInfo = makeBodyOtherBusInfo();
                        headerBuf = Util.makeHeader(h, headerBuf);

                        Data.writeData = Func.mergyByte(headerBuf, otherBusInfo);

                        sendData();
                    } else {
                        Toast.makeText(getApplicationContext(), "차량번호를 다시 한 번 확인 해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "전화번호를 다시 한 번 확인 해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.key12:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);

        // 2017.02.14
        if(mService != null) {
            mService.close();
            mService.stopSelf();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (setting.getBoolean("chk_auto", false)
                && socketFlag
                ) {
            noButton.setText(setting.getString("ID", ""));
            busNumButton.setText(setting.getString("PW", ""));

        }


//        if (mv.getDeviceID() != 0) {
//            finish();
//            startActivity(new Intent(getApplicationContext(), SelectMenuActivity.class));
//        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerPosition.DATA_READ_SUCESS:

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
                        h = Util.setHeader(h,LoginActivityNew.this,(byte)0x01,(byte)0x11, new byte[]{0x00, 0x01}, new byte[]{0x01, 0x02}, new byte[]{0x00, 0x00, 0x00});
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

    private byte[] makeBodyOtherBusInfo() {
        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);

        String date = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE));
        String time = String.format("%02d", ((cal.get(Calendar.HOUR_OF_DAY)) + 9)) + String.format("%02d", (cal.get(Calendar.MINUTE))) + String.format("%02d", cal.get(Calendar.SECOND));
        byte[] dt = Func.stringToByte(date + time);
        byte[] phone = Func.integerToByte(Integer.parseInt(noButton.getText().toString()), 4);
        byte[] bus = Func.integerToByte(Integer.parseInt(busNumButton.getText().toString()), 2);
        byte[] res = Func.integerToByte(mv.getReservation(), 4);

        return Func.mergyByte(Func.mergyByte(dt, phone), Func.mergyByte(bus, res));
    }


//    private void retryConnection() {
////        cTimer.cancel();
//        mService.close();
//        mService.stopSelf();
//        startService(new Intent(LoginActivityNew.this, mService.getClass()));
//        bindService(new Intent(LoginActivityNew.this, mService.getClass()), mConnection, Context.BIND_AUTO_CREATE);
//
////        sNetwork.close();
////        sNetwork = new SocketNetwork(NetworkUtil.IP, NetworkUtil.PORT, mHandler);
////        sNetwork.start();
//    }
    private void retryConnection() {
//        cTimer.cancel();
        if(mService != null) {
            mService.close();   // 2017.02.10
            mService.stopSelf();
//            mService = new NetworkIntentService(NetworkUtil.IP, NetworkUtil.PORT, mHandler);

            if (isServiceRunningCheck() == true) {
                unbindService(mConnection);
            }
            startService(new Intent(LoginActivityNew.this, mService.getClass()));
            bindService(new Intent(LoginActivityNew.this, mService.getClass()), mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    private void initConnection() {

        if(mService != null) {
//            mService.close();   // 2017.02.10
//            mService.stopSelf();

            if (isServiceRunningCheck() == true) {
                unbindService(mConnection);
            }
            startService(new Intent(LoginActivityNew.this, mService.getClass()));
            bindService(new Intent(LoginActivityNew.this, mService.getClass()), mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("neighbor.com.mbis.NetworkIntentService".equals(service.service.getClassName())) {
                MbisUtil.log(this,"isServiceRunningCheck is true..");
                return true;
            }
        }
        return false;
    }


    private void retryCountdownTimer() {
//        cTimer.cancel();
//        cTimer.start();
    }

//    SocketReadTimeout cTimer = new SocketReadTimeout(HandlerPosition.SERVER_READ_TIMEOUT, mHandler);

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
            } else if(opCode == OPUtil.OP_ACK) {    // 2017.02.13   // 로그인 송신이 되었다고 ack가 오면 버튼 활성화
                setAuthButtonEnable(true);
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

    // 2017.02.13 // 로그인 버튼 활성화 함수
    private void setAuthButtonEnable(boolean value){
        authButton.setEnabled(value);
    }

    public void userCertificationSuccess() {
        if (mv.getDeviceID() != 0) {
//            sNetwork.close();

            editor.putLong("deviceID", mv.getDeviceID());
            editor.commit();
//            cTimer.cancel();
            finish();
//            startActivity(new Intent(getApplicationContext(), SelectRouteActivity.class));
            startActivity(new Intent(getApplicationContext(), SelectMenuActivity.class));
            Toast.makeText(getApplicationContext(), "[인증 성공] from. Server : " + mv.getDeviceID(), Toast.LENGTH_SHORT).show();
            finish();   // 2017.02.13
        }
    }


//    public void onCheckBoxClick(View v) {
//        if (chk_auto.isChecked()) {
//            String ID = noButton.getText().toString();
//            String PW = busNumButton.getText().toString();
//
//            editor.putString("ID", ID);
//            editor.putString("PW", PW);
//            editor.putBoolean("chk_auto", true);
//            editor.commit();
//        } else {
//            editor.clear();
//            editor.commit();
//        }
//    }

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
