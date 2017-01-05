package a.baozouptu.common.appInfo;

import android.content.Context;

import java.io.IOException;

import a.baozouptu.common.dataAndLogic.MyDatabase;
import a.baozouptu.common.util.Util;

import static a.baozouptu.common.dataAndLogic.AllData.appConfig;

/**
 * Created by liuguicen on 2016/8/13.
 *
 */
public class InstallPolicy {
    Context mGlobalContext;
    private MyDatabase myDatabase;

    public InstallPolicy() {
        mGlobalContext = Util.MyApplication.getAppContext();
    }

    /**
     * 执行第一次安装或更新新版本所需的东西
     * 注意，1.0版本没有写入版本号，需要额外判断
     */
    public void processPolicy() {

        float appVersion = appConfig.readAppVersion();
        if (Float.compare(AppConfig.CUR_APPVERSION, appVersion) == 0) {
            return;//是当前版本,已完成版本更新，正常启动
        } else {

            if (appVersion == -1f && !appConfig.hasNewInstall())//是新安装
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
                   if(appVersion==AppConfig.APPVERSION_1_1){

                   }*/
            }
            writeCurVersionInfo();
            appConfig.writeCurAppVersion();
        }
    }

    private void writeCurVersionInfo() {
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
            String[] shareTitles = new String[]{"添加到微信收藏", "陌陌", "保存到QQ收藏",
                            "发送到朋友圈", "微博", "发送给朋友", "发送给好友"};
            String[] packageNames = new String[]{"com.tencent.mm","com.immomo.momo","com.tencent.mobileqq",
                      "com.tencent.mm", "com.sina.weibo","com.tencent.mm","com.tencent.mobileqq"};
            myDatabase = MyDatabase.getInstance(mGlobalContext);
            long time=System.currentTimeMillis();
            for (int i = 0; i < packageNames.length; i++)
                myDatabase.insertPreferShare(packageNames[i], shareTitles[i],time++);
        } catch (IOException e) {

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
