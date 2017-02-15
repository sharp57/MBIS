package neighbor.com.mbis.MapUtil.Thread;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import neighbor.com.mbis.MapUtil.HandlerPosition;
import neighbor.com.mbis.MapUtil.Value.LogicBuffer;

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
