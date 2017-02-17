package neighbor.com.mbis.maputil.thread;

import android.os.CountDownTimer;
import android.os.Handler;

import neighbor.com.mbis.maputil.HandlerPosition;

/**
 * Created by user on 2016-09-21.
 */
public class BusTimer extends CountDownTimer {

    Handler mHandler;

    public BusTimer(long millisInFuture, Handler mHandler) {
        super(millisInFuture, 1000);
        this.mHandler = mHandler;
    }

    @Override
    public void onTick(long l) {
        mHandler.sendEmptyMessage(HandlerPosition.TIME_CHANGE);
    }

    @Override
    public void onFinish() {
        mHandler.sendEmptyMessage(HandlerPosition.SEND_BUS_LOCATION_INFO);
        cancel();
        start();
    }
}
