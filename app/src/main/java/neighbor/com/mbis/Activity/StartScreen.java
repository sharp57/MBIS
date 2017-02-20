package neighbor.com.mbis.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import neighbor.com.mbis.R;
import neighbor.com.mbis.csv.RouteStationUtil;
import neighbor.com.mbis.csv.RouteUtil;
import neighbor.com.mbis.csv.StationUtil;
import neighbor.com.mbis.database.DBManager;
import neighbor.com.mbis.function.FTPManager;
import neighbor.com.mbis.maputil.Util;
import neighbor.com.mbis.network.NetworkUtil;
import neighbor.com.mbis.util.MbisUtil;

public class StartScreen extends AppCompatActivity {

    final String TAG = getClass().toString();
    SharedPreferences pref;
    private static final String MY_DB = "my_db";
    private static String HasVisited = "hasVisited";
    private DBManager db;

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
        setFTPInit();
    }

    private void setFTPInit(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FTPManager ftpManager = new FTPManager( NetworkUtil.FTP_IP, NetworkUtil.FTP_PORT, NetworkUtil.FTP_ID, NetworkUtil.FTP_PW);

                    ftpManager.connect();
                    ftpManager.checkMkDir();
                    boolean isLogin = ftpManager.login();
                    Logger.getLogger(TAG).error("isLogin: " + isLogin);
                    FTPFile[] files = ftpManager.list();
                    for(int i = 0; i < files.length; i++){
                        Logger.getLogger(TAG).error("files: " + files[i].getName());
                        ftpManager.get(NetworkUtil.FILE_PATH + NetworkUtil.FILE_PATH_2 + files[i].getName(), files[i].getName());
                    }


                    db = DBManager.getInstance(StartScreen.this);
                    checkData();
                    startActivity(new Intent(StartScreen.this, LoginActivityNew.class));
                    finish();
                    Util.sqliteExport(StartScreen.this);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

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
            if(MbisUtil.getPreferencesInt(this, MbisUtil.version) < Integer.parseInt(fileName[1])) {
                if(fileName[0].equals("route")) {
                    db.deleteRoute(null, null);
                    write_R(in);
                } else if(fileName[0].equals("node")) {
                    db.deleteStation(null, null);
                    write_S(in);
                } else if(fileName[0].equals("routestop")) {
                    db.deleteRouteStation(null, null);
                    write_RS(in);
                }

                file.delete();
                recreate();
                MbisUtil.setPreferencesInt(this, MbisUtil.version, Integer.parseInt(fileName[1]));
                Util.sqliteExport(this);

                startActivity(new Intent(StartScreen.this, LoginActivityNew.class));
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
}
