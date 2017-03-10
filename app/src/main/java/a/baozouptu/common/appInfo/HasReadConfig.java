package a.baozouptu.common.appInfo;

import android.content.Context;
import android.content.SharedPreferences;

import a.baozouptu.common.dataAndLogic.AllData;

public class HasReadConfig {

    private final SharedPreferences sp;

    public HasReadConfig() {
        sp = AllData.appContext.getSharedPreferences("common_config", Context.MODE_PRIVATE);
    }

    /**
     * 常用图片使用阅读与否
     */
    public boolean hasReadUsuPicUse() {
        return sp.getBoolean("usu_pic_use", false);
    }

    public void write_usuPicUse(boolean isRead) {
        sp.edit().putBoolean("usu_pic_use", isRead)
                .apply();
    }

    public boolean hasReadGoSend() {
        return sp.getBoolean("go_send", false);
    }

    public void write_GoSend(boolean isRead) {
        sp.edit().putBoolean("go_send", isRead)
                .apply();
    }

    public boolean hasRead_absorb() {
        return sp.getBoolean("absorb", false);
    }

    public void write_absorb(boolean isRead) {
        sp.edit().putBoolean("absorb", isRead)
                .apply();
    }


}
