package a.baozouptu.common.appInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.ptu.common.PtuData;

/**
 * Created by liuguicen on 2016/8/13.
 * <p> 本App的一些信息，特别升级的时候挺多的信息要记录，因为你不知道是从哪个版本升级过来的
 * <p> app版本，数据库版本,..
 *
 * <p><p>
 * //各个历史版本，别删
 * <p>public final static float APPVERSION_1_0 = 1.0f;
 * <p>public final static float APPVERSION_1_1 = 1.1f;
 * <p>public final static int DATABASE_VERSION_2 = 2;
 * <p>public final static int DATABASE_VERSION_3=3;
 */
public class AppConfig {
    //各个历史版本，别删
    public final static float APPVERSION_1_0 = 1.0f;
    public final static float APPVERSION_1_1 = 1.1f;
    public final static float CUR_APPVERSION = APPVERSION_1_1;
    public final static int DATABASE_VERSION_2 = 2;
    public final static int DATABASE_VERSION_3 = 3;
    public final static int CUR_DATABASE_VERSION = DATABASE_VERSION_3;

    private SharedPreferences sp;

    public static int getDatabaseVersion() {
        return CUR_DATABASE_VERSION;
    }

    public static float getAppversion() {
        return CUR_APPVERSION;
    }

    public AppConfig(Context globalContext) {
        sp = AllData.appContext.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
    }

    public float readAppVersion() {
        return sp.getFloat("app_version", -1);
    }

    public void writeCurAppVersion() {
        sp.edit().putFloat("app_version", CUR_APPVERSION)
                .apply();
    }

    public long readConfig_LastUseData() {
        return sp.getLong("last_used_date", 0);
    }

    public void writeConfig_LastUsedData(long data) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putLong("last_used_date", data);
        spEditor.apply();
    }

    public void clearOldVersionInfo_1_0() {
        SharedPreferences.Editor spEditor = sp.edit();
        //移除1.0版本的ptu上的配置信息，当时模块划分不清晰，也没考虑到模块会变大，变大之后这里变得复杂难写了
        spEditor.remove("text_rubber");
        spEditor.remove("go_send");
        spEditor.remove("usu_pic_use");
        if (!spEditor.commit()) {
            Log.e("暴走P图", "移除1.0版本Config信息失败");
        }
    }

    public boolean hasNewInstall() {
        return sp.contains("isNewInstall");
    }
}
