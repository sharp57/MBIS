package neighbor.com.mbis.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import neighbor.com.mbis.R;

/**
 * Created by 권오철 on 2017-02-10.
 */

public class EmergencyActivity extends Activity implements View.OnClickListener{

    private ImageView emergencyMenuImg01, emergencyMenuImg02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        setInit();
    }
    private void setInit(){
        emergencyMenuImg01 = (ImageView) findViewById(R.id.emergencyMenuImg01);
        emergencyMenuImg02 = (ImageView) findViewById(R.id.emergencyMenuImg02);

        emergencyMenuImg01.setOnClickListener(this);
        emergencyMenuImg02.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emergencyMenuImg01:
                break;
            case R.id.emergencyMenuImg02:
                break;
        }
    }
}
