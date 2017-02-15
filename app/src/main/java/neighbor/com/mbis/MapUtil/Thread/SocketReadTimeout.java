package neighbor.com.mbis.MapUtil.Thread;

import android.os.CountDownTimer;
import android.os.Handler;

import neighbor.com.mbis.MapUtil.HandlerPosition;

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
