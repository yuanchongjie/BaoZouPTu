package a.baozouptu.common.dataAndLogic;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by LiuGuicen on 2016/12/26 0026.
 * 基本的设置
 */

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

}
