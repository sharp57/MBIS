package neighbor.com.mbis.views.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.LocationSource;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import neighbor.com.mbis.R;
import neighbor.com.mbis.common.ConnectInfo;
import neighbor.com.mbis.util.Func;
import neighbor.com.mbis.util.Setter;
import neighbor.com.mbis.managers.DBManager;
import neighbor.com.mbis.managers.FTPManager;
import neighbor.com.mbis.managers.FileManager;
import neighbor.com.mbis.models.form.Form_Header;
import neighbor.com.mbis.models.value.MapVal;
import neighbor.com.mbis.network.BytePosition;
import neighbor.com.mbis.util.MbisUtil;
import neighbor.com.mbis.util.csv.RouteStationUtil;
import neighbor.com.mbis.util.csv.RouteUtil;
import neighbor.com.mbis.util.csv.StationUtil;
import neighbor.com.mbis.views.maputil.Data;
import neighbor.com.mbis.views.maputil.LocationWrapper;
import neighbor.com.mbis.network.Receive_OP;
import neighbor.com.mbis.views.maputil.Util;

import static neighbor.com.mbis.common.SocketHanderMessageDfe.ERROR_BOOTING;

import static neighbor.com.mbis.common.SocketHanderMessageDfe.ERROR_READ_SERVER_DISCONNECT;
import static neighbor.com.mbis.common.SocketHanderMessageDfe.SUCCESS_BOOTING;

public class StartScreen extends AppCompatActivity implements MessageHandler.SmartServiceHandlerInterface, LocationSource.OnLocationChangedListener {

