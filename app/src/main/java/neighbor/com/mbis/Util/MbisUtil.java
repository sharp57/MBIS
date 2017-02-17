package neighbor.com.mbis.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 권오철 on 2017-02-10.
 */

public class MbisUtil {
    final static  String PREF = "mbis";
    final static boolean isDebug = true;

    // 값 불러오기
    public static boolean getPreferences(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    // 값 저장하기
    public static void setPreferences(Context context, String key, boolean value){
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void log(Context context, String debug){
        if(isDebug) Log.e(context.getClass().toString(),debug);
    }
}
