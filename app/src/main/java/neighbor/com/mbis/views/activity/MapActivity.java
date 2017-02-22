package neighbor.com.mbis.views.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import neighbor.com.mbis.managers.FileManager;
import neighbor.com.mbis.util.Func;
import neighbor.com.mbis.util.Setter;
import neighbor.com.mbis.views.maputil.LocationWrapper;
import neighbor.com.mbis.views.adapter.MyArrayAdapter;
import neighbor.com.mbis.network.BytePosition;
import neighbor.com.mbis.views.maputil.Data;
import neighbor.com.mbis.views.maputil.HandlerPosition;
import neighbor.com.mbis.network.OPUtil;
import neighbor.com.mbis.network.OP_code;
import neighbor.com.mbis.network.Receive_OP;
import neighbor.com.mbis.views.maputil.thread.BusTimer;
import neighbor.com.mbis.models.value.LogicBuffer;
import neighbor.com.mbis.models.value.MapVal;
import neighbor.com.mbis.models.value.RouteBuffer;
import neighbor.com.mbis.models.value.StationBuffer;
import neighbor.com.mbis.models.value.StationSubBuffer_1;
import neighbor.com.mbis.models.value.StationSubBuffer_2;
import neighbor.com.mbis.network.NetworkIntentService;
import neighbor.com.mbis.R;
import neighbor.com.mbis.views.googlemap.AddMarker;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationSource.OnLocationChangedListener {
    private GoogleMap gmap;
    private MapView mapView;
    AddMarker mAddMarker;
    Marker busMarker;

    FileManager eventFileManager;
    FileManager locationFileManager;
    FileManager operationFileManager;


    StationBuffer sBuf = StationBuffer.getInstance();
    StationSubBuffer_1 ssBuf = StationSubBuffer_1.getInstance();
    StationSubBuffer_2 sssBuf = StationSubBuffer_2.getInstance();
    RouteBuffer rBuf = RouteBuffer.getInstance();

    TextView emergencyButton, currentlatView, currentlonView, devicetext, startDisplay, arriveDisplay, waitDisplay, readText, beforeBusDistanceText, beforeBusTimeText, beforeBusNumText, afterBusDistanceText, afterBusTimeText, afterBusNumText, driveState, ascending_descending, myBusNum, routeID;
    ScrollView eventscroll, readScroll;
    ListView movingStationList;
    private ImageView busImage;
    private ViewGroup lockImage;

    PolylineOptions rectOptions;
//    SocketNetwork sNetwork;


    MapVal mv = MapVal.getInstance();
    LogicBuffer lBuf = LogicBuffer.getInstance();
    OP_code op_code;

    final int DETECTRANGE = 30;

    static boolean mflag = false;
    static boolean startFlag = false;
    static int stationBuf = -1;

    int directionSwitch = rBuf.getRouteType();

    static byte[] op;

    BusTimer busTimer;


//    private NetworkIntentService mService = neighbor.com.mbis.activity.LoginActivityNew.mService;

    //이벤트 발생할 때 데이터 전송하려면 이벤트 발생하는곳에 사용 : sendData(byte[]배열);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        NetworkIntentService.mHandler = mHandler;

        LocationWrapper locationWrapper = new LocationWrapper(this);
        locationWrapper.setAccuracyFilterEnabled(true, 100);
        locationWrapper.registerOnLocationChangedListener(this);
        locationWrapper.requestUpdates();

        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);

        String packetFileName = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE)) + " packet";
        String locationFileName = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE)) + " location";
        String operationFileName = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE)) + " operation";

        eventFileManager = new FileManager(packetFileName);
        locationFileManager = new FileManager(locationFileName);
        operationFileManager = new FileManager(operationFileName);

        busTimer = new BusTimer(HandlerPosition.BUSTIMER_30SEC, mHandler);

