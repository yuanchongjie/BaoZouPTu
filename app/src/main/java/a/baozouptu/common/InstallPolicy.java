package a.baozouptu.common;

import android.content.Context;
import android.os.Environment;

import java.io.IOException;

import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.dataAndLogic.MyDatabase;
import a.baozouptu.base.util.FileTool;
import a.baozouptu.base.util.Util;

import static a.baozouptu.base.dataAndLogic.AllData.appConfig;

/**
 * Created by liuguicen on 2016/8/13.
 *
 * @description
 */
public class InstallPolicy {
    Context mGloableContext;
    private MyDatabase myDatabase;

    public InstallPolicy() {
        mGloableContext = Util.MyApplication.getAppContext();
        appConfig = new AppConfig(mGloableContext);
    }

    public void processPolicy() {
        if (appConfig.isNewInstall()) {//新安装安
            createAppFile();
            MyDatabase myDatabase = null;
            try {//添加分享的优先选项
                String[] shareTitls = new String[]{"添加到微信收藏", "陌陌", "保存到QQ收藏", "发送给朋友", "发送给好友"};
                myDatabase = MyDatabase.getInstance(mGloableContext);
                for (String title : shareTitls) {
                    myDatabase.insertPreferShare(title, System.currentTimeMillis());
                }
            } catch (IOException e) {

            }finally {
                myDatabase.close();
            }
       }
    }

    /**
     * 创建App的文件
     */
    private void createAppFile() {

    }

}
