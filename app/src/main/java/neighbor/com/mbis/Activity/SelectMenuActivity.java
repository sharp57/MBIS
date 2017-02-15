package neighbor.com.mbis.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import neighbor.com.mbis.R;

/**
 * Created by 권오철 on 2017-02-08.
 */

public class SelectMenuActivity extends Activity implements View.OnClickListener{

    ImageView selectRoute, selectAuth, selectMode;
    ImageView selectRouteIcon, selectAuthIcon;
    TextView selectRouteLabel, selectAuthLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_menu);

        setInit();

    }
    private void setInit(){
        selectRoute = (ImageView) findViewById(R.id.selectRoute);
        selectRouteIcon = (ImageView) findViewById(R.id.selectRouteIcon);
        selectRouteLabel = (TextView) findViewById(R.id.selectRouteLabel);
        selectAuth = (ImageView) findViewById(R.id.selectAuth);
        selectAuthIcon = (ImageView) findViewById(R.id.selectAuthIcon);
        selectAuthLabel = (TextView) findViewById(R.id.selectAuthLabel);


        selectRoute.setOnClickListener(this);
        selectAuth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selectRoute:
                startActivity(new Intent(SelectMenuActivity.this,SelectRouteActivityNew.class));
                finish();
                break;
            case R.id.selectAuth:
                startActivity(new Intent(SelectMenuActivity.this,LoginActivityNew.class));
                finish();
                break;
        }
    }
}
