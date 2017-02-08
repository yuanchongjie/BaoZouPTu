package a.baozouptu.common.appInfo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import a.baozouptu.common.dataAndLogic.MyDatabase;
import a.baozouptu.common.util.CustomToast;

import static a.baozouptu.common.dataAndLogic.AllData.appConfig;

/**
 * Created by liuguicen on 2016/8/13.
 */
public class InstallPolicy {
    Context mGlobalContext;
    private MyDatabase myDatabase;

    public InstallPolicy() {
        mGlobalContext = MyApplication.appContext;
    }

    /**
     * 执行第一次安装或更新新版本所需的东西
     * 注意，1.0版本没有写入版本号，需要额外判断
     */
    public void processPolicy() {

        int lastVersion = appConfig.readAppVersion();

        if (AppConfig.CUR_APP_VERSION == lastVersion) {//已经更新版本数据，或者新安装的版本相同
            return;
        } else if (AppConfig.CUR_APP_VERSION < lastVersion) {//更新的版本小于当前安装好的版本
            Toast.makeText(MyApplication.appContext,"更新的版本过低，请安装较新版本",Toast.LENGTH_LONG).show();
        } else {//执行版本更新操作=》是大于，不是==或《=

            if (lastVersion == -1 && !appConfig.hasNewInstall())//是新安装
            {
                createAppFile();
                setShearInfo();
            }
//            执行版本更新操作
// 清除旧版本信息，写入新版本信息,注意只需要秦楚或者写入其中一个
            if (appConfig.hasNewInstall())//是1.0版本
            {
                clearOldVersionInfo_1_0();
                setShearInfo();
            } else {//执行其他版本的
                    /*
                   if(appVersion==AppConfig.APP_VERSION_2){

                   }*/
            }
            writeCurVersionInfo();
            appConfig.writeCurAppVersion();
        }
    }

    private void writeCurVersionInfo() {
        //每次更新之后重新上传一次设备信息，因为版本已经更新，
        //顺便还有os版本等更新的检查
        //后面后台线程检测到false，就会自动更新了
        appConfig.writeSendDeviceInfo(false);
    }

    /**
     * 清除1.0的旧版本的信息
     */
    private void clearOldVersionInfo_1_0() {
        appConfig.clearOldVersionInfo_1_0();//清除旧版本配置信息
    }


    /**
     * 设置分享的信息
     */
    private void setShearInfo() {
        MyDatabase myDatabase = null;
        try {//添加分享的优先选项
            String[] packageNames = new String[]{"com.tencent.mm", "com.immomo.momo", "com.tencent.mobileqq",
                    "com.tencent.mm", "com.sina.weibo", "com.tencent.mm", "com.tencent.mobileqq"};
            String[] shareTitles = new String[]{"添加到微信收藏", "陌陌", "保存到QQ收藏",
                    "发送到朋友圈", "微博", "发送给朋友", "发送给好友"};
            myDatabase = MyDatabase.getInstance(mGlobalContext);
            long time = System.currentTimeMillis();
            for (int i = 0; i < packageNames.length; i++)
                myDatabase.insertPreferShare(packageNames[i], shareTitles[i], time++);
        } catch (IOException e) {
            Log.e("数据库", e.getMessage());
        } finally {
            myDatabase.close();
        }

    }

    /**
     * 创建App的文件
     */
    private void createAppFile() {

    }

}
