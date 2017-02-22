package neighbor.com.mbis.views.maputil.thread;

import android.os.CountDownTimer;
import android.os.Handler;

import neighbor.com.mbis.views.maputil.HandlerPosition;

/**
 * Created by user on 2016-09-29.
 */

public class SocketReadTimeout extends CountDownTimer {
    Handler mHandler;
    public SocketReadTimeout(long millisInFuture,  Handler mHandler) {
        super(millisInFuture, 1000 * 60);
        this.mHandler = mHandler;

    }

    @Override
    public void onTick(long l) {
    }

    @Override
    public void onFinish() {
        mHandler.sendEmptyMessage(HandlerPosition.READ_TIMEOUT_ERROR);
    }
}
