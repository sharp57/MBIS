package neighbor.com.mbis.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import neighbor.com.mbis.R;

public class StartScreen extends AppCompatActivity {

    SharedPreferences pref;
    private static final String MY_DB = "my_db";
    private static String HasVisited = "hasVisited";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // CALL_PHONE 권한을 Android OS 에 요청한다.
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//        }

        Handler h = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                startActivity(new Intent(StartScreen.this, LoginActivityNew.class));
                finish();
            }
        };
        h.sendEmptyMessageDelayed(0, 2000);
       /* h.sendEmptyMessageDelayed(0, 2500);*/
    }

}
