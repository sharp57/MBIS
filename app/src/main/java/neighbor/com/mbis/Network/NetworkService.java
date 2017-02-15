//package neighbor.com.mbis.Network;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.Handler;
//import android.os.IBinder;
//
//import neighbor.com.mbis.Activity.LoginActivity;
//import neighbor.com.mbis.MapUtil.Thread.SocketNetwork;
//
//
//public class NetworkService extends Service {
//
//    private SocketNetwork soc;
//    private final IBinder mBinder = new MainServiceBinder();
//
//    //서비스 바인더 내부 클래스 선언
//    public class MainServiceBinder extends Binder {
//        public NetworkService getService() {
//            return NetworkService.this; //현재 서비스를 반환.
//        }
//    }
//
//    public NetworkService() {
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        soc.close();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return START_NOT_STICKY;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        soc = new SocketNetwork(NetworkUtil.IP, NetworkUtil.PORT, LoginActivity.handler);
//        soc.start();
//
//        return mBinder;
//    }
//
//    public void sendData() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                soc.writeData();
//            }
//        }).start();
//
//    }
//
//    public void setHandler(Handler handler) {
//        soc.setHandler(handler);
//    }
//
//
//}
