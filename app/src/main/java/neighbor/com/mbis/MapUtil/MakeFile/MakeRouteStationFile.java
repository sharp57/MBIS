package neighbor.com.mbis.MapUtil.MakeFile;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

import neighbor.com.mbis.Function.FileManager;
import neighbor.com.mbis.Function.Func;
import neighbor.com.mbis.MapUtil.BytePosition;
import neighbor.com.mbis.MapUtil.Data;
import neighbor.com.mbis.MapUtil.Value.MapVal;

/**
 * Created by user on 2016-10-25.
 */
public class MakeRouteStationFile {
    MapVal mv = MapVal.getInstance();
    public MakeRouteStationFile() {
        makeRSFile();
    }
    private void makeRSFile() {
        byte[] arr = new byte[0];

        for (int i = 0; i < Data.readData.length; i++) {
//            dataView.append(String.format("%02d ", Data.readData[i]));
            if (i > BytePosition.BODY_ROUTE_STATION_DATA_START - 1 && !(i > Data.readData.length - 5)) {
                arr = Func.mergyByte(arr, new byte[]{Data.readData[i]});
            }
        }

        ArrayList<Buf_RS> BufRSArr = new ArrayList<Buf_RS>();

        FileManager fm = new FileManager(mv.getApplyDate_RS() + mv.getApplyTime_RS() + "-" + "RS", "csv");

        File f = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        if (!f.exists()) {
            return;
        }
        File[] allFiles = f.listFiles();

        for (File file : allFiles) {
            if (file.getName().endsWith("-RS.csv")) {
                file.delete();
            }
        }

        for (int i = 0; i < mv.getTotalStationNum_RS(); i++) {
            byte[] order = new byte[2];
            byte[] id = new byte[5];
            byte[] dis = new byte[2];
            byte[] time = new byte[2];

            Buf_RS b = new Buf_RS();

            System.arraycopy(arr, BytePosition.BODY_ROUTE_STATION_DATA_SIZE * i, order, 0, order.length);
            System.arraycopy(arr, BytePosition.BODY_ROUTE_STATION_DATA_SIZE * i + order.length, id, 0, id.length);
            System.arraycopy(arr, BytePosition.BODY_ROUTE_STATION_DATA_SIZE * i + order.length + id.length, dis, 0, dis.length);
            System.arraycopy(arr, BytePosition.BODY_ROUTE_STATION_DATA_SIZE * i + order.length + id.length + dis.length, time, 0, time.length);

            b.setStationOrder(Func.byteToInteger(order, order.length));
            b.setStationID(Func.byteToLong(id));
            b.setStationDistance(Func.byteToInteger(dis, dis.length));
            b.setStationTime(Func.byteToInteger(time, time.length));

            BufRSArr.add(b);

            fm.saveData(mv.getRouteID_RS() + ","
                    + mv.getRouteForm_RS() + ","
                    + b.getStationID() + ","
                    + b.getStationOrder() + ","
                    + b.getStationDistance() + ","
                    + b.getStationTime() + ","
            );
        }
    }
}
