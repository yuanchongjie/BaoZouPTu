package a.baozouptu.user;

import android.os.Build;
import android.util.Log;

import a.baozouptu.common.appInfo.UserExclusiveIdentify;
import a.baozouptu.common.dataAndLogic.AllData;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by LiuGuicen on 2017/1/21 0021.
 *
 */

public class DeviceInfos extends BmobObject {
    private String deviceIdentify;
    private String appVersion;
    private String osVersion;
    private String sdkVersion;
    private String vendor;
    private String model;
    private String cpuAbi;

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setCpuAbi(String cpuAbi) {
        this.cpuAbi = cpuAbi;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getVendor() {
        return vendor;
    }

    public String getModel() {
        return model;
    }

    public String getCpuAbi() {
        return cpuAbi;
    }

    public String getDeviceIdentify() {
        return deviceIdentify;
    }

    public void setDeviceIdentify(String deviceIdentify) {
        this.deviceIdentify = deviceIdentify;
    }

    public DeviceInfos() {
        appVersion = String.valueOf(AllData.appConfig.CUR_APP_VERSION);
        osVersion = Build.VERSION.RELEASE;
        sdkVersion = String.valueOf(Build.VERSION.SDK_INT);

        vendor = Build.MANUFACTURER;
        model = Build.MODEL;
//            CPU架构
        cpuAbi = Build.CPU_ABI;
        deviceIdentify= UserExclusiveIdentify.getExclusiveIndentify();
    }

    /**
     * 将信息上传到服务器，第一次的
     */
    public void serviceCreate() {
        save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Log.e("deviceInfos服务器保存成功：", objectId);
                    //服务器添加成功才写入用户信息
                    AllData.appConfig.writeSendDeviceInfo(true);
                } else {
                    Log.e("bmob deviceInfos", "服务器保存失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

}