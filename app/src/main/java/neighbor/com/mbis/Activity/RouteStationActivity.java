package neighbor.com.mbis.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import neighbor.com.mbis.Database.DBManager;
import neighbor.com.mbis.Function.Func;
import neighbor.com.mbis.MapUtil.Value.RouteBuffer;
import neighbor.com.mbis.MapUtil.Value.StationSubBuffer_1;
import neighbor.com.mbis.MapUtil.Value.StationSubBuffer_2;
import neighbor.com.mbis.R;
import neighbor.com.mbis.MapUtil.Value.StationBuffer;

public class RouteStationActivity extends AppCompatActivity {

    public static Activity rsActivity;

    TextView tv;

    String key;
    DBManager db = DBManager.getInstance(this);

    StationBuffer sBuf = StationBuffer.getInstance();
    StationSubBuffer_1 ssBuf = StationSubBuffer_1.getInstance();
    StationSubBuffer_2 sssBuf = StationSubBuffer_2.getInstance();

    RouteBuffer rBuf = RouteBuffer.getInstance();

    String[] routeStationDB = new String[]{"station_id", "station_order"};
    String[] stationDB = new String[]{"station_id", "station_name", "station_type", "station_x", "station_y"};

    Cursor c;
    Cursor cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_station);


        rsActivity = RouteStationActivity.this;

        tv = (TextView) findViewById(R.id.myLog);

        Intent intent = getIntent();
        key = intent.getStringExtra("routeInfo");


        c = db.queryRouteStation(
                routeStationDB,
                "route_id=? and route_form=? ",
                new String[]{key, Integer.toString(rBuf.getRouteType())},
                null,
                null,
                "station_order"
        );
        int k = 0;
        if (rBuf.getRouteType() == 1) {
            k = 2;
        } else if (rBuf.getRouteType() == 2) {
            k = 1;
        }
        cc = db.queryRouteStation(
                routeStationDB,
                "route_id=? and route_form=? ",
                new String[]{key, Integer.toString(k)},
                null,
                null,
                "station_order"
        );

