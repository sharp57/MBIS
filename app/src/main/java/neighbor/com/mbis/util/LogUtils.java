package neighbor.com.mbis.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.log4j.DailyMaxRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogCatAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import neighbor.com.mbis.BuildConfig;

public class LogUtils {

    private static final String TAG = "log-utils";

    static final String ROOT_LOG_FILE_NAME = BuildConfig.APPLICATION_ID+"-log.txt";

    public static File getLogDir(Context context) {
        File dir = context.getExternalCacheDir();
        if (dir == null) {
            dir = context.getCacheDir();
        }
        if (dir != null) {
            dir = new File(dir, "log");
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    Log.d(TAG, "make dir: " + dir);
                }
            }
        }
        return dir;
    }

    public static int reset(Context context) {
        Log.i(TAG, "reset log...");
        int count = 0;
        File dir = getLogDir(context);
        if (dir != null) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        count++;
                    }
                }
            }
        }
        configLog(context);
        return count;
    }

    public static void configLog(Context context) {
        LogManager.getLoggerRepository().resetConfiguration();

        // LogCat appender
        String logCatPattern = "%m [%F:%L]%n";
        configureLogCatAppender(logCatPattern);

        final Logger root = Logger.getRootLogger();


        // File appender
        if (BuildConfig.DEBUG) {
            File dir = LogUtils.getLogDir(context);
            File file = new File(dir, ROOT_LOG_FILE_NAME);
            //String filePattern = "[%-5p] [%d{yy-MM-dd HH:mm:ss}] [%-25.25c] - %m%n";
            //String filePattern = "[%d{yy-MM-dd HH:mm:ss}][%-5p][%M(%F:%L)] - %m%n";
            String filePattern = "[%d{yy-MM-dd HH:mm:ss}][%-5p][%-25.25c] - %m%n";
            String datePattern = ".yyyy-MM-dd";
            configureFileAppender(root, filePattern, file.getAbsolutePath(), datePattern, 1);
        }

        // Log level
        if (BuildConfig.DEBUG) {
            root.setLevel(Level.ALL);
        } else {
            root.setLevel(Level.DEBUG);
        }
    }

    private static void configureFileAppender(Logger logger, String filePattern, String filePath,
                                              String datePattern, int maxBackupDay) {
        final DailyMaxRollingFileAppender appender;
        final Layout layout = new PatternLayout(filePattern);

        try {
            appender = new DailyMaxRollingFileAppender(layout, filePath, datePattern);
        } catch (final IOException e) {
            throw new RuntimeException("Exception configuring log system", e);
        }

        appender.setMaxBackupDay(maxBackupDay);
        appender.setImmediateFlush(true);

        logger.addAppender(appender);
    }

    private static void configureLogCatAppender(String logCatPattern) {
        final Logger root = Logger.getRootLogger();
        final Layout layout = new PatternLayout(logCatPattern);
        final LogCatAppender appender = new LogCatAppender(layout);

        root.addAppender(appender);
    }

    public static File[] getLogFiles(Context context) {
        File dir = getLogDir(context);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.equals(ROOT_LOG_FILE_NAME);
            }
        });
        return files;
    }

    public static String buildStackTrack(Throwable th) {
        StringWriter body = new StringWriter();
        PrintWriter writer = new PrintWriter(body, true);
        writer.write("[Stack Trace]\n");
        if (th != null) {
            th.printStackTrace(writer);
        }
        writer.write("\n");
        writer.close();
        return body.toString();
    }

    public static String buildPackageLog(Context context) {
        StringBuilder buffer = new StringBuilder("[PACKAGE]\n");
        String pkgName = context.getPackageName();
        buffer.append("\t- Package: " + pkgName + "\n");
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pkgInfo = pm.getPackageInfo(pkgName, 0);
            buffer.append("\t- App Name: " + pkgInfo.applicationInfo.loadLabel(pm) + "\n");
            buffer.append("\t- App Version: " + pkgInfo.versionName + "\n");
        } catch (NameNotFoundException e) {
        }

        return buffer.toString();
    }

    public static String buildPhoneLog() {
        StringBuilder buffer = new StringBuilder("[PHONE]\n");
        buffer.append("\t- Manufacturer: " + Build.MANUFACTURER + "\n");
        buffer.append("\t- Model: " + Build.MODEL + "\n");
        buffer.append("\t- SDK Version: " + Build.VERSION.RELEASE + "\n");
        return buffer.toString();
    }

    public static String buildPreferencesLog(Context context) {
        StringBuilder buffer = new StringBuilder("[PREFERENCES]\n");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        buffer.append(prefs.getAll().toString());

        return buffer.toString();
    }

}
