package neighbor.com.mbis.activity;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.TimeZone;

import neighbor.com.mbis.R;
import neighbor.com.mbis.function.FileManager;
import neighbor.com.mbis.function.Func;
import neighbor.com.mbis.function.Setter;
import neighbor.com.mbis.maputil.BytePosition;
import neighbor.com.mbis.maputil.Data;
import neighbor.com.mbis.maputil.HandlerPosition;
import neighbor.com.mbis.maputil.OPUtil;
import neighbor.com.mbis.maputil.Receive_OP;
import neighbor.com.mbis.maputil.Util;
import neighbor.com.mbis.maputil.form.Form_Header;
import neighbor.com.mbis.maputil.makefile.MakeRouteFile;
import neighbor.com.mbis.maputil.makefile.MakeRouteStationFile;
import neighbor.com.mbis.maputil.makefile.MakeStationFile;
import neighbor.com.mbis.maputil.thread.FTPInfoThread;
import neighbor.com.mbis.maputil.thread.FTPThread;
import neighbor.com.mbis.maputil.value.MapVal;
import neighbor.com.mbis.network.NetworkIntentService;
import neighbor.com.mbis.util.MbisUtil;


/**
 * Created by 권오철 on 2017-02-08.
 */

public class LoginActivityNew extends Activity implements View.OnClickListener, MessageHandler.SmartServiceHandlerInterface {


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

    private Button key01, key02, key03, key04, key05, key06, key07, key08, key09, key10, key11;
    private RadioButton radioButton01, radioButton02;
    private static String TAG = LoginActivityNew.class.getSimpleName();
    private MessageHandler handler = new MessageHandler(this);
    private final int FOCUS_NO_BUTTON = 1;
    private final int FOCUS_BUS_NUM_BUTTON = 2;
    private int inputBoxFocus = FOCUS_NO_BUTTON;
    private InputMethodManager imm;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
//tset
        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // CALL_PHONE 권한을 Android OS 에 요청한다.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }


//        if (mService != null) {
//            mService.close();   // 2017.02.13
//            mService.stopSelf();
//        }
//        mService = new NetworkIntentService(NetworkUtil.IP, NetworkUtil.PORT, mHandler);
//        initConnection();


//        byte[] op = new byte[]{0x03};
//        mv.setDataLength(BytePosition.BODY_USER_CERTIFICATION_SIZE - BytePosition.HEADER_SIZE);
//        h.setOp_code(op);
//
//        Setter.setHeader();
//        h.setDeviceID(Util.hexStringToByteArray(Util.getDeviceID(this)));
//        byte[] otherBusInfo = makeBodyOtherBusInfo();
//        headerBuf = Util.makeHeader(h, headerBuf);
//
//        Data.writeData = Func.mergyByte(headerBuf, otherBusInfo);


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
        Logger.getLogger(TAG).debug("onCreate");
        setInit();
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }


    private void setInit() {
        authButton = (Button) findViewById(R.id.authButton);
        key12 = (ImageView) findViewById(R.id.key12);
        noButton = (EditText) findViewById(R.id.noButton);
        busNumButton = (EditText) findViewById(R.id.busNumButton);
        radioButton01 = (RadioButton) findViewById(R.id.option1);
        radioButton02 = (RadioButton) findViewById(R.id.option2);
        key01 = (Button) findViewById(R.id.key01);
        key02 = (Button) findViewById(R.id.key02);
        key03 = (Button) findViewById(R.id.key03);
        key04 = (Button) findViewById(R.id.key04);
        key05 = (Button) findViewById(R.id.key05);
        key06 = (Button) findViewById(R.id.key06);
        key07 = (Button) findViewById(R.id.key07);
        key08 = (Button) findViewById(R.id.key08);
        key09 = (Button) findViewById(R.id.key09);
        key10 = (Button) findViewById(R.id.key10);
        key11 = (Button) findViewById(R.id.key11);


//        noButton.setText("12341230");
//        busNumButton.setText("5678");

        authButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
        busNumButton.setOnClickListener(this);
        key01.setOnClickListener(this);
        key02.setOnClickListener(this);
        key03.setOnClickListener(this);
        key04.setOnClickListener(this);
        key05.setOnClickListener(this);
        key06.setOnClickListener(this);
        key07.setOnClickListener(this);
        key08.setOnClickListener(this);
        key09.setOnClickListener(this);
        key10.setOnClickListener(this);
        key11.setOnClickListener(this);
        key12.setOnClickListener(this);
//
//        noButton.setInputType(0);
//        busNumButton.setInputType(0);

        // 키보드 hidden
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }


//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
//        );
        noButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                inputBoxFocus = FOCUS_NO_BUTTON;
                                noButton.setTextIsSelectable(true);
                                noButton.setSelection(noButton.length());
                                Logger.getLogger(TAG).error("noButton focus:");
                            }
                        }, 0);
                        break;
                }
                return false;
            }
        });
        busNumButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                inputBoxFocus = FOCUS_BUS_NUM_BUTTON;
                                busNumButton.setTextIsSelectable(true);
                                busNumButton.setSelection(busNumButton.length());
                                Logger.getLogger(TAG).error("busNumButton focus:");
                            }
                        }, 0);

                        break;
                }
                return false;
            }
        });

        radioButton01.setOnClickListener(optionOnClickListener);
        radioButton02.setOnClickListener(optionOnClickListener);
        radioButton01.setChecked(true);


        if (MbisUtil.getPreferencesBoolean(this, "mode")) {
            radioButton02.setChecked(true);
        } else {
            radioButton01.setChecked(true);
        }
    }

    RadioButton.OnClickListener optionOnClickListener
            = new RadioButton.OnClickListener() {

        public void onClick(View v) {
            if (radioButton02.isChecked() == true) {
                MbisUtil.setPreferencesBoolean(LoginActivityNew.this, "mode", true);
            } else {
                MbisUtil.setPreferencesBoolean(LoginActivityNew.this, "mode", false);
            }
//            Toast.makeText(
//                    LoginActivityNew.this,
//                    "Option 1 : " + radioButton01.isChecked() + "\n"
//                            + "Option 2 : " + radioButton02.isChecked() + "\n",
//                    Toast.LENGTH_LONG).show();

        }
    };

    @Override
    public void onClick(View v) {
        imm.hideSoftInputFromWindow(noButton.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(busNumButton.getWindowToken(), 0);


        switch (v.getId()) {
            case R.id.authButton:
//                startActivity(new Intent(LoginActivityNew.this, SelectMenuActivity.class));
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
                        h.setDeviceID(Util.hexStringToByteArray(Util.getDeviceID(this)));
                        byte[] otherBusInfo = makeBodyOtherBusInfo();
                        headerBuf = Util.makeHeader(h, headerBuf);

                        Data.writeData = Func.mergyByte(headerBuf, otherBusInfo);


//                        sendData();


                        MbisUtil.sendData(handler);

//                        SocketConnect socketConnect = new SocketConnect();
//                        socketConnect.setSocket(handler, Data.writeData);
//                        socketConnect.start();

//                        SocketHandlerThread thread = new SocketHandlerThread("obd-engine");
//                        thread.start();
//                        if(thread.getSocket() != null){
//                            Logger.getLogger(TAG).debug("thread.getSocket() != null");
//                        }
//                        SocketLooper socketLooper = new SocketLooper(LoginActivityNew.this, thread
//                                .getLooper(), thread.getSocket());
//                        socketLooper.setData( Data.writeData);


                    } else {
                        Toast.makeText(getApplicationContext(), "차량번호를 다시 한 번 확인 해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "전화번호를 다시 한 번 확인 해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.key01:    // test booting info
//
//                try {
//                    h = Util.setHeader(h, LoginActivityNew.this, (byte) 0x02, (byte) 0x11, new byte[]{0x00, 0x01}, new byte[]{0x01, 0x02}, new byte[]{0x00, 0x00, 0x00});
//                    Data.writeData = Util.makeHeader(h, headerBuf);
//
//                    // 0x11
//                    byte[] op = new byte[]{0x11};
//                    mv.setDataLength(BytePosition.BODY_BOOT_INFO_SIZE - BytePosition.HEADER_SIZE);
//                    h.setOp_code(op);
//                    Setter.setHeader();
//                    h.setDeviceID(Util.hexStringToByteArray(Util.getDeviceID(LoginActivityNew.this)));
//                    byte[] otherBusInfo = makeBodyBusBootingInfo();
//                    headerBuf = Util.makeHeader(h, headerBuf);
//
//                    Data.writeData = Func.mergyByte(headerBuf, otherBusInfo);
//                    MbisUtil.sendData(handler);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
////                sendData();
//                break;
//            case R.id.key02:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            FTPManager ftpManager = new FTPManager("211.189.132.192", 30300, "", "");
//                            ftpManager.connect();
//                            boolean isLogin = ftpManager.login();
//                            Logger.getLogger(TAG).error("isLogin: " + isLogin);
//                            FTPFile[] files = ftpManager.list();
//                            for (int i = 0; i < files.length; i++) {
//                                Logger.getLogger(TAG).error("files: " + files[i].getName());
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//
//                break;
            case R.id.key01:
                setInputKey("1");
                break;
            case R.id.key02:
                setInputKey("2");
                break;
            case R.id.key03:
                setInputKey("3");
                break;
            case R.id.key04:
                setInputKey("4");
                break;
            case R.id.key05:
                setInputKey("5");
                break;
            case R.id.key06:
                setInputKey("6");
                break;
            case R.id.key07:
                setInputKey("7");
                break;
            case R.id.key08:
                setInputKey("8");
                break;
            case R.id.key09:
                setInputKey("9");
                break;
            case R.id.key10:
                setInputKey("0");
                break;
            case R.id.key11:
                setInputKey("-");
                break;
            case R.id.key12:
                setInputDel();
                break;
            case R.id.noButton:

                break;
            case R.id.busNumButton:

                break;
        }
    }

    private void setInputKey(String key) {

        Logger.getLogger(TAG).error("setInputKey focus: " + inputBoxFocus);
        if (inputBoxFocus == FOCUS_NO_BUTTON) {
            noButton.setText(noButton.getText().toString() + key);
            noButton.setSelection(noButton.getText().toString().length());
        } else {
            busNumButton.setText(busNumButton.getText().toString() + key);
            busNumButton.setSelection(busNumButton.getText().toString().length());
        }
    }

    private void setInputDel() {
        if (inputBoxFocus == FOCUS_NO_BUTTON) {
            if (noButton.length() > 0) {
                noButton.setText(noButton.getText().toString().substring(0, noButton.getText().toString().length() - 1));
            }
        } else {
            if (busNumButton.length() > 0) {
                busNumButton.setText(busNumButton.getText().toString().substring(0, busNumButton.getText().toString().length() - 1));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(mConnection);

        // 2017.02.14
        if (mService != null) {
            mService.close();
            mService.stopSelf();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.

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
                        h = Util.setHeader(h, LoginActivityNew.this, (byte) 0x02, (byte) 0x11, new byte[]{0x00, 0x01}, new byte[]{0x01, 0x02}, new byte[]{0x00, 0x00, 0x00});
                        Data.writeData = Util.makeHeader(h, headerBuf);

//                        // 0x11

                        byte[] op = new byte[]{0x11};
                        mv.setDataLength(BytePosition.BODY_BOOT_INFO_SIZE - BytePosition.HEADER_SIZE);
                        h.setOp_code(op);
                        Setter.setHeader();
                        h.setDeviceID(Util.hexStringToByteArray(Util.getDeviceID(LoginActivityNew.this)));
                        byte[] otherBusInfo = makeBodyBusBootingInfo();
                        headerBuf = Util.makeHeader(h, headerBuf);

                        Data.writeData = Func.mergyByte(headerBuf, otherBusInfo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    sendData();
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
        byte[] dt = Util.byteReverse(Func.stringToByte(date + time));
        byte[] phone = Util.byteReverse(Func.integerToByte(Integer.parseInt(noButton.getText().toString()), 4));
        byte[] bus = Util.byteReverse(Func.integerToByte(Integer.parseInt(busNumButton.getText().toString()), 2));
        byte[] res = Util.byteReverse(Func.integerToByte(mv.getReservation(), 4));

        return Func.mergyByte(Func.mergyByte(dt, phone), Func.mergyByte(bus, res));
    }

    private byte[] makeBodyBusBootingInfo() {
        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);

        String date = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE));
        String time = String.format("%02d", ((cal.get(Calendar.HOUR_OF_DAY)) + 9)) + String.format("%02d", (cal.get(Calendar.MINUTE))) + String.format("%02d", cal.get(Calendar.SECOND));
        byte[] dt = Util.byteReverse(Func.stringToByte(date + time));
        byte[] dt2 = Util.byteReverse(Func.stringToByte(date + time));
        byte[] gpsx = Util.byteReverse(Func.longToByte(3712345678l, 4));
        byte[] gpsy = Util.byteReverse(Func.longToByte(12812345678l, 4));
        byte[] angle = Util.byteReverse(Func.integerToByte(90, 2));
        byte[] speed = Util.byteReverse(Func.integerToByte(50, 2));
        byte[] busnum = Util.byteReverse(Func.integerToByte(1, 2));
        byte[] route = Util.byteReverse(Func.integerToByte(2, 2));
        byte[] dev = Util.byteReverse(Func.integerToByte(3, 4));

//        byte[] dt = Func.stringToByte(date + time);
//        byte[] phone = Func.integerToByte(Integer.parseInt(noButton.getText().toString()), 4);
//        byte[] bus = Func.integerToByte(Integer.parseInt(busNumButton.getText().toString()), 2);
//        byte[] res = Func.integerToByte(mv.getReservation(), 4);

        return Func.mergyByte(Func.mergyByte(Func.mergyByte(Func.mergyByte(Func.mergyByte(dt, dt2), Func.mergyByte(gpsx, gpsy)), Func.mergyByte(angle, speed)), Func.mergyByte(busnum, route)), dev);
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
        if (mService != null) {
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

        if (mService != null) {
//            mService.close();   // 2017.02.10
//            mService.stopSelf();
            Logger.getLogger(TAG).debug("mService != null");
            if (isServiceRunningCheck() == true) {
                unbindService(mConnection);
            }
            Intent intent = new Intent(LoginActivityNew.this, mService.getClass());
            startService(intent);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("neighbor.com.mbis.NetworkIntentService".equals(service.service.getClassName())) {
                MbisUtil.log(this, "isServiceRunningCheck is true..");
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
        eventFileManager.saveData("\n(" + (mv.getSendYear() - 2000) + "." + mv.getSendMonth() + "." + mv.getSendDay() +
                " - " + (mv.getSendHour() + 9) + ":" + mv.getSendMin() + ":" + mv.getSendSec() +
                ")\n[SEND:" + Data.writeData.length + "] - " + dd);
        Log.e("[sendData]", "111");
//        mService.writeData();
    }

//    private void sendData() {
//        String dd = "";
//        for (int j = 0; j < Data.writeData.length; j++) {
//            dd = dd + String.format("%02X ", Data.writeData[j]);
//        }
//
//    }

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
            } else if (opCode == OPUtil.OP_ACK) {    // 2017.02.13   // 로그인 송신이 되었다고 ack가 오면 버튼 활성화
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
    private void setAuthButtonEnable(boolean value) {
        authButton.setEnabled(value);
    }

    public void userCertificationSuccess() {
        if (mv.getDeviceID() != 0) {
//            sNetwork.close();

            editor.putLong("deviceID", mv.getDeviceID());
            editor.commit();
//            cTimer.cancel();
//            finish();
//            startActivity(new Intent(getApplicationContext(), SelectRouteActivity.class));

            startActivity(new Intent(getApplicationContext(), SelectMenuActivity.class));
            Toast.makeText(getApplicationContext(), "[인증 성공] from. Server : " + Func.byteToLong(Util.byteReverse(Func.longToByte(mv.getDeviceID(), 8))), Toast.LENGTH_SHORT).show();
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
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            NetworkIntentService.MainServiceBinder binder = (NetworkIntentService.MainServiceBinder) service;
            mService = binder.getService(); //서비스 받아옴
        }

        // Called when the connection with the service disconnects unexpectedly
        @Override
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };


    @Override
    public void handleServiceMessage(Message message) {

        switch (message.what) {
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
                    h = Util.setHeader(h, LoginActivityNew.this, (byte) 0x02, (byte) 0x11, new byte[]{0x00, 0x01}, new byte[]{0x01, 0x02}, new byte[]{0x00, 0x00, 0x00});
                    Data.writeData = Util.makeHeader(h, headerBuf);

//                        // 0x11

                    byte[] op = new byte[]{0x11};
                    mv.setDataLength(BytePosition.BODY_BOOT_INFO_SIZE - BytePosition.HEADER_SIZE);
                    h.setOp_code(op);
                    Setter.setHeader();
                    h.setDeviceID(Util.hexStringToByteArray(Util.getDeviceID(LoginActivityNew.this)));
                    byte[] otherBusInfo = makeBodyBusBootingInfo();
                    headerBuf = Util.makeHeader(h, headerBuf);

                    Data.writeData = Func.mergyByte(headerBuf, otherBusInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                    sendData();
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


}
