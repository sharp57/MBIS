package neighbor.com.mbis.views.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import neighbor.com.mbis.R;
import neighbor.com.mbis.common.SettingsStore;
import neighbor.com.mbis.common.SocketHanderMessageDfe;
import neighbor.com.mbis.managers.FileManager;
import neighbor.com.mbis.models.form.Form_Header;
import neighbor.com.mbis.models.value.MapVal;
import neighbor.com.mbis.network.BytePosition;
import neighbor.com.mbis.network.RecvDataUtil;
import neighbor.com.mbis.util.Func;
import neighbor.com.mbis.util.Setter;

import neighbor.com.mbis.network.NetworkIntentService;
import neighbor.com.mbis.util.MbisUtil;
import neighbor.com.mbis.views.maputil.Data;
import neighbor.com.mbis.network.OPUtil;
import neighbor.com.mbis.views.maputil.Util;

import static java.lang.String.format;
import static neighbor.com.mbis.common.SocketHanderMessageDfe.ERROR_LOGIN;
import static neighbor.com.mbis.common.SocketHanderMessageDfe.SUCCESS_LOGIN;


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
        noButton.setLongClickable(false);
        busNumButton.setLongClickable(false);
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
                    case MotionEvent.ACTION_UP:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                inputBoxFocus = FOCUS_BUS_NUM_BUTTON;
                                busNumButton.setTextIsSelectable(true);
                                busNumButton.setSelection(busNumButton.length());
                                Logger.getLogger(TAG).error("busNumButton focus:");
                                imm.hideSoftInputFromWindow(noButton.getWindowToken(), 0);
                                imm.hideSoftInputFromWindow(busNumButton.getWindowToken(), 0);
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
                if (noButton.length() == 8 || noButton.length() == 7) {
                    if (busNumButton.length() > 0 && busNumButton.length() < 5) {

                        byte[] op = new byte[]{OPUtil.OP_USER_CERTIFICATION};
                        mv.setDataLength(BytePosition.BODY_USER_CERTIFICATION_SIZE - BytePosition.HEADER_SIZE);
                        h.setOp_code(op);
                        Setter.setHeader();
                        h.setDeviceID(Util.hexStringToByteArray(Util.getDeviceID(this)));
                        byte[] otherBusInfo = makeBodyOtherBusInfo();
                        headerBuf = Util.makeHeader(h, headerBuf);
                        Data.writeData = Func.mergeByte(headerBuf, otherBusInfo);
                        MbisUtil.sendData(handler, SUCCESS_LOGIN,
                                SocketHanderMessageDfe.ERROR_LOGIN);

                    } else {
                        Toast.makeText(getApplicationContext(), "차량번호를 다시 한 번 확인 해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "전화번호를 다시 한 번 확인 해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
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
            Logger.getLogger(TAG).debug("getSelectionStart : " + noButton.getSelectionStart());

            String tempString = noButton.getText().toString();
            StringBuffer stringBuffer = new StringBuffer(tempString);
            if (noButton.getSelectionStart() != noButton.getText().toString().length()) {
                Logger.getLogger(TAG).debug("삽입");
                int selectionStart = noButton.getSelectionStart();
                stringBuffer.insert(selectionStart, key);
                noButton.setText(stringBuffer.toString());
                noButton.setSelection(selectionStart + 1);
            } else {
                Logger.getLogger(TAG).debug("일반 추가");
                noButton.setText(stringBuffer.append(key).toString());
                noButton.setSelection(stringBuffer.toString().length());
            }

        } else {
            Logger.getLogger(TAG).debug("getSelectionStart : " + busNumButton.getSelectionStart());
            Logger.getLogger(TAG).debug("length : " + busNumButton.getText().toString().length());
            String tempString = busNumButton.getText().toString();
            StringBuffer stringBuffer = new StringBuffer(tempString);
            if (busNumButton.getSelectionStart() != busNumButton.getText().toString().length()) {
                Logger.getLogger(TAG).debug("삽입");
                int selectionStart = busNumButton.getSelectionStart();
                stringBuffer.insert(selectionStart, key);
                busNumButton.setText(stringBuffer.toString());
                busNumButton.setSelection(selectionStart + 1);
            } else {
                Logger.getLogger(TAG).debug("일반 추가");
                busNumButton.setText(stringBuffer.append(key).toString());
                busNumButton.setSelection(stringBuffer.toString().length());
            }
        }
    }

    private void setInputDel() {
        if (inputBoxFocus == FOCUS_NO_BUTTON) {
            int selectionStart = noButton.getSelectionStart();
            if (selectionStart == noButton.getText().toString().length()) {
                Logger.getLogger(TAG).debug("일반 삭제");
                if (noButton.getText().toString().length() != 0) {
                    noButton.setText(noButton.getText().toString().substring(0, noButton.getText().toString().length() - 1));
                    noButton.setSelection(noButton.getText().length());
                }
            } else {
                char[] numCharArray = noButton.getText().toString().toCharArray();
                ArrayList<Character> sample = new ArrayList<>();
                for (char aNumCharArray : numCharArray) {
                    sample.add(aNumCharArray);
                }
                if (selectionStart > 0) {
                    sample.remove(selectionStart - 1);
                    String tempString = getStringRepresentation(sample);
                    noButton.setText(tempString);
                    noButton.setSelection(selectionStart - 1);
                }
            }
        } else {
            int selectionStart = busNumButton.getSelectionStart();
            if (selectionStart == busNumButton.getText().toString().length()) {
                busNumButton.setText(busNumButton.getText().toString().substring(0, busNumButton.getText().toString().length() - 1));
                busNumButton.setSelection(busNumButton.getText().length());
            } else {
                char[] numCharArray = busNumButton.getText().toString().toCharArray();
                ArrayList<Character> sample = new ArrayList<>();
                for (char aNumCharArray : numCharArray) {
                    sample.add(aNumCharArray);
                }
                if (selectionStart > 0) {
                    sample.remove(selectionStart - 1);
                    String tempString = getStringRepresentation(sample);
                    busNumButton.setText(tempString);
                    busNumButton.setSelection(selectionStart - 1);
                }
            }
        }
    }

    private String getStringRepresentation(ArrayList<Character> list) {
        StringBuilder builder = new StringBuilder(list.size());
        for (Character ch : list) {
            builder.append(ch);
        }
        return builder.toString();
    }


    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
        if (setting.getBoolean("chk_auto", false) && socketFlag) {
            noButton.setText(setting.getString("ID", ""));
            busNumButton.setText(setting.getString("PW", ""));
        }
    }


    private byte[] makeBodyOtherBusInfo() {
        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);

        String calendarDate = format("%s%s%s",
                format("%02d", cal.get(Calendar.YEAR) - 2000),
                format("%02d", (cal.get(Calendar.MONTH) + 1)),
                format("%02d", cal.get(Calendar.DATE)));
        String time = format("%02d", ((cal.get(Calendar.HOUR_OF_DAY)) + 9)) +
                format("%02d", (cal.get(Calendar.MINUTE))) +
                format("%02d", cal.get(Calendar.SECOND));
        byte[] dt = Util.byteReverse(Func.stringToByte(calendarDate + time));
        byte[] phone = Util.byteReverse(Func.integerToByte(Integer.parseInt(noButton.getText().toString()), 4));
        byte[] bus = Util.byteReverse(Func.integerToByte(Integer.parseInt(busNumButton.getText().toString()), 2));
        byte[] res = Util.byteReverse(Func.integerToByte(mv.getReservation(), 4));

        return Func.mergeByte(Func.mergeByte(dt, phone), Func.mergeByte(bus, res));
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

        return Func.mergeByte(Func.mergeByte(Func.mergeByte(Func.mergeByte(Func.mergeByte(dt, dt2), Func.mergeByte(gpsx, gpsy)), Func.mergeByte(angle, speed)), Func.mergeByte(busnum, route)), dev);
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

