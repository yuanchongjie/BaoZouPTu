package a.baozouptu.user.userSetting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.common.BaseActivity;
import a.baozouptu.common.appInfo.AppConfig;

public class AboutAppActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ((TextView) findViewById(R.id.about_version)).
                setText("暴走P图 " + AppConfig.CUR_VERSION_NAME + ".0");

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