//        Cursor c = rsDB.myQuery(key);

        dbController();
        findViewById(R.id.goMap).performClick();
    }

    private void dbController() {

        if (c != null) {
            while (c.moveToNext()) {
                String sid = c.getString(0);
                sBuf.addStationOrder(Integer.parseInt(c.getString(1)));
//                sBuf.addRemark(Integer.parseInt(c.getString(3)));
                ssBuf.addStationOrder(Integer.parseInt(c.getString(1)));
//                ssBuf.addRemark(Integer.parseInt(c.getString(3)));

                Cursor cs = db.queryStation(
                        stationDB,
                        "station_id=?",
                        new String[]{sid},
                        null,
                        null,
                        null
                );

                if (cs != null) {
                    while (cs.moveToNext()) {
                        String idx[] = new String[stationDB.length];
                        for (int i = 0; i < idx.length; i++) {
                            idx[i] = cs.getString(i);
                        }
                        tv.append("[ 0 : " + idx[0] + " ] ");
                        tv.append("[ 1 : " + idx[1] + " ] ");
                        tv.append("[ 2 : " + idx[2] + " ] ");
                        tv.append("[ 3 : " + idx[3] + " ] ");
                        tv.append("[ 4 : " + idx[4] + " ] ");
                        tv.append("\n\n");

                        sBuf.getReferenceStationId().add(Long.parseLong(idx[0]));
                        sBuf.getReferenceStationName().add(idx[1]);
                        sBuf.getStationDivision().add(Integer.parseInt(idx[2]));
                        sBuf.getReferenceLatPosition().add(Double.parseDouble(idx[3]));
                        sBuf.getReferenceLngPosition().add(Double.parseDouble(idx[4]));

                        ssBuf.getReferenceStationId().add(Long.parseLong(idx[0]));
                        ssBuf.getReferenceStationName().add(idx[1]);
                        ssBuf.getStationDivision().add(Integer.parseInt(idx[2]));
                        ssBuf.getReferenceLatPosition().add(Double.parseDouble(idx[3]));
                        ssBuf.getReferenceLngPosition().add(Double.parseDouble(idx[4]));


//                        sBuf.getReferenceLatPosition().add(Double.parseDouble(idx[5]));
//                        sBuf.getReferenceLngPosition().add(Double.parseDouble(idx[4]));
//                        sBuf.getReferenceStationId().add(Long.parseLong(idx[0]));
//                        sBuf.getStationDivision().add(Integer.parseInt(idx[6]));
//                        sBuf.getReferenceStationName().add(idx[1]);
//
//
//                        ssBuf.getReferenceLatPosition().add(Double.parseDouble(idx[5]));
//                        ssBuf.getReferenceLngPosition().add(Double.parseDouble(idx[4]));
//                        ssBuf.getReferenceStationId().add(Long.parseLong(idx[0]));
//                        ssBuf.getStationDivision().add(Integer.parseInt(idx[6]));
//                        ssBuf.getReferenceStationName().add(idx[1]);
                    }
                }
            }
            for (int i = 0; i < sBuf.getReferenceStationId().size(); i++) {
                if ((i + 1) < sBuf.getReferenceStationId().size()) {
                    sBuf.getRemark().add(
                            Func.getDistance(
                                    sBuf.getReferenceLatPosition().get(i),
                                    sBuf.getReferenceLngPosition().get(i),
                                    sBuf.getReferenceLatPosition().get(i + 1),
                                    sBuf.getReferenceLngPosition().get(i + 1)
                            ) * 2
                    );

                    ssBuf.getRemark().add(
                            Func.getDistance(
                                    ssBuf.getReferenceLatPosition().get(i),
                                    ssBuf.getReferenceLngPosition().get(i),
                                    ssBuf.getReferenceLatPosition().get(i + 1),
                                    ssBuf.getReferenceLngPosition().get(i + 1)
                            ) * 2
                    );
                } else {
                    sBuf.getRemark().add(sBuf.getRemark().get(i - 1));
                    ssBuf.getRemark().add(ssBuf.getRemark().get(i - 1));
                }
            }
            c.close();
        }

        if (cc != null) {
            while (cc.moveToNext()) {
                String sid = cc.getString(0);
                sssBuf.addStationOrder(Integer.parseInt(cc.getString(1)));

                Cursor cs = db.queryStation(
                        stationDB,
                        "station_id=?",
                        new String[]{sid},
                        null,
                        null,
                        null
                );
                if (cs != null) {
                    while (cs.moveToNext()) {
                        String idx[] = new String[stationDB.length];
                        for (int i = 0; i < idx.length; i++) {
                            idx[i] = cs.getString(i);
                        }
                        tv.append("[ 0 : " + idx[0] + " ] ");
                        tv.append("[ 1 : " + idx[1] + " ] ");
                        tv.append("[ 2 : " + idx[2] + " ] ");
                        tv.append("[ 3 : " + idx[3] + " ] ");
                        tv.append("[ 4 : " + idx[4] + " ] ");
                        tv.append("\n\n");

                        sssBuf.getReferenceStationId().add(Long.parseLong(idx[0]));
                        sssBuf.getReferenceStationName().add(idx[1]);
                        sssBuf.getStationDivision().add(Integer.parseInt(idx[2]));
                        sssBuf.getReferenceLatPosition().add(Double.parseDouble(idx[3]));
                        sssBuf.getReferenceLngPosition().add(Double.parseDouble(idx[4]));
                    }
                }


            }
            for (int i = 0; i < sssBuf.getReferenceStationId().size(); i++) {
                if ((i + 1) < sssBuf.getReferenceStationId().size()) {
                    sssBuf.getRemark().add(
                            Func.getDistance(
                                    sssBuf.getReferenceLatPosition().get(i),
                                    sssBuf.getReferenceLngPosition().get(i),
                                    sssBuf.getReferenceLatPosition().get(i + 1),
                                    sssBuf.getReferenceLngPosition().get(i + 1)
                            ) * 2
                    );
                } else {
                    sssBuf.getRemark().add(sssBuf.getRemark().get(i - 1));
                }
            }
            cc.close();
        }
    }

    public void goMap(View v) {
        switch (v.getId()) {
            case R.id.goMap:
                startActivity(new Intent(this, MapActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sBuf.clearAll();
        ssBuf.clearAll();
        sssBuf.clearAll();

    }
}
