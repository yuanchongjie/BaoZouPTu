package a.baozouptu.ptu.saveAndShare;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import a.baozouptu.ptu.TestActivity;

/**
 * Created by LiuGuicen on 2016/12/26 0026.
 * 专门用于qq分享的类
 */
public class MyQQShare extends Fragment {
    Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("QQShare","OnCreate发生调用");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        Log.e("QQShare","onDestroy发生调用");
        super.onDestroy();
    }

    public void share(String picPath, Context context) {
        Tencent myTencent;
        mContext = context;
        myTencent = Tencent.createInstance("1105572903", mContext.getApplicationContext());
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, picPath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "暴走P图");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        myTencent.shareToQQ((Activity)mContext, params, new BaseUiListener());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode,resultCode,data,new  BaseUiListener());
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }

    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            //Toast.makeText(mContext, "onComplete:", Toast.LENGTH_LONG).show();
            doComplete((JSONObject) o);
        }

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onError(UiError e) {
          /*  Toast.makeText(mContext, "onError:" + "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + "+detail:" + e.errorDetail, Toast.LENGTH_LONG).show();*/
            Toast.makeText(mContext, "分享出错了！", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
          //  Toast.makeText(mContext, "onCancel", Toast.LENGTH_LONG).show();
        }
    }
}
