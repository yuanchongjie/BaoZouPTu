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
    public final static int DATABASE_VERSION = 2;
    private boolean textRubber;
    private boolean goSend;


    public boolean hasReadTextRubber() {
        return textRubber;
    }

    public boolean hasReadGoSend() {
        return goSend;
    }

    enum SpType {
        appconfig
    }

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public static float getAppversion() {
        return APPVERSION;
    }

    public SharedPreferences getSharedPreference(SpType spType) {
        SharedPreferences sp = null;
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
        //各种首次使用提示
        SharedPreferences sp = mGloableContext.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
        textRubber = sp.getBoolean("text_rubber", false);
        goSend = sp.getBoolean("go_send", false);
        init();
    }

    private void init() {

    }

    public void writeConfig_TextRubber(boolean isRead) {
        textRubber = isRead;
        SharedPreferences sp = mGloableContext.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putBoolean("text_rubber", isRead);
        spEditor.apply();
    }

    public void wiriteConfig_GoSend(boolean isRead){
        goSend=isRead;
        SharedPreferences sp = mGloableContext.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putBoolean("go_send", isRead);
        spEditor.apply();
    }

    public boolean isNewInstall() {
        SharedPreferences sp = mGloableContext.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        if (!sp.contains("isNewInstall")) {
            spEditor.putBoolean("isNewInstall", false);
            spEditor.apply();
            return true;
        }
        return false;
    }

    public void setTextRubber(boolean textRubber) {
        this.textRubber = textRubber;
    }
}
