package neighbor.com.mbis.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import neighbor.com.mbis.csv.RouteStationUtil;
import neighbor.com.mbis.csv.RouteUtil;
import neighbor.com.mbis.csv.StationUtil;
import neighbor.com.mbis.database.DBManager;
import neighbor.com.mbis.maputil.Util;
import neighbor.com.mbis.R;
import neighbor.com.mbis.util.MbisUtil;

/**
 * Created by 권오철 on 2017-02-09.
 */

public class SelectRouteActivityNew extends Activity implements View.OnClickListener{

    private Button testButton;
    private DBManager db;
    private SharedPreferences pref;
    private static final String MY_DB="my_db";
    private static String HasVisited = "hasVisited";
    private long todayLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actrivity_select_route_new);

        setInit();


        db = DBManager.getInstance(this);
//        isHasVisited(this);


        setTodayLong();
//        checkData();

        // 2017.02.06 db test
//        Util.sqliteExport(this);

    }
    private void setInit(){
        testButton = (Button) findViewById(R.id.testButton);

        testButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.testButton:
                if(MbisUtil.getPreferencesBoolean(this,"mode") == false){
                    startActivity(new Intent(SelectRouteActivityNew.this,RunActivity.class));
                }else{
                    startActivity(new Intent(SelectRouteActivityNew.this,MapActivity.class));
                }
                finish();
                break;
        }
    }
    public void isHasVisited(ContextWrapper cw){
        pref = cw.getSharedPreferences(MY_DB, Context.MODE_PRIVATE);

        boolean hasVisited = pref.getBoolean(HasVisited, false);
        if(!hasVisited || true){
            SharedPreferences.Editor e = pref.edit();
            e.putBoolean(HasVisited, true);
            e.commit();

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionResult == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle("권한이 필요합니다.")
                                .setMessage("이 기능을 사용하기 위해서는 단말기의 \"GPS, Storage\" 권한이 필요합니다. 계속하시겠습니까?")
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                                        }

                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .create()
                                .show();
                    }

                    //최초로 권한을 요청할 때
                    else {
                        // CALL_PHONE 권한을 Android OS 에 요청한다.
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                    }
                }
            }


            Toast.makeText(this, "잠시만 기다려 주세요. Database 를 확인하는 중입니다.", Toast.LENGTH_SHORT).show();

            addRouteList();
            addStationList();
            addRouteStationList();
        }
    }

    private void addRouteList() {
        try {
            InputStream is = this.getAssets().open("my_route.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "euc-kr"));

            String line;

            db.dbBeginTransaction();
            //Read each line
            while ((line = reader.readLine()) != null) {

                //Split to separate the name from the capital
                String[] rowData = line.split(",");

                //Create a State object for this row's data.
                RouteUtil r = new RouteUtil();
                r.setRoute_id(rowData[0]);
                r.setRoute_name(rowData[1]);
                r.setRoute_form(Integer.parseInt(rowData[2]));
                r.setRoute_type(Integer.parseInt(rowData[3]));
                r.setRoute_first_start_time(Integer.parseInt(rowData[4]));
                r.setRoute_last_start_time(Integer.parseInt(rowData[5]));
                r.setRoute_average_interval(Integer.parseInt(rowData[6]));
                r.setRoute_average_time(Integer.parseInt(rowData[7]));
                r.setRoute_length(Float.parseFloat(rowData[8]));
                r.setRoute_station_num(Integer.parseInt(rowData[9]));
                r.setRoute_start_station(rowData[10]);
                r.setRoute_important_station1(rowData[11]);
                r.setRoute_important_station2(rowData[12]);
                r.setRoute_last_station(rowData[13]);

                addRouteUtil(r);
            }
            db.dbTransactionSuccessful();
        } catch (IOException e) {
            Toast.makeText(this, "Fail ToT", Toast.LENGTH_SHORT).show();
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

    private void addStationList() {
        try {
            InputStream is = this.getAssets().open("my_station.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "euc-kr"));

            String line;

            db.dbBeginTransaction();

            //Read each line
            while ((line = reader.readLine()) != null) {

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
        } catch (IOException e) {
            Toast.makeText(this, "Fail ToT", Toast.LENGTH_SHORT).show();
        } finally {
            db.dbEndTransaction();
        }

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

    private void addRouteStationList() {
        try {
            InputStream is = this.getAssets().open("my_route_station.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "euc-kr"));

            String line;
            db.dbBeginTransaction();

            //Read each line
            while ((line = reader.readLine()) != null) {

                //Split to separate the name from the capital
                String[] rowData = line.split(",");

                //Create a State object for this row's data.
                RouteStationUtil rs = new RouteStationUtil();
                rs.setRoute_id(rowData[0]);
                rs.setRoute_form(Integer.parseInt(rowData[1]));
                rs.setStation_id(rowData[2]);
                rs.setStation_order(Integer.parseInt(rowData[3]));
                rs.setStation_distance(Float.parseFloat(rowData[4]));
                rs.setStation_time(Integer.parseInt(rowData[5]));

                addRouteStationUtil(rs);

            }
            db.dbTransactionSuccessful();
        } catch (IOException e) {
            Toast.makeText(this, "Fail ToT", Toast.LENGTH_SHORT).show();
        } finally {
            db.dbEndTransaction();
        }

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
    private void setTodayLong() {
        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);

        String today = String.format("%02d", cal.get(Calendar.YEAR) - 2000)
                + String.format("%02d", (cal.get(Calendar.MONTH) + 1))
                + String.format("%02d", cal.get(Calendar.DATE))
                + String.format("%02d", (cal.get(Calendar.HOUR_OF_DAY)) + 9)
                + String.format("%02d", cal.get(Calendar.MINUTE))
                + String.format("%02d", (cal.get(Calendar.SECOND)));
        todayLong = Long.parseLong(today);
    }

    private void checkData() {
        ArrayList<File> csvFiles = new ArrayList<File>();

        File f = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + "/data");
        if (!f.exists()) {
            return;
        }
        File[] allFiles = f.listFiles();

        if(allFiles != null) {
            for (File file : allFiles) {
                if (file.getName().endsWith(".csv")) {
                    csvFiles.add(file);
                    overwriteDB(file);
                }
            }
        }
    }

    public void overwriteDB(File file) {

        try {
//            FileReader reader = new FileReader(file, "UTF8");
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"euc-kr");
            BufferedReader in = new BufferedReader(reader);

            String[] fileName = file.getName().split("_");


            //db 삭제하기 전에 백업하기.
            backupDB();
            if(todayLong > Long.parseLong(fileName[0])) {
                if(fileName[1].startsWith("R.")) {
                    db.deleteRoute(null, null);
                    write_R(in);
                } else if(fileName[1].startsWith("S.")) {
                    db.deleteStation(null, null);
                    write_S(in);
                } else if(fileName[1].startsWith("RS.")) {
                    db.deleteRouteStation(null, null);
                    write_RS(in);
                }

                file.delete();
                recreate();
            }

        } catch (IOException e) {
            Toast.makeText(this, "Fail ToT", Toast.LENGTH_SHORT).show();
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
                String[] rowData                = line.split(",");

                //Create a State object for this row's data.
                RouteUtil r = new RouteUtil();
                r.setRoute_id(rowData[0]);
                r.setRoute_name(rowData[1]);
                r.setRoute_form(Integer.parseInt(rowData[2]));
                r.setRoute_type(Integer.parseInt(rowData[3]));
                r.setRoute_first_start_time(Integer.parseInt(rowData[4]));
                r.setRoute_last_start_time(Integer.parseInt(rowData[5]));
                r.setRoute_average_interval(Integer.parseInt(rowData[6]));
                r.setRoute_average_time(Integer.parseInt(rowData[7]));
                r.setRoute_length(Float.parseFloat(rowData[8]));
                r.setRoute_station_num(Integer.parseInt(rowData[9]));
                r.setRoute_start_station(rowData[10]);
                r.setRoute_important_station1(rowData[11]);
                r.setRoute_important_station2(rowData[12]);
                r.setRoute_last_station(rowData[13]);

                addRouteUtil(r);
            }
            db.dbTransactionSuccessful();
            Toast.makeText(this, "R ok", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
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
            Toast.makeText(this, "S ok", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
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
            Toast.makeText(this, "RS ok", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.dbEndTransaction();
        }
    }

}
