package neighbor.com.mbis.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ts.ha on 2017-02-22.
 */

public class SettingsStore {

    private static final String PREF_INTRODUCED = "introduced";
    private static final String PREF_DEVICE_ID = "PREF_DEVICE_ID";

    private static SettingsStore mInstance;

    private final SharedPreferences mPrefs;

    private final Context mContext;

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new SettingsStore(context);
        }
    }

    public static SettingsStore getInstance() {
        return mInstance;
    }

    private SettingsStore(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context.getApplicationContext();
    }

    public SharedPreferences getPreferences() {
        return mPrefs;
    }

    public void reset() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();
    }

    public static void commit() {
        if (mInstance != null) {
            boolean result = mInstance.mPrefs.edit().commit();
        }
    }


    public void putIntroduced(boolean introduced) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(PREF_INTRODUCED, introduced);
        editor.apply();
    }

    public boolean isIntroduced() {
        return mPrefs.getBoolean(PREF_INTRODUCED, false);
    }

    public void putDeviceId(long deviceID) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong(PREF_DEVICE_ID, deviceID);
        editor.apply();
    }
    public long getDeviceId() {
        return mPrefs.getLong(PREF_DEVICE_ID,0);
    }
}
