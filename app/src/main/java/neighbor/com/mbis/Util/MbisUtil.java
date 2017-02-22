package neighbor.com.mbis.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

import neighbor.com.mbis.managers.FileManager;
import neighbor.com.mbis.views.maputil.Data;
import neighbor.com.mbis.models.value.MapVal;
import neighbor.com.mbis.network.SocketConnect;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 권오철 on 2017-02-10.
 */

public class MbisUtil {
    final static String PREF = "mbis";
    public final static String isAppFinish = "isAppFinish";
    public final static String version_route = "version_route";
    public final static String version_node = "version_node";
    public final static String version_routestop = "version_routestop";
    final static boolean isDebug = true;
    static FileManager eventFileManager;
    static MapVal mv = MapVal.getInstance();

    public static void reveData() {

        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);
        String packetFileName = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE)) + " packet";
        eventFileManager = new FileManager(packetFileName);
        String dd = "";
        for (int i = 0; i < Data.readData.length; i++) {
            dd = dd + String.format("%02x ", Data.readData[i]);
//            readText.append(String.format("%02x ", Data.readData[i]));
        }
        eventFileManager.saveData("\n(" + cal.get(Calendar.YEAR) + ":" + (cal.get(Calendar.MONTH) + 1) + ":" + cal.get(Calendar.DATE) +
                " - " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) +
                ")\n[RECV:" + Data.readData.length + "] - " + dd);
    }

    public static void sendData(Handler handler, int successCode, int failCode) {

        TimeZone jst = TimeZone.getTimeZone("JST");
        Calendar cal = Calendar.getInstance(jst);
        String packetFileName = String.format("%02d", cal.get(Calendar.YEAR) - 2000) + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + String.format("%02d", cal.get(Calendar.DATE)) + " packet";
        eventFileManager = new FileManager(packetFileName);
        String dd = "";
        for (int j = 0; j < Data.writeData.length; j++) {
            dd = dd + String.format("%02X ", Data.writeData[j]);
        }
        eventFileManager.saveData("\n(" + (mv.getSendYear() - 2000) + "." + mv.getSendMonth() + "." + mv.getSendDay() +
                " - " + (mv.getSendHour() + 9) + ":" + mv.getSendMin() + ":" + mv.getSendSec() +
                ")\n[SEND:" + Data.writeData.length + "] - " + dd);

        SocketConnect socketConnect = new SocketConnect();
        socketConnect.setSocket(handler, Data.writeData, successCode, failCode);
        socketConnect.start();
    }

    // 값 불러오기
    public static boolean getPreferencesBoolean(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    // 값 불러오기
    public static int getPreferencesInt(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    // 값 저장하기
    public static void setPreferencesBoolean(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // 값 저장하기
    public static void setPreferencesInt(Context context, String key, int value) {
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static void log(Context context, String debug) {
        if (isDebug) Log.e(context.getClass().toString(), debug);
    }
}
