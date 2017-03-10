package a.baozouptu.ptu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import a.baozouptu.R;
import a.baozouptu.common.appInfo.AppConfig;
import a.baozouptu.ptu.view.ColorBar;
import a.baozouptu.ptu.view.ColorPicker;

/**
 * Created by LiuGuicen on 2016/12/26 0026.
 */
public class TestActivity extends AppCompatActivity {
    String picPath;
    Tencent myTencent;


    private ColorPicker colorPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        colorPicker = new ColorPicker(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private static void testOpenCV() {
    }

}
/*
线程名称:main
a.baozouptu.ptu.draw.DrawFragment.onCreateView(DrawFragment.java:122)
android.app.Fragment.performCreateView(Fragment.java:2226)
android.app.FragmentManagerImpl.moveToState(FragmentManager.java:978)
android.app.FragmentManagerImpl.moveToState(FragmentManager.java:1157)
android.app.BackStackRecord.run(BackStackRecord.java:793)
android.app.FragmentManagerImpl.execPendingActions(FragmentManager.java:1544)
android.app.FragmentManagerImpl$1.run(FragmentManager.java:483)
android.os.Handler.handleCallback(Handler.java:743)
android.os.Handler.dispatchMessage(Handler.java:95)
android.os.Looper.loop(Looper.java:150)
android.app.ActivityThread.main(ActivityThread.java:5665)
java.lang.reflect.Method.invoke(Native Method)
com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:822)
com.android.internal.os.ZygoteInit.main(ZygoteInit.java:712)
异常信息：类:
android.support.constraint.ConstraintLayout cannot be cast to android.widget.LinearLayout

        setContentView(R.layout.test);
        picPath= Environment.getExternalStorageDirectory()+"/test.jpg";
        myTencent = Tencent.createInstance("1105572903", this.getApplicationContext());
        findViewById(R.id.shearToQQ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShare();
            }
        });
        Bitmap bitmap= BitmapFactory.decodeFile(picPath);
        ((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);
    }

    private void onClickShare() {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,picPath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "暴走P图");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        myTencent.shareToQQ(this, params, new BaseUiListener());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode,resultCode,data,new BaseUiListener());
    }

    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            Toast.makeText(TestActivity.this,"onComplete:",Toast.LENGTH_LONG).show();
            doComplete((JSONObject)o);
        }
        protected void doComplete(JSONObject values) {
        }
        @Override
        public void onError(UiError e) {
            Toast.makeText(TestActivity.this,"onError:"+"code:" + e.errorCode + ", msg:"
                    + e.errorMessage + "+detail:" + e.errorDetail,Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCancel() {
            Toast.makeText(TestActivity.this,"onCancel",Toast.LENGTH_LONG).show();
        }
    }
}
*/
