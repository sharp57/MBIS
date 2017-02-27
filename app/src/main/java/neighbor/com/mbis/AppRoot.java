package neighbor.com.mbis;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import org.apache.log4j.Logger;

import neighbor.com.mbis.managers.SettingsStore;
import neighbor.com.mbis.util.LogUtils;

/**
 * Created by ts.ha on 2017-02-17.
 */

public class AppRoot extends Application {

    private static final String TAG = AppRoot.class.getSimpleName();
//    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
//        handleException();
        // Configure log
        LogUtils.configLog(this);
        // init settings preferences
        SettingsStore.init(this);
    }

    /**
     * 모든 에러를 firebase 로 보냄
     */
    private void handleException() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            public void uncaughtException(Thread thread, Throwable e) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "NPE caught");
                FirebaseCrash.report(e);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        //휴대폰 메모리가 없는 경우 TRIM_MEMORY_COMPLETE
        if (level == TRIM_MEMORY_COMPLETE) {

        }else if(level == TRIM_MEMORY_UI_HIDDEN){
            Logger.getLogger(TAG).debug("TRIM_MEMORY_UI_HIDDEN");
        }
    }

}