    final String TAG = getClass().toString();
    SharedPreferences pref;
    private static final String MY_DB = "my_db";
    private static String HasVisited = "hasVisited";
    private DBManager db;
    private boolean isStart = true;
    MapVal mv = MapVal.getInstance();
    Form_Header h = Form_Header.getInstance();
    static byte[] headerBuf = null;
    private MessageHandler handler = new MessageHandler(this);
    private ProgressBar pb;
    public final int MY_PERMISSIONS_REQUEST = 100;
    private LocationWrapper locationWrapper;
    FileManager eventFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // CALL_PHONE 권한을 Android OS 에 요청한다.
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//        }
//
//        Handler h = new Handler() {
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                startActivity(new Intent(StartScreen.this, LoginActivityNew.class));
//                finish();
//            }
//        };
//        h.sendEmptyMessageDelayed(0, 2000);
       /* h.sendEmptyMessageDelayed(0, 2500);*/
//        setFTPInit();


        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);
        String packetFileName = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE)) + " packet";
        eventFileManager = new FileManager(packetFileName);

        pb = (ProgressBar) findViewById(R.id.progressbar);
        checkGpsService();

        //setFTPInit();;
    }

    private boolean checkGpsService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Logger.getLogger(TAG).error("checkGpsService: " + true);
                // CALL_PHONE 권한을 Android OS 에 요청한다.
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
            } else {
                Logger.getLogger(TAG).error("checkGpsService: " + false);
                setLog();

            }
        }

        String gps = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        Log.d(gps, "check GPS Page");

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("GPS 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n" +
                    "GPS 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "GPS를 켜야 사용 가능합니다.", Toast.LENGTH_SHORT);
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLog();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setInitBooting(Location location) {
        // 0x11
        try {
            byte[] op = new byte[]{0x11};
            mv.setDataLength(BytePosition.BODY_BOOT_INFO_SIZE - BytePosition.HEADER_SIZE);
            h.setOp_code(op);
            Setter.setHeader();
            h.setDeviceID(Util.hexStringToByteArray(Util.getDeviceID(StartScreen.this)));
            byte[] otherBusInfo = makeBodyBusBootingInfo(location);
            headerBuf = Util.makeHeader(h, headerBuf);

            Data.writeData = Func.mergeByte(headerBuf, otherBusInfo);
            MbisUtil.sendData(handler, SUCCESS_BOOTING,
                    ERROR_BOOTING);


            // 0x11 respsonse 일때 아래 함수 호출하기로.. 지금은 테스트
            setFTPInit();
//            startActivity(new Intent(StartScreen.this, LoginActivityNew.class));
//            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] makeBodyBusBootingInfo(Location location) {

        Logger.getLogger(TAG).error("location: " + location.getLatitude() + " / " + location.getLongitude());

        long lat = (long) (location.getLatitude() * 1000000);
        long lon = (long) (location.getLongitude() * 1000000);


        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);

        String date = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE));
        String time = String.format("%02d", ((cal.get(Calendar.HOUR_OF_DAY)) + 9)) + String.format("%02d", (cal.get(Calendar.MINUTE))) + String.format("%02d", cal.get(Calendar.SECOND));
        byte[] dt = Util.byteReverse(Func.stringToByte(date + time));
        byte[] dt2 = Util.byteReverse(Func.stringToByte(date + time));
        byte[] gpsx = Util.byteReverse(Func.longToByte(lat, 4));
        byte[] gpsy = Util.byteReverse(Func.longToByte(lon, 4));
        byte[] route = Util.byteReverse(Func.integerToByte(MbisUtil.getPreferencesInt(StartScreen.this, MbisUtil.version_route), 2));
        byte[] routestop = Util.byteReverse(Func.integerToByte(MbisUtil.getPreferencesInt(StartScreen.this, MbisUtil.version_routestop), 2));
        byte[] node = Util.byteReverse(Func.integerToByte(MbisUtil.getPreferencesInt(StartScreen.this, MbisUtil.version_node), 2));
        byte[] dev = Util.byteReverse(Func.integerToByte(3, 4));

        return Func.mergeByte(Func.mergeByte(Func.mergeByte(Func.mergeByte(dt, dt2), Func.mergeByte(gpsx, gpsy)), Func.mergeByte(route, routestop)), Func.mergeByte(node, dev));
    }


    public void setLog() {


        locationWrapper = new LocationWrapper(this);
        locationWrapper.setAccuracyFilterEnabled(true, 1000);
        locationWrapper.registerOnLocationChangedListener(this);
        locationWrapper.requestUpdates();

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final GpsStatus gpsStatus = locationManager.getGpsStatus(null);

        // GPS 프로바이더 사용가능여부
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("Main", "isGPSEnabled=" + isGPSEnabled);
        Log.d("Main", "isNetworkEnabled=" + isNetworkEnabled);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                Logger.getLogger(TAG).error("onLocationChanged: ");
                if (isStart == true) {
                    isStart = false;
                    setInitBooting(location);
                }
            }


            public void onStatusChanged(String provider, int status, Bundle extras) {
                //위치공급자의 상태가 바뀔 때 호출
                //켬 -> 끔 or 끔 -> 켬
                Toast.makeText(getApplicationContext(), "GPS 상태 변환", Toast.LENGTH_SHORT).show();
            }

            public void onProviderEnabled(String provider) {
                //위치 공급자가 사용 가능해 질 때 호출
                //즉 GPS를 켜면 호출됨
//                Toast.makeText(getApplicationContext(), "GPS on", Toast.LENGTH_SHORT).show();
            }

            public void onProviderDisabled(String provider) {
                //위치 공급자가 사용 불가능해질(disabled) 때 호출
                //GPS 꺼지면 여기서 예외처리 해주면 됨
//                Toast.makeText(getApplicationContext(), "GPS off", Toast.LENGTH_SHORT).show();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
//        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 0, locationListener);
    }

    private void setFTPInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FTPManager ftpManager = new FTPManager(ConnectInfo.FTP_IP, ConnectInfo.FTP_PORT, ConnectInfo.FTP_ID, ConnectInfo.FTP_PW);

                    ftpManager.connect();
                    ftpManager.checkMkDir();
                    boolean isLogin = ftpManager.login();
                    Logger.getLogger(TAG).error("isLogin: " + isLogin);
                    FTPFile[] files = ftpManager.list();

                    db = DBManager.getInstance(StartScreen.this);

                    for (int i = 0; i < files.length; i++) {
                        Logger.getLogger(TAG).error("files: " + files[i].getName());
                        String[] fileName = files[i].getName().split("_");
                        fileName[1] = fileName[1].replace(".csv", "");

                        if (fileName[0].equals("route")) {
                            Logger.getLogger(TAG).error("fileName route: " + MbisUtil.getPreferencesInt(StartScreen.this, MbisUtil.version_route) + " vs " + Integer.parseInt(fileName[1]) + " / " + files[i].getName());
                            if (MbisUtil.getPreferencesInt(StartScreen.this, MbisUtil.version_route) < Integer.parseInt(fileName[1])) {
                                ftpManager.get(ConnectInfo.FILE_PATH + ConnectInfo.FILE_PATH_2 + files[i].getName(), files[i].getName());
                                Logger.getLogger(TAG).error("fileName route down");
                                checkData(files[i].getName());
                            }
                        } else if (fileName[0].equals("routestop")) {
                            Logger.getLogger(TAG).error("fileName routestop: " + MbisUtil.getPreferencesInt(StartScreen.this, MbisUtil.version_routestop) + " vs " + Integer.parseInt(fileName[1]) + " / " + files[i].getName());
                            if (MbisUtil.getPreferencesInt(StartScreen.this, MbisUtil.version_routestop) < Integer.parseInt(fileName[1])) {
                                ftpManager.get(ConnectInfo.FILE_PATH + ConnectInfo.FILE_PATH_2 + files[i].getName(), files[i].getName());
                                checkData(files[i].getName());
                            }
                        } else if (fileName[0].equals("node")) {
                            Logger.getLogger(TAG).error("fileName node: " + MbisUtil.getPreferencesInt(StartScreen.this, MbisUtil.version_node) + " vs " + Integer.parseInt(fileName[1]) + " / " + files[i].getName());
                            if (MbisUtil.getPreferencesInt(StartScreen.this, MbisUtil.version_node) < Integer.parseInt(fileName[1])) {
                                ftpManager.get(ConnectInfo.FILE_PATH + ConnectInfo.FILE_PATH_2 + files[i].getName(), files[i].getName());
                                checkData(files[i].getName());
                            }
                        }
                    }
