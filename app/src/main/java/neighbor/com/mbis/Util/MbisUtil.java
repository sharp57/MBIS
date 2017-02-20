package neighbor.com.mbis.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import neighbor.com.mbis.maputil.Data;
import neighbor.com.mbis.network.SocketConnect;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 권오철 on 2017-02-10.
 */

public class MbisUtil {
    final static  String PREF = "mbis";
    public final static String isAppFinish = "isAppFinish";
    public final static String version = "version";
    final static boolean isDebug = true;

    public static void sendData(Handler handler){
        SocketConnect socketConnect = new SocketConnect();
        socketConnect.setSocket(handler ,  Data.writeData);
        socketConnect.start();
    }
    // 값 불러오기
    public static boolean getPreferencesBoolean(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }
    // 값 불러오기
    public static int getPreferencesInt(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    // 값 저장하기
    public static void setPreferencesBoolean(Context context, String key, boolean value){
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    // 값 저장하기
    public static void setPreferencesInt(Context context, String key, int value){
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static void log(Context context, String debug){
        if(isDebug) Log.e(context.getClass().toString(),debug);
    }
}
