package neighbor.com.mbis.views.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import neighbor.com.mbis.R;
import neighbor.com.mbis.common.SocketHanderMessageDfe;
import neighbor.com.mbis.models.form.Form_Header;
import neighbor.com.mbis.models.value.LogicBuffer;
import neighbor.com.mbis.models.value.MapVal;
import neighbor.com.mbis.models.value.RouteBuffer;
import neighbor.com.mbis.models.value.StationBuffer;
import neighbor.com.mbis.models.value.StationSubBuffer_1;
import neighbor.com.mbis.models.value.StationSubBuffer_2;
import neighbor.com.mbis.network.BytePosition;
import neighbor.com.mbis.network.OP_code;
import neighbor.com.mbis.network.Receive_OP;
import neighbor.com.mbis.util.Func;
import neighbor.com.mbis.util.MbisUtil;
import neighbor.com.mbis.util.Setter;
import neighbor.com.mbis.views.maputil.Data;
import neighbor.com.mbis.views.maputil.HandlerPosition;
import neighbor.com.mbis.views.maputil.LocationWrapper;
import neighbor.com.mbis.views.maputil.Util;
import neighbor.com.mbis.views.maputil.thread.BusTimer;

import static neighbor.com.mbis.common.SocketHanderMessageDfe.ERROR_BOOTING;
import static neighbor.com.mbis.common.SocketHanderMessageDfe.ERROR_READ_SERVER_DISCONNECT;
import static neighbor.com.mbis.common.SocketHanderMessageDfe.SUCCESS_BOOTING;
import static neighbor.com.mbis.common.SocketHanderMessageDfe.SUCCESS_BUS_LOCATION_INFO;
import static neighbor.com.mbis.common.SocketHanderMessageDfe.SUCCESS_LOGIN;
import static neighbor.com.mbis.common.SocketHanderMessageDfe.SUCCESS_STATION_ARRIVE;
import static neighbor.com.mbis.common.SocketHanderMessageDfe.SUCCESS_STATION_START;

/**
 * Created by 권오철 on 2017-02-08.
 */

public class RunActivity extends Activity implements View.OnClickListener, MessageHandler.SmartServiceHandlerInterface, LocationSource.OnLocationChangedListener {

    private final String TAG = getClass().toString();
    private TextView prevStationLabel, nextStationLabel;
    private Button emergencyLabel;

    private LocationWrapper locationWrapper;

    GpsStatus gpsStatus;

    MapVal mv = MapVal.getInstance();
    Form_Header h = Form_Header.getInstance();
    static byte[] headerBuf = null;
    private MessageHandler handler = new MessageHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        setInit();
        checkGpsService();
        setLog();
    }
    private void setInit(){
        prevStationLabel = (TextView) findViewById(R.id.prevStationLabel);
        nextStationLabel = (TextView) findViewById(R.id.nextStationLabel);
        emergencyLabel = (Button) findViewById(R.id.emergency_label);
        nextStationLabel.setSelected(true);
        prevStationLabel.setSelected(true);

        emergencyLabel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emergency_label:
                startActivity(new Intent(RunActivity.this,EmergencyActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        isFinish();
//        super.onBackPressed();
    }

    /**
     * Infalter 다이얼로그
     * @return ab
     */
    private void isFinish() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("알림");
        ab.setMessage("노선 선택 화면으로 이동하시겠습니까?");

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int arg1) {
//                startActivity(new Intent(RunActivity.this,SelectRouteActivityNew.class));
                finish();
            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int arg1) {
                dlg.dismiss();
            }
        });

        ab.show();
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


        locationWrapper = new LocationWrapper(this);
        locationWrapper.setAccuracyFilterEnabled(true, 1000);
        locationWrapper.registerOnLocationChangedListener(this);
        locationWrapper.requestUpdates();

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        gpsStatus = locationManager.getGpsStatus(null);

        // GPS 프로바이더 사용가능여부
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("Main", "isGPSEnabled=" + isGPSEnabled);
        Log.d("Main", "isNetworkEnabled=" + isNetworkEnabled);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
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



    private synchronized void recvData(byte opCode) {
        MbisUtil.reveData();
        new Receive_OP(opCode); // 여기서 ftp 정보 MapVal class 에 저장한다
//        Logger.getLogger(TAG).error("reveData: ftp: ip: " + mv.getFtpIP()); // 2017.02.20 //  test

        // 여기서 ftp 정보로 다운받으면 된다.

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


    @Override
    public void onLocationChanged(Location location) {

    }

    private void setPositionInfo(Location location) {
        // 0x11
        try {
            byte[] op = new byte[]{0x20};
            mv.setDataLength(BytePosition.BODY_BOOT_INFO_SIZE - BytePosition.HEADER_SIZE);
            h.setOp_code(op);
            Setter.setHeader();
            h.setDeviceID(Util.hexStringToByteArray(Util.getDeviceID(RunActivity.this)));
            byte[] otherBusInfo = makeBodyBusPositionInfo(location);
            headerBuf = Util.makeHeader(h, headerBuf);

            Data.writeData = Func.mergeByte(headerBuf, otherBusInfo);
            MbisUtil.sendData(handler, SUCCESS_BOOTING,
                    ERROR_BOOTING);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] makeBodyBusPositionInfo(Location location) {

        Logger.getLogger(TAG).error("location: " + location.getLatitude() + " / " + location.getLongitude());

        long lat = (long) (location.getLatitude() * 100000000);
        long lon = (long) (location.getLongitude() * 100000000);


        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);

        String date = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE));
        String time = String.format("%02d", ((cal.get(Calendar.HOUR_OF_DAY)) + 9)) + String.format("%02d", (cal.get(Calendar.MINUTE))) + String.format("%02d", cal.get(Calendar.SECOND));
//      전송일자 + 전송시각
        byte[] dt = Util.byteReverse(Func.stringToByte(date + time));
//      발생일자 + 발생시각
        byte[] dt2 = Util.byteReverse(Func.stringToByte(date + time));
//      노선정보
//      GPS 정보
//      기기상태
//      통과지점
//      통과지점 ID
//      통과지점 순번
//      도착지점 ID
//      도착지점 순번
//      운행구분
//      예약
        byte[] gpsx = Util.byteReverse(Func.longToByte(lat, 4));
        byte[] gpsy = Util.byteReverse(Func.longToByte(lon, 4));
        byte[] route = Util.byteReverse(Func.integerToByte(MbisUtil.getPreferencesInt(RunActivity.this, MbisUtil.version_route), 2));
        byte[] routestop = Util.byteReverse(Func.integerToByte(MbisUtil.getPreferencesInt(RunActivity.this, MbisUtil.version_routestop), 2));
        byte[] node = Util.byteReverse(Func.integerToByte(MbisUtil.getPreferencesInt(RunActivity.this, MbisUtil.version_node), 2));
        byte[] dev = Util.byteReverse(Func.integerToByte(3, 4));

        return Func.mergeByte(Func.mergeByte(Func.mergeByte(Func.mergeByte(dt, dt2), Func.mergeByte(gpsx, gpsy)), Func.mergeByte(route, routestop)), Func.mergeByte(node, dev));
    }

}
