package a.baozouptu.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liuguicen on 2016/8/13.
 * 本App的一些信息
 *
 * @description
 */
public class AppConfig {
    Context mGloableContext;
    public final static float APPVERSION = 1.0f;
    public final static int DATABASE_VERSION =2;
    enum SpType {
        appconfig
    }
    public static int getDatabaseVersion(){
        return DATABASE_VERSION;
    }

    public static float getAppversion() {
        return APPVERSION;
    }

    public SharedPreferences getSharedPreference(SpType spType) {
        SharedPreferences sp=null;
        switch (spType) {
            case appconfig:
                sp = mGloableContext.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
                break;
            default:
                break;
        }
        return sp;
    }

    public AppConfig(Context globleContext) {
        mGloableContext = globleContext;
        init();
    }

    private void init() {


    }

    public boolean isNewInstall() {
        SharedPreferences sp = mGloableContext.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        if (!sp.contains("isNewInstall")) {
            spEditor.putBoolean("isNewInstall", false);
            spEditor.commit();
            return true;
        }
        return false;
    }
}