//                    for(int i = 0; i < files.length; i++){
//                        Logger.getLogger(TAG).error("files: " + files[i].getName());
//                        ftpManager.get(NetworkUtil.FILE_PATH + NetworkUtil.FILE_PATH_2 + files[i].getName(), files[i].getName());
//                    }


//                    checkData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Util.sqliteExport(StartScreen.this);
                            pb.setVisibility(View.GONE);
                            startActivity(new Intent(StartScreen.this, LoginActivityNew.class));
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private void checkData(String fileName) {
        ArrayList<File> csvFiles = new ArrayList<File>();

//        File f = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + "/data");
        File f = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + ConnectInfo.FILE_PATH_2);
        if (!f.exists()) {
            return;
        }

        Logger.getLogger(TAG).error("fileName route checkData");
        File[] allFiles = f.listFiles();

        if (allFiles != null) {
            for (File file : allFiles) {
                String[] f_name = file.getName().split("_");
                String[] f_name2 = fileName.split("_");
                Logger.getLogger(TAG).error("fileName route f_name[0] : " + f_name[0] + " / " + f_name2[0]);
                if (f_name[0].equals(f_name2[0])) {
//                    csvFiles.add(file);
                    Logger.getLogger(TAG).error("fileName route overwriteDB");
                    overwriteDB(file);
                }
            }
        }
    }

    public void overwriteDB(File file) {

        try {
//            FileReader reader = new FileReader(file, "UTF8");
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "euc-kr");
            BufferedReader in = new BufferedReader(reader);

            String[] fileName = file.getName().split("_");

            String version = fileName[1].replace(".csv", "");

            //db 삭제하기 전에 백업하기.
            backupDB();

            if (fileName[0].equals("route")) {
                Logger.getLogger(TAG).error("insert route");
                try {
                    db.deleteRoute(null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                write_R(in);
                MbisUtil.setPreferencesInt(this, MbisUtil.version_route, Integer.parseInt(version));
            } else if (fileName[0].equals("node")) {
                Logger.getLogger(TAG).error("insert node");
                try {
                    db.deleteStation(null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                write_S(in);
                MbisUtil.setPreferencesInt(this, MbisUtil.version_node, Integer.parseInt(version));
            } else if (fileName[0].equals("routestop")) {
                Logger.getLogger(TAG).error("insert routestop");
                try {
                    db.deleteRouteStation(null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                write_RS(in);
                MbisUtil.setPreferencesInt(this, MbisUtil.version_routestop, Integer.parseInt(version));
            }

            file.delete();    // 이 주석 풀면 다운로드후 scv 파일 삭제

//            recreate();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Util.sqliteExport(StartScreen.this);
//                }
//            });
//            startActivity(new Intent(StartScreen.this, LoginActivityNew.class));
//            finish();

        } catch (Exception e) {
//            Toast.makeText(this, "Fail ToT", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void backupDB() {
        Cursor cr = null;
        Cursor cs = null;
        Cursor crs = null;
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write_R(BufferedReader in) {

        String line = "";
        try {
            db.dbBeginTransaction();
            while ((line = in.readLine()) != null) {

                //Split to separate the name from the capital
                String[] rowData = line.split(",");

                //Create a State object for this row's data.
                RouteUtil r = new RouteUtil();
                r.setRoute_id(rowData[0]);
                r.setRoute_name(rowData[1]);
                r.setRoute_form(Integer.parseInt(rowData[2]));
                r.setRoute_type(Integer.parseInt(rowData[3]));
                r.setRoute_brt_type(Integer.parseInt(rowData[4]));
                r.setRoute_first_start_time(Integer.parseInt(rowData[5]));
                r.setRoute_last_start_time(Integer.parseInt(rowData[6]));
                r.setRoute_average_interval(Integer.parseInt(rowData[7]));
                r.setRoute_average_time(Integer.parseInt(rowData[8]));
                r.setRoute_length(Float.parseFloat(rowData[9]));
                r.setRoute_station_num(Integer.parseInt(rowData[10]));
                r.setRoute_start_station(rowData[11]);
                r.setRoute_important_station1(rowData[12]);
                r.setRoute_important_station2(rowData[13]);
                r.setRoute_last_station(rowData[14]);

                addRouteUtil(r);
            }
            db.dbTransactionSuccessful();
//            Toast.makeText(this, "R ok", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(TAG).error("데이터 다운로드 오류로 앱을 종료합니다");
            finish();
        } finally {
            db.dbEndTransaction();
        }
    }

    private void write_S(BufferedReader in) {
        String line;

        //Read each line
        try {
            db.beginTransaction();
            while ((line = in.readLine()) != null) {

                //Split to separate the name from the capital
                String[] rowData = line.split(",");

                //Create a State object for this row's data.
                StationUtil s = new StationUtil();
                s.setStation_id(rowData[0]);
                s.setStation_name(rowData[1]);
                s.setStation_type(Integer.parseInt(rowData[2]));
                s.setStation_angle(Integer.parseInt(rowData[3]));
                s.setStation_x(rowData[4]);
                s.setStation_y(rowData[5]);
                s.setStation_arrive_distance(Integer.parseInt(rowData[6]));
                s.setStation_start_distance(Integer.parseInt(rowData[7]));

                addStationUtil(s);
            }
            db.dbTransactionSuccessful();
//            Toast.makeText(this, "S ok", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(TAG).error("데이터 다운로드 오류로 앱을 종료합니다");
            finish();
        } finally {
            db.dbEndTransaction();
        }
    }

    private void write_RS(BufferedReader in) {
        String line;

        //Read each line
        try {
            db.dbBeginTransaction();
            while ((line = in.readLine()) != null) {

                //Split to separate the name from the capital
                String[] rowData = line.split(",");

                //Create a State object for this row's data.

                RouteStationUtil rs = new RouteStationUtil();
                rs.setRoute_id(rowData[0]);
                rs.setRoute_form(Integer.parseInt(rowData[1]));
                rs.setStation_id(rowData[2]);
                rs.setStation_order(Integer.parseInt(rowData[3]));
                rs.setStation_distance(0/*Float.parseFloat(rowData[4])*/);
                rs.setStation_time(Integer.parseInt(rowData[5]));

                addRouteStationUtil(rs);
            }
            db.dbTransactionSuccessful();
//            Toast.makeText(this, "RS ok"    , Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(TAG).error("데이터 다운로드 오류로 앱을 종료합니다");
            finish();
        } finally {
            db.dbEndTransaction();
        }
    }

    private void addRouteUtil(RouteUtil ru) {
//        mDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("route_id", ru.getRoute_id());
        values.put("route_name", ru.getRoute_name());
        values.put("route_form", ru.getRoute_form());
        values.put("route_type", ru.getRoute_type());
        values.put("route_brt_type", ru.getRoute_brt_type());
        values.put("route_first_start_time", ru.getRoute_first_start_time());
        values.put("route_last_start_time", ru.getRoute_last_start_time());
        values.put("route_average_interval", ru.getRoute_average_interval());
        values.put("route_average_time", ru.getRoute_average_time());
        values.put("route_length", ru.getRoute_length());
        values.put("route_station_num", ru.getRoute_station_num());
        values.put("route_start_station", ru.getRoute_start_station());
        values.put("route_important_station1", ru.getRoute_important_station1());
        values.put("route_important_station2", ru.getRoute_important_station2());
        values.put("route_last_station", ru.getRoute_last_station());

        // Inserting Row
        db.insertRoute(values);
    }


    private void addStationUtil(StationUtil su) {
        ContentValues values = new ContentValues();

        values.put("station_id", su.getStation_id());
        values.put("station_name", su.getStation_name());
        values.put("station_type", su.getStation_type());
        values.put("station_angle", su.getStation_angle());
        values.put("station_x", su.getStation_x());
        values.put("station_y", su.getStation_y());
        values.put("station_arrive_distance", su.getStation_arrive_distance());
        values.put("station_start_distance", su.getStation_start_distance());

        // Inserting Row
        db.insertStation(values);

    }

    private void addRouteStationUtil(RouteStationUtil rsu) {
        ContentValues values = new ContentValues();

        values.put("route_id", rsu.getRoute_id()); // Contact Name
        values.put("route_form", rsu.getRoute_form()); // Contact Name
        values.put("station_id", rsu.getStation_id()); // Contact Name
        values.put("station_order", rsu.getStation_order()); // Contact Name
        values.put("station_distance", rsu.getStation_distance()); // Contact Name
        values.put("station_time", rsu.getStation_time()); // Contact Name

        // Inserting Row
        db.insertRouteStation(values);

    }

    @Override
    public void handleServiceMessage(Message message) {

        switch (message.what) {
            case SUCCESS_BOOTING:
                recvData(Data.readData[BytePosition.HEADER_OPCODE]);
                break;
            case ERROR_READ_SERVER_DISCONNECT:
                Toast.makeText(this, "서버 연결 해제", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_BOOTING:
                Toast.makeText(this, "boot 정보 입력 실패", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private synchronized void recvData(byte opCode) {
        MbisUtil.reveData();
        new Receive_OP(opCode); // 여기서 ftp 정보 MapVal class 에 저장한다
        Logger.getLogger(TAG).error("reveData: ftp: ip: " + mv.getFtpIP()); // 2017.02.20 //  test

        // 여기서 ftp 정보로 다운받으면 된다.

    }

    @Override
    public void onLocationChanged(Location location) {

//        Logger.getLogger(TAG).error("onLocationChanged: ");
//        if(isStart == true){
//            isStart = false;
        Logger.getLogger(TAG).debug("onLocationChanged");

        locationWrapper.cancelUpdates();
//
        setInitBooting(location);
//        }
    }
}