//            if (isServiceRunningCheck() == true) {
//                unbindService(mConnection);
//            }
//            startService(new Intent(LoginActivityNew.this, mService.getClass()));
//            bindService(new Intent(LoginActivityNew.this, mService.getClass()), mConnection, Context.BIND_AUTO_CREATE);
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



    // 2017.02.13 // 로그인 버튼 활성화 함수
    private void setAuthButtonEnable(boolean value) {
        authButton.setEnabled(value);
    }

    public void userCertificationSuccess() {
        if (mv.getDeviceID() != 0) {
//            sNetwork.close();

            SettingsStore settingsStore = SettingsStore.getInstance();
            settingsStore.putDeviceId(mv.getDeviceID());
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
            case SUCCESS_LOGIN:
                byte[] bytes = new byte[0];
                try {
                    bytes = serialize(message.obj);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 파일 저장
                RecvDataUtil.saveRecvData(bytes);
                addUtilUserCertificationAfterDeviceIdSend(bytes);
                break;
            case ERROR_LOGIN:

                break;
        }
    }

    private void addUtilUserCertificationAfterDeviceIdSend(byte[] bytes) {
        byte[] deviceID = new byte[8];
        for (int i = 0; i < deviceID.length; i++) {
            deviceID[i] = bytes[i + BytePosition.BODY_USER_CERTIFICATION_AFTER_DEVICEID];
        }

        mv.setDeviceID(Func.byteToLong(deviceID));
        userCertificationSuccess();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }

}
