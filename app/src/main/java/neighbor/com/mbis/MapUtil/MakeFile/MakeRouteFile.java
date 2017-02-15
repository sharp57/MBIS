package neighbor.com.mbis.MapUtil.MakeFile;

import android.os.Environment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import neighbor.com.mbis.Function.FileManager;
import neighbor.com.mbis.Function.Func;
import neighbor.com.mbis.MapUtil.BytePosition;
import neighbor.com.mbis.MapUtil.Data;
import neighbor.com.mbis.MapUtil.Value.MapVal;


/**
 * Created by user on 2016-10-25.
 */
public class MakeRouteFile {
    MapVal mv = MapVal.getInstance();

    public MakeRouteFile() {
        makeRFile();
    }

    private void makeRFile() {
        byte[] arr = new byte[0];

        for (int i = 0; i < Data.readData.length; i++) {
//            dataView.append(String.format("%02d ", Data.readData[i]));
            if (i > BytePosition.BODY_ROUTE_DATA_START - 1 && !(i > Data.readData.length - 5)) {
                arr = Func.mergyByte(arr, new byte[]{Data.readData[i]});
            }
        }

        ArrayList<Buf_R> BufRArr = new ArrayList<Buf_R>();

        FileManager fm = new FileManager(mv.getApplyDate_R() + mv.getApplyTime_R() + "-" + "R", "csv");

        File f = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        if (!f.exists()) {
            return;
        }
        File[] allFiles = f.listFiles();

        for (File file : allFiles) {
            if (file.getName().endsWith("-R.csv")) {
                file.delete();
            }
        }

        for (int i = 0; i < mv.getTotalRouteNum_R(); i++) {
            byte[] route_id = new byte[8];
            byte[] route_name1 = new byte[5];
            byte[] route_name2 = new byte[2];
            byte[] route_form = new byte[1];
            byte[] route_division = new byte[2];

            byte[] route_type = new byte[1];
            byte[] route_first_start_time = new byte[2];
            byte[] route_last_start_time = new byte[2];
            byte[] route_average_interval = new byte[2];
            byte[] route_average_time = new byte[2];

            byte[] route_length = new byte[2];
            byte[] route_station_num = new byte[1];
            byte[] route_start_station = new byte[30];
            byte[] route_important_station1 = new byte[30];
            byte[] route_important_station2 = new byte[30];
            byte[] route_last_station = new byte[30];


            Buf_R b = new Buf_R();
            int k = 0;

            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_id, 0, route_id.length);

            k += route_id.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_name1, 0, route_name1.length);

            k += route_name1.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_name2, 0, route_name2.length);

            k += route_name2.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_form, 0, route_form.length);

            k += route_form.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_division, 0, route_division.length);

            k += route_division.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_type, 0, route_type.length);

            k += route_type.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_first_start_time, 0, route_first_start_time.length);

            k += route_first_start_time.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_last_start_time, 0, route_last_start_time.length);

            k += route_last_start_time.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_average_interval, 0, route_average_interval.length);

            k += route_average_interval.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_average_time, 0, route_average_time.length);

            k += route_average_time.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_length, 0, route_length.length);

            k += route_length.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_station_num, 0, route_station_num.length);

            k += route_station_num.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_start_station, 0, route_start_station.length);

            k += route_start_station.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_important_station1, 0, route_important_station1.length);

            k += route_important_station1.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_important_station2, 0, route_important_station2.length);

            k += route_important_station2.length;
            System.arraycopy(arr, BytePosition.BODY_ROUTE_DATA_SIZE * i + k
                    , route_last_station, 0, route_last_station.length);

            b.setRouteID(Func.byteToLong(route_id));
            try {
                b.setRouteNum(new String(route_name1), new String(route_name2, "EUC-KR").trim());
                b.setRouteForm(Func.byteToInteger(route_form, route_form.length));
                b.setRouteType(Func.byteToInteger(route_type, route_type.length));
                b.setRouteFirstStartTime(Func.byteToInteger(route_first_start_time, route_first_start_time.length));
                b.setRouteLastStartTime(Func.byteToInteger(route_last_start_time, route_last_start_time.length));
                b.setRouteAverageInterval(Func.byteToInteger(route_average_interval, route_average_interval.length));
                b.setRouteAverageTime(Func.byteToInteger(route_average_time, route_average_time.length));
                b.setRouteLength(Func.byteToInteger(route_length, route_length.length));
                b.setRouteStationNum(Func.byteToInteger(route_station_num, route_station_num.length));
                b.setRouteStartStation(new String(route_start_station, "EUC-KR").trim());
                b.setRouteImportantStation1(new String(route_important_station1, "EUC-KR").trim());
                b.setRouteImportantStation2(new String(route_important_station2, "EUC-KR").trim());
                b.setRouteLastStation(new String(route_last_station, "EUC-KR").trim());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            BufRArr.add(b);

            fm.saveData(b.getRouteID() + ","
                    + b.getRouteNum() + ","
                    + b.getRouteForm() + ","
                    + b.getRouteType() + ","
                    + b.getRouteFirstStartTime() + ","
                    + b.getRouteLastStartTime() + ","
                    + b.getRouteAverageInterval() + ","
                    + b.getRouteAverageTime() + ","
                    + b.getRouteLength() + ","
                    + b.getRouteStationNum() + ","
                    + b.getRouteStartStation() + ","
                    + b.getRouteImportantStation1() + ","
                    + b.getRouteImportantStation2() + ","
                    + b.getRouteLastStation()
            );
        }
    }
}
