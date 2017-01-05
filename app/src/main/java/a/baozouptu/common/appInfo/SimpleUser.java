package a.baozouptu.common.appInfo;

import android.content.Context;
import android.util.Log;

import java.util.List;

import a.baozouptu.common.dataAndLogic.AllData;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by LiuGuicen on 2016/12/25 0025.
 *
 */

public class SimpleUser extends BmobObject {
    private String userIdentify;
    private String appVersion;

    public SimpleUser() {
        userIdentify= new UserExclusiveIdentify(AllData.appContext).toString();
        appVersion=String.valueOf(AppConfig.CUR_DATABASE_VERSION);
    }
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    public String getAppVersion() {
        return appVersion;
    }
    public String getUserIdentify() {
        return userIdentify;
    }
    public void setUserIdentify(String userIdentify) {
        this.userIdentify = userIdentify;
    }

    /**
     * 在每次判断是否需要更新时查看是否已添加到服务器 ，这个时候调用此函数进行添加
     */
    private void mySave() {

        save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Log.e("服务器保存成功：", objectId);
                    //服务器添加成功才写入用户信息
                    AllData.appConfig.writeConfig_LastUsedData(System.currentTimeMillis());
                } else {
                    Log.e("bmob", "服务器保存失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public void myUpdate() {
        if (!isUpdateNeeded()) return;//不需要更新，返回

        BmobQuery<SimpleUser> query = new BmobQuery<>();
        query.addWhereEqualTo("userIdentify", userIdentify);
        query.findObjects(new FindListener<SimpleUser>() {
            @Override
            public void done(List<SimpleUser> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) return;
                    SimpleUser simpleUser = list.get(0);

                    String objectId = simpleUser.getObjectId();
                    update(objectId, new UpdateListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                AllData.appConfig.writeConfig_LastUsedData(System.currentTimeMillis());
                                Log.e("bmob", "更新成功");
                            } else {
                                Log.e("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                } else {
                    Log.e("bmob", "查询失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

    /**
     * 查看是否需要更新，并且会查看是否添加到服务器，没有添加时则由这里添加
     */
    private boolean isUpdateNeeded() {
        long data = System.currentTimeMillis() / 24 / 3600 / 100;//日为单位
        long lastData = AllData.appConfig.readConfig_LastUseData() / 24 / 3600 / 100;//日为单位
        if (lastData == 0)//网络端没有创建成功,则重新创建
        {
            mySave();
            return false;
        } else if (data > lastData)//大于上次使用日期
        {
            return true;
        }
        return false;
    }
}
