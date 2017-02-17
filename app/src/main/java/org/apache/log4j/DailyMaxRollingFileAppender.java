package org.apache.log4j;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DailyMaxRollingFileAppender extends DailyRollingFileAppender {

    protected int maxBackupDay = 1;

    public DailyMaxRollingFileAppender() {
    }

    public DailyMaxRollingFileAppender(Layout layout, String filename,
                                       String datePattern) throws IOException {
        super(layout, filename, datePattern);
    }

    public int getMaxBackupDay() {
        return maxBackupDay;
    }

    public void setMaxBackupDay(int maxBackupDay) {
        this.maxBackupDay = maxBackupDay;
    }

    @Override
    void rollOver() throws IOException {
        super.rollOver();

        int period = getPeriodicity();
        Calendar cal = Calendar.getInstance();
        cal.add(period, -1 * (maxBackupDay + 1));

        File file = new File(fileName);
        File dir = file.getParentFile();
        File[] files = dir.listFiles();

        String name = new File(fileName).getName();

        if (files != null && files.length > 0) {
            int count = 0;
            for (File f : files) {
                String dateString = f.getName().replace(name, "");
                try {
                    Date date = sdf.parse(dateString);
                    if (date.getTime() <= cal.getTimeInMillis()) {
                        if (f.delete()) {
                           count++;
                        }
                    }
                } catch (ParseException e) {
                }
            }
            Log.d("Log4j", "delete old files: " + count);
        }
    }

    protected int getPeriodicity() {
        switch (computeCheckPeriod()) {
            case TOP_OF_MINUTE:
                return Calendar.MINUTE;
            case TOP_OF_HOUR:
                return Calendar.HOUR_OF_DAY;
            case HALF_DAY:
                return Calendar.HOUR_OF_DAY;
            case TOP_OF_DAY:
                return Calendar.DAY_OF_MONTH;
            case TOP_OF_WEEK:
                return Calendar.WEEK_OF_YEAR;
            case TOP_OF_MONTH:
                return Calendar.MONTH;
            default:
                return Calendar.DAY_OF_MONTH;
        }
    }
}