//        NetworkService.socket.setHandler(mHandler);
//        mService.setHandler(mHandler);
//        sNetwork = new SocketNetwork(NetworkUtil.IP, NetworkUtil.PORT, mHandler);
//        sNetwork.start();

        checkGpsService();
        getItem(savedInstanceState);
        setLog();
        onClickEmergencyButton();
    }


    private boolean checkGpsService() {

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

    public void setLog() {

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

                addUtilDefault(location, gpsStatus);

                driving();
            }


            public void onStatusChanged(String provider, int status, Bundle extras) {
                //위치공급자의 상태가 바뀔 때 호출
                //켬 -> 끔 or 끔 -> 켬
                Toast.makeText(getApplicationContext(), "GPS 상태 변환", Toast.LENGTH_SHORT).show();
            }

            public void onProviderEnabled(String provider) {
                //위치 공급자가 사용 가능해 질 때 호출
                //즉 GPS를 켜면 호출됨
                Toast.makeText(getApplicationContext(), "GPS on", Toast.LENGTH_SHORT).show();
            }

            public void onProviderDisabled(String provider) {
                //위치 공급자가 사용 불가능해질(disabled) 때 호출
                //GPS 꺼지면 여기서 예외처리 해주면 됨
                Toast.makeText(getApplicationContext(), "GPS off", Toast.LENGTH_SHORT).show();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        busTimer.cancel();
//        cTimer.cancel();

        // 2017.02.10
//        if(mService != null){
//            mService.close();   // 2017.02.10
//            mService.stopSelf();
//        }
//        sNetwork.close();

//        sBuf.clearAll();
//        ssBuf.clearAll();
//        sssBuf.clearAll();
//        lBuf.getStationListBuf().clear();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.gmap = map;
        mAddMarker = new AddMarker(this.gmap);
        //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gmap.setMyLocationEnabled(true);
        rectOptions = new PolylineOptions().color(0xffff0000);


        for (int i = 0; i < sBuf.getReferenceLatPosition().size(); i++) {
            rectOptions.add(new LatLng(sBuf.getReferenceLatPosition().get(i), sBuf.getReferenceLngPosition().get(i)));
        }
        Polyline polyline = gmap.addPolyline(rectOptions);
        for (int i = 0; i < sBuf.getReferenceLatPosition().size(); i++) {
            busMarker = mAddMarker.getMark(sBuf.getReferenceLatPosition().get(i), sBuf.getReferenceLngPosition().get(i), getApplicationContext());
        }


    }

    private void sendData() {
        String dd = "";
        for (int j = 0; j < Data.writeData.length; j++) {
            dd = dd + String.format("%02X ", Data.writeData[j]);
        }
        eventFileManager.saveData("\n(" + mv.getSendYear() + "." + mv.getSendMonth() + "." + mv.getSendDay() +
                " - " + mv.getSendHour() + ":" + mv.getSendMin() + ":" + mv.getSendSec() +
                ")\n[SEND:" + Data.writeData.length + "] - " + dd);
//        mService.writeData();

//        NetworkService.socket.writeData(Data.writeData);
//        mService.sendData();
//        sNetwork.writeData(Data.writeData);
        eventscroll.fullScroll(View.FOCUS_DOWN);
        busTimer.cancel();
        busTimer.start();
    }

    private void addUtilDefault(Location location, GpsStatus gpsStatus) {
        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);

        double latD = location.getLatitude();
        double lngD = location.getLongitude();

        sBuf.getDistance().clear();
        for (int i = 0; i < sBuf.getReferenceLatPosition().size(); i++) {
            sBuf.addDistance(Func.getDistance(latD, lngD, sBuf.getReferenceLatPosition().get(i), sBuf.getReferenceLngPosition().get(i)));
        }

        if (startFlag && !mflag && sBuf.getDistance().get(stationBuf) > sBuf.getRemark().get(stationBuf) * 2) {//노선이탈

            offenceInfo(2);
            driveEnd();

            startFlag = false;
            mflag = true;

            devicetext.append("\n노선이탈 운행종료");
            Log.e("Buffer", "\n" + LogicBuffer.startBuf[0] + "," + LogicBuffer.startBuf[1] + "," + LogicBuffer.startBuf[2] + "\n"
                    + LogicBuffer.jumpBuf[0] + "," + LogicBuffer.jumpBuf[1] + "," + LogicBuffer.jumpBuf[2]
                    + "\n" + directionSwitch + " " + rBuf.getRouteType());
        }


        currentlatView.setText("위도 : " + String.format("%.5f", latD));
        currentlonView.setText("경도 :" + String.format("%.5f", lngD));

        //날짜시간날짜시간
        mv.setSendYear(cal.get(Calendar.YEAR) - 2000);
        mv.setSendMonth(cal.get(Calendar.MONTH) + 1);
        mv.setSendDay(cal.get(Calendar.DATE));
        mv.setSendHour((cal.get(Calendar.HOUR_OF_DAY)) + 9);
        mv.setSendMin(cal.get(Calendar.MINUTE));
        mv.setSendSec(cal.get(Calendar.SECOND));
        mv.setEventYear(cal.get(Calendar.YEAR) - 2000);
        mv.setEventMonth(cal.get(Calendar.MONTH) + 1);
        mv.setEventDay(cal.get(Calendar.DATE));
        mv.setEventHour((cal.get(Calendar.HOUR_OF_DAY)) + 9);
        mv.setEventMin(cal.get(Calendar.MINUTE));
        mv.setEventSec(cal.get(Calendar.SECOND));

        //노선정보
        mv.setRouteID(rBuf.getRouteID());
        mv.setRouteNum(rBuf.getRouteName());
        mv.setRouteDivision("00");

        //GPS정보
        double bufX = location.getLatitude() * 100000;
        double bufY = location.getLongitude() * 100000;
        mv.setLocationX((int) bufX);
        mv.setLocationY((int) bufY);
        mv.setBearing(Func.getBearingAtoB(LogicBuffer.locationXBuf, LogicBuffer.locationYBuf, latD, lngD));
        mv.setSpeed(Func.getSpeed(LogicBuffer.locationXBuf, LogicBuffer.locationYBuf, latD, lngD));

        LogicBuffer.locationXBuf = latD;
        LogicBuffer.locationYBuf = lngD;

        //기기상태
        int i = 0;
        final Iterator<GpsSatellite> iter = gpsStatus.getSatellites().iterator();

        while (iter.hasNext()) {
            GpsSatellite satellite = iter.next();
            if (satellite.usedInFix()) {
                i++;
            }
        }
        if (i >= 3) {
            mv.setGpsState(0);
        } else mv.setGpsState(128);

        locationFileManager.saveData("#" + latD + "," + lngD);
    }

    private void addUtilArriveStation() {
        lBuf.setArriveTimeBuf(mv.getSendHour() * 3600 + mv.getSendMin() * 60 + mv.getSendSec());
        mv.setArriveStationID(sBuf.getReferenceStationId().get(stationBuf));
        mv.setArriveStationTurn(sBuf.getStationOrder().get(stationBuf));
        mv.setAdjacentTravelTime(lBuf.getArriveTimeBuf() - lBuf.getStartTimeBuf());
        mv.setReservation(0);

        movingStationList.setSelection(lBuf.getStationListBuf().size() - movingStationList.getChildCount() - stationBuf);
        //밥먹고 화면 올라갈 부분 숫자 계산하기
        lockImage.setBackgroundResource(R.drawable.focus_arrive);
        busImage.setVisibility(View.VISIBLE);
        arriveDisplay.setVisibility(View.VISIBLE);
        startDisplay.setVisibility(View.INVISIBLE);
        waitDisplay.setVisibility(View.INVISIBLE);
    }

    private void addUtilStartStation() {
        mv.setArriveStationID(sBuf.getReferenceStationId().get(stationBuf));
        mv.setArriveStationTurn(sBuf.getStationOrder().get(stationBuf));
        mv.setAdjacentTravelTime(lBuf.getArriveTimeBuf() - lBuf.getStartTimeBuf());
        mv.setReservation(0);
        lBuf.setStartTimeBuf(mv.getSendHour() * 3600 + mv.getSendMin() * 60 + mv.getSendSec());
        mv.setServiceTime(lBuf.getStartTimeBuf() - lBuf.getArriveTimeBuf());

        lockImage.setBackgroundResource(R.drawable.focus_start);
        busImage.setVisibility(View.VISIBLE);
        arriveDisplay.setVisibility(View.INVISIBLE);
        startDisplay.setVisibility(View.VISIBLE);
        waitDisplay.setVisibility(View.INVISIBLE);

    }

    private void addUtilStartDrive() {
        lBuf.setStartTimeBuf(mv.getSendHour() * 3600 + mv.getSendMin() * 60 + mv.getSendSec());
        mv.setDriveDate(String.format("%02d", mv.getSendYear()) + String.format("%02d", mv.getSendMonth()) + String.format("%02d", mv.getSendDay()));
        mv.setDriveStartTime(String.format("%02d", mv.getSendHour()) + String.format("%02d", mv.getSendMin()) + String.format("%02d", mv.getSendSec()));
        mv.setReservation(0);
    }

    private void addUtilEndDrive() {
        mv.setArriveStationID(sBuf.getReferenceStationId().get(stationBuf));
        mv.setArriveStationTurn(sBuf.getStationOrder().get(stationBuf));
        mv.setDetectStationArriveNum(lBuf.getStationArriveNumBuf());
        mv.setDetectStationStartNum(lBuf.getStationStartNumBuf());

        mv.setReservation(0);

        lockImage.setBackgroundResource(R.drawable.focus_wait);
        busImage.setVisibility(View.INVISIBLE);
        arriveDisplay.setVisibility(View.INVISIBLE);
        startDisplay.setVisibility(View.INVISIBLE);
        waitDisplay.setVisibility(View.VISIBLE);

        movingStationList.setSelection(sBuf.getReferenceStationId().size() - 1);

    }

    private void addUtilOffence(int offenceCode) {
        mv.setArriveStationID(sBuf.getReferenceStationId().get(stationBuf));
        mv.setArriveStationTurn(sBuf.getStationOrder().get(stationBuf));
        mv.setOffenceCode(offenceCode);
        mv.setReservation(0);
    }

    private void addUtilEmergency() {
        mv.setArriveStationID(sBuf.getReferenceStationId().get(stationBuf));
        mv.setArriveStationTurn(sBuf.getStationOrder().get(stationBuf));
//            mv.setAfterArriveStationId(sBuf.getReferenceStationId().get(stationBuf + 1));
//            mv.setAfterArriveStationTurn(sBuf.getStationOrder().get(stationBuf + 1));

        mv.setReservation(0);
    }

    private void addUtilBusLocation() {
        mv.setArriveStationID(sBuf.getReferenceStationId().get(stationBuf));
        mv.setArriveStationTurn(sBuf.getStationOrder().get(stationBuf));
        mv.setAfterArriveStationId(sBuf.getReferenceStationId().get(stationBuf + 1));
        mv.setAfterArriveStationTurn(sBuf.getStationOrder().get(stationBuf + 1));
        mv.setReservation(0);
    }

    private void driveStart() {

        op = new byte[]{0x15};

        mv.setDataLength(BytePosition.BODY_DRIVE_START_SIZE - BytePosition.HEADER_SIZE);

        addUtilStartDrive();

        Setter.setHeader();
        Setter.setBody_Default();
        Setter.setBody_StartDrive();
        op_code = new OP_code(op);

        sendData();

        //운행 시작하면 30초씩 센다.
        busTimer.start();

        startFlag = true;
    }

    private void stationArrive(int i) {
        stationBuf = i;
        op = new byte[]{0x21};
        lBuf.setStationArriveNumBuf(lBuf.getStationArriveNumBuf() + 1);

        mv.setDataLength(BytePosition.BODY_STATION_ARRIVE_SIZE - BytePosition.HEADER_SIZE);

        addUtilArriveStation();

        Setter.setHeader();
        Setter.setBody_Default();
        Setter.setBody_ArriveStation();
        op_code = new OP_code(op);

        sendData();
        if (stationBuf != sBuf.getReferenceLatPosition().size() - 1) {
            mflag = true;
        }
    }

    private void stationArrive() {
        op = new byte[]{0x21};
        lBuf.setStationArriveNumBuf(lBuf.getStationArriveNumBuf() + 1);
        addUtilArriveStation();

        mv.setDataLength(BytePosition.BODY_STATION_ARRIVE_SIZE - BytePosition.HEADER_SIZE);

        Setter.setHeader();
        Setter.setBody_Default();
        Setter.setBody_ArriveStation();
        op_code = new OP_code(op);

        sendData();
    }

    public void getItem(Bundle savedInstanceState) {

        emergencyButton = (TextView) findViewById(R.id.emergencyBtn);

        currentlatView = (TextView) findViewById(R.id.curlat);
        currentlonView = (TextView) findViewById(R.id.curlon);
        devicetext = (TextView) findViewById(R.id.devicetext);

        beforeBusDistanceText = (TextView) findViewById(R.id.beforeBusDistanceText);
        beforeBusTimeText = (TextView) findViewById(R.id.beforeBusTimeText);
        beforeBusNumText = (TextView) findViewById(R.id.beforeBusNumText);
        afterBusDistanceText = (TextView) findViewById(R.id.afterBusDistanceText);
        afterBusTimeText = (TextView) findViewById(R.id.afterBusTimeText);
        afterBusNumText = (TextView) findViewById(R.id.afterBusNumText);

        driveState = (TextView) findViewById(R.id.driveState);
        ascending_descending = (TextView) findViewById(R.id.ascending_descending);
        myBusNum = (TextView) findViewById(R.id.myBusNum);
        routeID = (TextView) findViewById(R.id.routeID);

        busImage = (ImageView) findViewById(R.id.busImage);
        startDisplay = (TextView) findViewById(R.id.startDisplay);
        arriveDisplay = (TextView) findViewById(R.id.arriveDisplay);
        waitDisplay = (TextView) findViewById(R.id.waitDisplay);
        lockImage = (ViewGroup) findViewById(R.id.lockImage);
        lockImage.setBackgroundResource(R.drawable.focus_wait);

        movingStationList = (ListView) findViewById(R.id.movingStationList);

        eventscroll = (ScrollView) findViewById(R.id.eventscroll);

//        readText = (TextView) findViewById(R.id.readText);
//        readScroll = (ScrollView) findViewById(R.id.readScroll);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        settingMovingStationList();
        makeMyBusInfoView();
    }

    private void stationStart() {
        op = new byte[]{0x22};
        lBuf.setStationStartNumBuf(lBuf.getStationStartNumBuf() + 1);
        addUtilStartStation();

        mv.setDataLength(BytePosition.BODY_STATION_START_SIZE - BytePosition.HEADER_SIZE);

        Setter.setHeader();
        Setter.setBody_Default();
        Setter.setBody_StartStation();
        op_code = new OP_code(op);

        makeMyBusInfoView();
        sendData();

    }

    private void driveEnd(int i) { //종료 메소드
        stationBuf = i;
        op = new byte[]{0x31};

        mv.setDataLength(BytePosition.BODY_DRIVE_END_SIZE - BytePosition.HEADER_SIZE);

        addUtilEndDrive();

        Setter.setHeader();
        Setter.setBody_Default();
        Setter.setBody_EndDrive();
        op_code = new OP_code(op);


        sendData();

        busTimer.cancel();
        startFlag = false;
        stationBuf = -1;
        retryConnection();

        LogicBuffer.jumpBuf = new int[]{-2, -1, 0};
        LogicBuffer.startBuf = new int[]{-10, -10, -10};
        mflag = false;

        changeDirection();

    }

    private void driveEnd() {
        op = new byte[]{0x31};

        mv.setDataLength(BytePosition.BODY_DRIVE_END_SIZE - BytePosition.HEADER_SIZE);

        addUtilEndDrive();

        Setter.setHeader();
        Setter.setBody_Default();
        Setter.setBody_EndDrive();
        op_code = new OP_code(op);

        sendData();

        busTimer.cancel();
        startFlag = false;
        stationBuf = -1;
        retryConnection();

        LogicBuffer.jumpBuf = new int[]{-2, -1, 0};
        LogicBuffer.startBuf = new int[]{-10, -10, -10};
        mflag = false;

        changeDirection();
    }

    private void offenceInfo(int offenceCode) {
        op = new byte[]{0x24};

        mv.setDataLength(BytePosition.BODY_OFFENCE_SIZE - BytePosition.HEADER_SIZE);

        addUtilOffence(offenceCode);

        Setter.setHeader();
        Setter.setBody_Default();
        Setter.setBody_Offence();

        op_code = new OP_code(op);

        sendData();
    }

    private void emergencyInfo() {
        op = new byte[]{0x51};

        mv.setDataLength(BytePosition.BODY_EMERGENCY_SIZE - BytePosition.HEADER_SIZE);

        addUtilEmergency();

        Setter.setHeader();
        Setter.setBody_Default();
        Setter.setBody_Emergency();

        op_code = new OP_code(op);

        sendData();
    }

    private void busLocationInfo() {
        op = new byte[]{0x20};

        mv.setDataLength(BytePosition.BODY_BUSLOCATION_SIZE - BytePosition.HEADER_SIZE);

        addUtilBusLocation();

        Setter.setHeader();
        Setter.setBody_Default();
        Setter.setBody_BusLocation();

        op_code = new OP_code(op);

        sendData();
    }


    private void changeDirection() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(MapActivity.this);
        alt_bld.setMessage("상/하행을 변경하시겠습니까?").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mflag = false;
                        busTimer.cancel();
                        startFlag = false;
                        stationBuf = -1;

                        sBuf.clearAll();

                        if (rBuf.getRouteType() == directionSwitch) {
                            if (directionSwitch == 1) {
                                rBuf.setRouteType(2);
                            } else if (directionSwitch == 2) {
                                rBuf.setRouteType(1);
                            }
                            sBuf.setReferenceStationId(sssBuf.getReferenceStationId());
                            sBuf.setReferenceLatPosition(sssBuf.getReferenceLatPosition());
                            sBuf.setReferenceLngPosition(sssBuf.getReferenceLngPosition());
                            sBuf.setReferenceStationName(sssBuf.getReferenceStationName());
                            sBuf.setStationOrder(sssBuf.getStationOrder());
                            sBuf.setReferenceStationName(sssBuf.getReferenceStationName());
                            sBuf.setStationDivision(sssBuf.getStationDivision());
                            sBuf.setRemark(sssBuf.getRemark());
                        } else {
                            if (directionSwitch == 1) {
                                rBuf.setRouteType(2);
                            } else if (directionSwitch == 2) {
                                rBuf.setRouteType(1);
                            }
                            sBuf.setReferenceStationId(ssBuf.getReferenceStationId());
                            sBuf.setReferenceLatPosition(ssBuf.getReferenceLatPosition());
                            sBuf.setReferenceLngPosition(ssBuf.getReferenceLngPosition());
                            sBuf.setReferenceStationName(ssBuf.getReferenceStationName());
                            sBuf.setStationOrder(ssBuf.getStationOrder());
                            sBuf.setReferenceStationName(ssBuf.getReferenceStationName());
                            sBuf.setStationDivision(ssBuf.getStationDivision());
                            sBuf.setRemark(ssBuf.getRemark());
                        }
                        lBuf.getStationListBuf().clear();

                        recreate();
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        mflag = false;
                        dialog.cancel();
                        lBuf.getStationListBuf().clear();

                        recreate();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("Change");
        // Icon for AlertDialog
        alert.setIcon(R.drawable.icon);
        alert.show();
    }

    private void onClickEmergencyButton() {

        final String[] option = new String[]{"버스사고", "버스고장", "도로사고", "긴급상황발생"};

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, option);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Emergency!!!!!");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dia, final int position) {

                mv.setEmergencyCode(position);

                android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(MapActivity.this);
                alt_bld.setMessage("신고하겠습니까?").setCancelable(
                        false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (stationBuf < 0) {
                                    devicetext.append("\n신고 실패..(운행중이 아닙니다.)");
                                    operationFileManager.saveData("\n신고 실패..(운행중이 아닙니다.)");
                                } else {
                                    emergencyInfo();
                                    devicetext.append("\n신고 완료!!");
                                    operationFileManager.saveData("\n신고 완료!!");
                                }
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = alt_bld.create();
                // Title for AlertDialog
                alert.setTitle("Change");
                // Icon for AlertDialog
                alert.show();
            }
        });

        final android.support.v7.app.AlertDialog dialog = builder.create();

        emergencyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.show();
            }
        });
    }


    private void driving() {

        for (int i = 0; i < sBuf.getReferenceLatPosition().size(); i++) {
            if (sBuf.getDistance().get(i) < DETECTRANGE && !mflag && i > stationBuf) {
                //역에 도착했을 때
                stationBuf = i;
                lBuf.setArriveLatBuf(mv.getLocationX() * 0.00001);
                lBuf.setArriveLngBuf(mv.getLocationY() * 0.00001);

                if (stationBuf == 0) {
                    //도착지점이 A라면 A가 차고지가 되어 운행시작을 알림
                    LogicBuffer.jumpBuf[0] = -2;
                    LogicBuffer.jumpBuf[1] = -1;
                    LogicBuffer.jumpBuf[2] = stationBuf;

                    driveStart();
                    devicetext.append("\n정상 운행 시작 ");
                    operationFileManager.saveData("\n정상 운행 시작 ");
                }

                LogicBuffer.jumpBuf[0] = LogicBuffer.jumpBuf[1];
                LogicBuffer.jumpBuf[1] = LogicBuffer.jumpBuf[2];
                LogicBuffer.jumpBuf[2] = stationBuf;

                LogicBuffer.startBuf[0] = LogicBuffer.startBuf[1];
                LogicBuffer.startBuf[1] = LogicBuffer.startBuf[2];
                LogicBuffer.startBuf[2] = stationBuf;

                //운행 시작 이후 도착 이벤트 발생
                if (startFlag) {
                    //다음 순서대로 잘 갔는지 비교
                    if (LogicBuffer.jumpBuf[2] - LogicBuffer.jumpBuf[0] < 3) {
                        //정상적인 역 도착
                        if (sBuf.getStationDivision().get(stationBuf) == 2) {
                            //도착한 곳이 교차로
                            stationArrive();
                            devicetext.append("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 도착(교)");
                            operationFileManager.saveData("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 도착(교차로)");
                        } else {
                            //도착한 곳이 역
                            stationArrive();
                            devicetext.append("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 도착(역)");
                            operationFileManager.saveData("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 도착(역)");
                        }
                    } else {
                        //역 점프
                        stationArrive();
                        devicetext.append("\n역 점프 발생");
                        operationFileManager.saveData("\n역 점프 발생");
                    }
                }
                //비정상 출발일 시 연속된 3역을 검사한다.
                else {
                    //만약 3 역의 차이가 4보다 적다면 해당 역에서 출발시킨다.
                    if (LogicBuffer.startBuf[2] - LogicBuffer.startBuf[0] < 3) {
                        driveStart();
                        stationArrive();
                        devicetext.append("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 비정상 운행 시작");
                        devicetext.append("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 도착");
                        operationFileManager.saveData("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 비정상 운행 시작");
                        operationFileManager.saveData("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 도착");
                    }
                }
                if (stationBuf != sBuf.getReferenceLatPosition().size() - 1) {
                    mflag = true;
                } else {
                    //상하행 변경
                    devicetext.append("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 운행 종료");
                    operationFileManager.saveData("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 운행 종료");
                    //마지막 역 도착 운행종료
                    driveEnd(i);
                    mflag = true;

                }
                if (stationBuf != -1)
                    Log.e("비정상도착 체크", "stationName : " + sBuf.getReferenceStationName().get(stationBuf) + "\nstationBuf : " + stationBuf + " \n0 : " + LogicBuffer.startBuf[0] + " \n1 : " + LogicBuffer.startBuf[1] + " \n2 : " + LogicBuffer.startBuf[2]);
            }
        }

        //운행 시작 전
        if (stationBuf < 0) {
        }
        //어떤 지점에서 출발했을 때
        else if (mflag && sBuf.getDistance().get(stationBuf) >= DETECTRANGE &&
                //좌표 튀는거 방지
                Func.getDistance(lBuf.arriveLatBuf, lBuf.arriveLngBuf, mv.getLocationX() * 0.00001, mv.getLocationY() * 0.00001) > HandlerPosition.SPLASH_LOCATION_RANGE) {
            if (startFlag) {
                if (stationBuf == sBuf.getReferenceLatPosition().size() - 1) {
                    //출발지점이 마지막 역이라면 출발 없음
                    return;
                } else {
                    //출발지점이 마지막 역이 아니라면 그냥 해당 역에서 출발한 것을 알림
                    stationStart();
                    devicetext.append("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 출발");
                    operationFileManager.saveData("\n" + sBuf.getReferenceStationId().get(stationBuf) + " 출발");
                }
            }
            Log.e("비정상출발 체크", "stationName : " + sBuf.getReferenceStationName().get(stationBuf) + "\nstationBuf : " + stationBuf + " \n0 : " + LogicBuffer.startBuf[0] + " \n1 : " + LogicBuffer.startBuf[1] + " \n2 : " + LogicBuffer.startBuf[2]);

            mflag = false;
        }
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerPosition.SEND_BUS_LOCATION_INFO:
                    busLocationInfo();
                    devicetext.append("\n정주기 전송!");
                    operationFileManager.saveData("\n정주기 전송!");
                    break;
                case HandlerPosition.TIME_CHANGE:
                    break;
                //소켓 연결 성공!
                case HandlerPosition.SOCKET_CONNECT_SUCCESS:
                    retryCountdownTimer();
//                    readText.append("Socket Connect Success\n");
                    break;
                //소켓 연결 실패!
                case HandlerPosition.SOCKET_CONNECT_ERROR:
//                    readText.append("Socket Connect Error\n");
                    retryConnection();
                    break;
                //연결 중 서버가 죽었을 때
                case HandlerPosition.READ_SERVER_DISCONNECT_ERROR:
//                    readText.append("Server Disconnect Error(Read)\n");
                    retryConnection();
                    break;
                //데이터 전송이 실패했을 때
                case HandlerPosition.WRITE_SERVER_DISCONNECT_ERROR:
//                    readText.append("Server Disconnect Error(Write)\n");
                    retryConnection();
                    break;
                //데이터 수신 성공!
                case HandlerPosition.DATA_READ_SUCESS:
                    retryCountdownTimer();
                    recvData(Data.readData[BytePosition.HEADER_OPCODE]);

                    break;
                //잘못된 데이터가 왔을 때
                case HandlerPosition.READ_DATA_ERROR:
//                    readText.append("Server Read Data Error\n");
                    retryConnection();
                    retryCountdownTimer();
                    break;
                //타임아웃!
                case HandlerPosition.READ_TIMEOUT_ERROR:
//                    readText.append("\nTime out!\n");
                    retryConnection();
                    retryCountdownTimer();
                    break;
            }
        }
    };

    private void retryConnection() {

//        cTimer.cancel();
//        mService.close();
//        mService.stopSelf();
//        mService.setHandler(mHandler);
//        startService(new Intent(MapActivity.this, mService.getClass()));
//        bindService(new Intent(MapActivity.this,  mService.getClass()), mConnection, Context.BIND_AUTO_CREATE);
//        sNetwork.close();
//        sNetwork = new SocketNetwork(NetworkUtil.IP, NetworkUtil.PORT, mHandler);
//        sNetwork.start();
    }

    private void retryCountdownTimer() {
//        cTimer.cancel();
//        cTimer.start();
    }

