package neighbor.com.mbis.network;

import java.util.Calendar;
import java.util.TimeZone;

import neighbor.com.mbis.managers.FileManager;

import static java.lang.String.format;

/**
 * Created by ts.ha on 2017-02-22.
 */

public class RecvDataUtil {
    private static String DATE_FORMAT = "%02d";


    public static void saveRecvData(byte[] data) {
        String dd = "";
        for (byte aData : data) {
            dd = dd + format("%02x ", aData);
        }
        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);
        String packetFileName = new StringBuilder().append(format(DATE_FORMAT, cal.get(Calendar.YEAR) - 2000)).
                append(format(DATE_FORMAT, (cal.get(Calendar.MONTH) + 1))).
                append(format(DATE_FORMAT, cal.get(Calendar.DATE))).
                append(" packet").toString();


        FileManager eventFileManager = new FileManager(packetFileName);
        eventFileManager.saveData("\n(" + cal.get(Calendar.YEAR) + ":" + (cal.get(Calendar.MONTH) + 1) + ":" + cal.get(Calendar.DATE) +
                " - " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) +
                ")\n[RECV:" + data.length + "] - " + dd);
    }

}
