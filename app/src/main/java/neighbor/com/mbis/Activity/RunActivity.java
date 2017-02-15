package neighbor.com.mbis.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import neighbor.com.mbis.R;

/**
 * Created by 권오철 on 2017-02-08.
 */

public class RunActivity extends Activity implements View.OnClickListener{
    private TextView prevStationLabel, nextStationLabel;
    private Button emergencyLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        setInit();
    }
    private void setInit(){
        prevStationLabel = (TextView) findViewById(R.id.prevStationLabel);
        nextStationLabel = (TextView) findViewById(R.id.nextStationLabel);
        emergencyLabel = (Button) findViewById(R.id.emergency_label);
        nextStationLabel.setSelected(true);
        prevStationLabel.setSelected(true);

        emergencyLabel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emergency_label:
                startActivity(new Intent(RunActivity.this,EmergencyActivity.class));
                break;
        }
    }
}
