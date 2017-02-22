package neighbor.com.mbis.views.activity;

import android.Manifest;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
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

import neighbor.com.mbis.util.csv.RouteStationUtil;
import neighbor.com.mbis.util.csv.RouteUtil;
import neighbor.com.mbis.util.csv.StationUtil;
import neighbor.com.mbis.managers.DBManager;
import neighbor.com.mbis.models.value.MapVal;
import neighbor.com.mbis.models.value.RouteBuffer;
import neighbor.com.mbis.R;

public class SelectRouteActivity extends AppCompatActivity {

    ListView mList;
    DBManager db;
    TextView tv;
    SimpleCursorAdapter scAdapter;
    RouteBuffer rBuf = RouteBuffer.getInstance();

    long todayLong;

    SharedPreferences pref;
    private static final String MY_DB="my_db";
    private static String HasVisited = "hasVisited";
    String text;
    MapVal mv = MapVal.getInstance();

    FloatingActionButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);

        mList = (ListView)findViewById(R.id.mList);
        tv = (TextView) findViewById(R.id.text);
        logoutButton = (FloatingActionButton) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginActivity.mService != null) {
                    LoginActivity.mService.close();
                    LoginActivity.mService.stopSelf();
                }
                finish();
                Intent i = new Intent(SelectRouteActivity.this, LoginActivity.class);
                i.putExtra("flag", false);
                startActivity(i);
            }
        });

        db = DBManager.getInstance(this);
        isHasVisited(this);


        setTodayLong();
        checkData();

        scAdapter = new SimpleCursorAdapter(
                this,
                R.layout.route_select_item,
                db.queryRoute(new String[]{"_id", "route_id", "route_name", "route_type"}, null, null, null, null, null),
                new String[]{"route_name", "route_type"},
                new int[]{R.id.busNum, R.id.busDivision}
        );

        mList.setAdapter(scAdapter);


        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor c = (Cursor)mList.getItemAtPosition(position);

                text = c.getString(1).toString();
                rBuf.setRouteID(Long.parseLong(text));
                rBuf.setRouteName(c.getString(2).toString());
                String dir = c.getString(3).toString();
                rBuf.setRouteType(Integer.parseInt(dir));
                mv.setRouteForm(dir);

                DialogSelectOption();
            }
        });

//        Toast.makeText(this, mv.getDeviceID() + "", Toast.LENGTH_SHORT).show();

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
        }
    }

    private void write_R(BufferedReader in) {

        String line = "";
        try {
            while ((line = in.readLine()) != null) {

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
            Toast.makeText(this, "R ok", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        }
    }

    private void write_S(BufferedReader in) {
        String line;

        //Read each line
        try {
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
            Toast.makeText(this, "S ok", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        }
    }

    private void write_RS(BufferedReader in) {
        String line;

        //Read each line
        try {
            while ((line = in.readLine()) != null) {

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
            Toast.makeText(this, "RS ok", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        }
    }

    private void addRouteList() {
        try {
            InputStream is = this.getAssets().open("my_route.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "euc-kr"));

            String line;

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
        } catch (IOException e) {
            Toast.makeText(this, "Fail ToT", Toast.LENGTH_SHORT).show();
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
        } catch (IOException e) {
            Toast.makeText(this, "Fail ToT", Toast.LENGTH_SHORT).show();
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
        } catch (IOException e) {
            Toast.makeText(this, "Fail ToT", Toast.LENGTH_SHORT).show();
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
    public void isHasVisited(ContextWrapper cw){
        pref = cw.getSharedPreferences(MY_DB, Context.MODE_PRIVATE);

        boolean hasVisited = pref.getBoolean(HasVisited, false);
        if(!hasVisited){
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

    private void DialogSelectOption() {
        final String items[] = { "정상운행", "공차", "막차" };
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("운행 구분");
        ab.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mv.setDriveDivision(whichButton);
                        Toast.makeText(getApplicationContext(), ""+whichButton , Toast.LENGTH_SHORT).show();
                    }
                }).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // OK 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
//                        Toast.makeText(getApplicationContext(), ""+whichButton , Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), RouteStationActivity.class);
                        i.putExtra("routeInfo", text);
                        startActivity(i);
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Cancel 버튼 클릭시
                    }
                });
        ab.show();
    }

}
