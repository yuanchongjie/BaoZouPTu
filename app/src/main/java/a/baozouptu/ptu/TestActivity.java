package a.baozouptu.ptu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.File;

import a.baozouptu.R;
import a.baozouptu.common.appInfo.AppConfig;

/**
 * Created by LiuGuicen on 2016/12/26 0026.
 */
public class TestActivity extends AppCompatActivity {
    String picPath;
    Tencent myTencent;
    static{
        
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testOpenCV();
    }

    private static void testOpenCV() {
    }
}
/*

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
