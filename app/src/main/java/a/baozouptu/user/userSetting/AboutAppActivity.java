package a.baozouptu.user.userSetting;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.common.BaseActivity;
import a.baozouptu.common.appInfo.AppConfig;
import a.baozouptu.common.dataAndLogic.AllData;

public class AboutAppActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ((TextView) findViewById(R.id.about_version)).
                setText("暴走P图 " + AppConfig.APPVERSION_1_1 + ".0");

        findViewById(R.id.about_return_btn).
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }
                );

    }

}
