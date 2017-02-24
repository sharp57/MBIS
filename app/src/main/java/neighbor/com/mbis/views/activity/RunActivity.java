package neighbor.com.mbis.views.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

    @Override
    public void onBackPressed() {
        isFinish();
//        super.onBackPressed();
    }

    /**
     * Infalter 다이얼로그
     * @return ab
     */
    private void isFinish() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("알림");
        ab.setMessage("노선 선택 화면으로 이동하시겠습니까?");

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int arg1) {
//                startActivity(new Intent(RunActivity.this,SelectRouteActivityNew.class));
                finish();
            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int arg1) {
                dlg.dismiss();
            }
        });

        ab.show();
    }

}
