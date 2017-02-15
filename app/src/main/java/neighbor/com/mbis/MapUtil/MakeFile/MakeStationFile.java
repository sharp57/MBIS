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
public class MakeStationFile {
    MapVal mv = MapVal.getInstance();

    public MakeStationFile() {
        makeSFile();
    }

    private void makeSFile() {
        byte[] arr = new byte[0];

        for (int i = 0; i < Data.readData.length; i++) {
//            dataView.append(String.format("%02d ", Data.readData[i]));
            if (i > BytePosition.BODY_STATION_DATA_START - 1 && !(i > Data.readData.length - 5)) {
                arr = Func.mergyByte(arr, new byte[]{Data.readData[i]});
            }
        }

        ArrayList<Buf_S> BufSArr = new ArrayList<Buf_S>();

        FileManager fm = new FileManager(mv.getApplyDate_S() + mv.getApplyTime_S() + "-" + "S", "csv");

        File f = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        if (!f.exists()) {
            return;
        }
        File[] allFiles = f.listFiles();

        for (File file : allFiles) {
            if (file.getName().endsWith("-S.csv")) {
                file.delete();
            }
        }

        for (int i = 0; i < mv.getTotalStationNum_S(); i++) {
            byte[] station_id = new byte[5];
            byte[] station_name = new byte[30];
            byte[] station_type = new byte[2];
            byte[] station_angle = new byte[2];
            byte[] station_x = new byte[4];
            byte[] station_y = new byte[4];
            byte[] station_arrive_distance = new byte[2];
            byte[] station_start_distance = new byte[2];

            Buf_S b = new Buf_S();

            int k = 0;

            System.arraycopy(arr, BytePosition.BODY_STATION_DATA_SIZE * i + k
                    , station_id, 0, station_id.length);

            k += station_id.length;
            System.arraycopy(arr, BytePosition.BODY_STATION_DATA_SIZE * i + k
                    , station_name, 0, station_name.length);

            k += station_name.length;
            System.arraycopy(arr, BytePosition.BODY_STATION_DATA_SIZE * i + k
                    , station_type, 0, station_type.length);

            k += station_type.length;
            System.arraycopy(arr, BytePosition.BODY_STATION_DATA_SIZE * i + k
                    , station_angle, 0, station_angle.length);

            k += station_angle.length;
            System.arraycopy(arr, BytePosition.BODY_STATION_DATA_SIZE * i + k
                    , station_x, 0, station_x.length);

            k += station_x.length;
            System.arraycopy(arr, BytePosition.BODY_STATION_DATA_SIZE * i + k
                    , station_y, 0, station_y.length);

            k += station_y.length;
            System.arraycopy(arr, BytePosition.BODY_STATION_DATA_SIZE * i + k
                    , station_arrive_distance, 0, station_arrive_distance.length);

            k += station_arrive_distance.length;
            System.arraycopy(arr, BytePosition.BODY_STATION_DATA_SIZE * i + k
                    , station_start_distance, 0, station_start_distance.length);

            BufSArr.add(b);

            b.setStationID(Func.byteToLong(station_id));
            try {
                b.setStationName(new String(station_name, "EUC-KR").trim());
                b.setStationType(Func.byteToInteger(station_type, station_type.length));
                b.setStationAngle(Func.byteToInteger(station_angle, station_angle.length));
                b.setStationX(Double.toString(Func.byteToInteger(station_x, 4)*0.00001));
                b.setStationY(Double.toString(Func.byteToInteger(station_y, 4)*0.00001));
                b.setStationArriveDistance(Func.byteToInteger(station_arrive_distance, station_arrive_distance.length));
                b.setStationStartDistance(Func.byteToInteger(station_start_distance, station_start_distance.length));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            fm.saveData(b.getStationID() + ","
                    + b.getStationName() + ","
                    + b.getStationType() + ","
                    + b.getStationAngle() + ","
                    + b.getStationX() + ","
                    + b.getStationY() + ","
                    + b.getStationArriveDistance() + ","
                    + b.getStationStartDistance() + ","
            );
        }
    }

}