//    SocketReadTimeout cTimer = new SocketReadTimeout(HandlerPosition.SERVER_READ_TIMEOUT, mHandler);

    private void recvData(byte opCode) {
        String dd = "";
        for (int i = 0; i < Data.readData.length; i++) {
            dd = dd + String.format("%02x ", Data.readData[i]);
//            readText.append(String.format("%02x ", Data.readData[i]));
        }
        eventFileManager.saveData("\n(" + mv.getSendYear() + ":" + mv.getSendMonth() + ":" + mv.getSendDay() +
                " - " + mv.getSendHour() + ":" + mv.getSendMin() + ":" + mv.getSendSec() +
                ")\n[RECV:" + Data.readData.length + "] - " + dd);
//        readText.append("\n");

        new Receive_OP(opCode);
        if (opCode == OPUtil.OP_OTHER_BUS_INFO) {
            makeOtherBusInfoView();
        }
    }

    private void makeOtherBusInfoView() {
        beforeBusDistanceText.setText("" + mv.getBeforeBusDistance());
        beforeBusTimeText.setText("" + mv.getBeforeBusTime());
        beforeBusNumText.setText("" + mv.getBeforeBusNum());
        afterBusDistanceText.setText("" + mv.getAfterBusDistance());
        afterBusTimeText.setText("" + mv.getAfterBusTime());
        afterBusNumText.setText("" + mv.getAfterBusNum());
    }

    private void makeMyBusInfoView() {
        routeID.setText("" + mv.getRouteID());
        myBusNum.setText(rBuf.getRouteName());
        if (mv.getDriveDivision() == 0) {
            driveState.setText("정상운행");
        } else if (mv.getDriveDivision() == 1) {
            driveState.setText("공 차");
        } else if (mv.getDriveDivision() == 2) {
            driveState.setText("막 차");
        } else {
            driveState.setText("");
        }
        if (directionSwitch == 1) {
            ascending_descending.setText("상 행");
        } else if (directionSwitch == 2) {
            ascending_descending.setText("하 행");
        } else {
            ascending_descending.setText("");
        }
    }

    private void settingMovingStationList() {


        lBuf.getStationListBuf().add("");
        lBuf.getStationListBuf().add("");
        lBuf.getStationListBuf().add("");
        lBuf.getStationListBuf().add("");
        lBuf.getStationListBuf().add("");

        for (int i = 0; i < sBuf.getReferenceStationName().size(); i++) {
            lBuf.getStationListBuf().add(sBuf.getReferenceStationName().get(sBuf.getReferenceStationName().size() - 1 - i));
        }
        lBuf.getStationListBuf().add("");

//        ArrayAdapter mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sBuf.getReferenceStationId());
        MyArrayAdapter mAdapter = new MyArrayAdapter(this, R.layout.map_item, lBuf.getStationListBuf());
        movingStationList.setAdapter(mAdapter);
        movingStationList.setClickable(false);
        movingStationList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int act = motionEvent.getAction();
                switch (act & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        movingStationList.setSelection(sBuf.getReferenceStationId().size() - 1);

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder BackAlertDialog = new AlertDialog.Builder(this);

        BackAlertDialog.setTitle("운행 종료");

        BackAlertDialog.setMessage("정말로 종료하시겠습니까?");

        BackAlertDialog.setPositiveButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //Cancel alert dialog box .
                        dialog.cancel();
                    }
                });

        BackAlertDialog.setNegativeButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        lBuf.getStationListBuf().clear();

                        busTimer.cancel();
//                        cTimer.cancel();
//                        sNetwork.close();

                        // 2017.02.10
//                        MbisUtil.log(MapActivity.this, "mService: " + mService);
//                        if(mService != null){
//                            mService.stopSelf();
//                        }
//                        stopService(new Intent(MapActivity.this, mService.getClass()));

                        //Exit from activity.
                        finish();
                        // 2017.02.10 주석
//                        RouteStationActivity rsActivity = (RouteStationActivity) RouteStationActivity.rsActivity;
//                        rsActivity.finish();
                    }
                });

        BackAlertDialog.show();

        return;
    }


    //서비스 커넥션 선언.
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            NetworkIntentService.MainServiceBinder binder = (NetworkIntentService.MainServiceBinder) service;
//            mService = binder.getService(); //서비스 받아옴
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
//            mService = null;
        }
    };

    @Override
    public void onLocationChanged(Location location) {

    }
}