package a.baozouptu.user.userSetting;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.DecimalFormat;

import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.FileTool;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public class SettingDataSourceImpl implements SettingDataSource {
    Context appContext;
    private static final String SEND_SHORTCUT_NOTIFY = "send_shortcut_notify";
    private static final String SEND_SHORTCUT_NOTIFY_EXIT = "send_shortcut_notify_exit";
    private static final String SHARED_WHTHOUT_LABEL = "shared_without_label";
    SharedPreferences sp;

    /**
     * 清除数据相关
     */
    private String[] DATA_DIRS;
    private float totalSize;
    private final String[] DATA_NAMES;

    private String[] dataItemInfos;


    /**
     * 传入app的context
     */
    public SettingDataSourceImpl(@NonNull Context appContext) {
        this.appContext = appContext;
        sp = appContext.getSharedPreferences("user_config", Context.MODE_PRIVATE);
        /**
         * 写死的部分
         */
        DATA_DIRS = new String[]{AllData.zitiDir, ""};
        DATA_NAMES = new String[]{"字体文件", "图片缓存"};

        initDataInfo();
    }

    /**
     * 初始化app相关的数据信息
     * 注意清除之后要调用一次刷新信息
     */
    private void initDataInfo() {
        totalSize = 0;
        //获取大小信息
        String[] sizeStrings = new String[DATA_DIRS.length];
        DecimalFormat df = new DecimalFormat("#.0");
        for (int i = 0; i < DATA_DIRS.length; i++) {
            File file = new File(DATA_DIRS[i]);
            if (file.exists()) {
                double size = FileTool.getFileSize(file) * 1d / 1000 / 1000;
                //格式化一下
                if (size < 0.05) {//对于0，系统处理有点问题
                    sizeStrings[i] = "0";
                } else {
                    sizeStrings[i] = df.format(size);
                }
                totalSize += (float) size;
            } else
                sizeStrings[i] = "0";
        }
        //说明的信息
        dataItemInfos = new String[DATA_DIRS.length];
        for (int i = 0; i < DATA_DIRS.length; i++)
            dataItemInfos[i] = DATA_NAMES[i] + "(" + sizeStrings[i] + "M)";
        totalSize = Float.parseFloat(df.format(totalSize));
    }

    @Override
    public void saveSendShortCutNotify(boolean isSend) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SEND_SHORTCUT_NOTIFY, isSend);
        editor.apply();
    }

    @Override
    public void saveSendShortCutNotifyExit(boolean isSend) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SEND_SHORTCUT_NOTIFY_EXIT, isSend);
        editor.apply();
    }

    @Override
    public void saveSharedWithout(boolean isWith) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SHARED_WHTHOUT_LABEL, isWith);
        editor.apply();
    }

    @Override
    public boolean getSendShortcutNotify() {
        return sp.getBoolean(SEND_SHORTCUT_NOTIFY, true);
    }

    @Override
    public boolean getSendShortcutNotifyExit() {
        return sp.getBoolean(SEND_SHORTCUT_NOTIFY_EXIT, true);
    }

    public String[] getDataItemInfos() {
        return dataItemInfos;
    }

    /**
     * @return 默认是要带 false
     */
    @Override
    public boolean getSharedWithout() {
        return sp.getBoolean(SHARED_WHTHOUT_LABEL, false);//默认是要带 false
    }

    @Override
    public float getAppDataSize() {
        return totalSize;
    }

    @Override
    public String clearAppCache(boolean[] userChosenItems) {
        String failRes = "";
        //清除字体文件
        if (userChosenItems[0]) {
            if (!clearTypeface())
                failRes += DATA_NAMES[0];
        }
        //清除缓存的贴图
        if (userChosenItems[1]) {
            if (!clearImageCahe())
                failRes += DATA_NAMES[1];
        }
        initDataInfo();
        return failRes;
    }

    private boolean clearImageCahe() {
        return true;
    }

    private boolean clearTypeface() {
        return FileTool.deleteAllChileFile(new File(AllData.zitiDir));
    }
}
