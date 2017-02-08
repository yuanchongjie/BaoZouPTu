package a.baozouptu.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import a.baozouptu.common.CrashLog;

/**
 * Created by Administrator on 2016/11/21 0021.
 */

public class NetworkState {
    /**
     * 检测网络状态，
     *
     * @return <p>1表示WiFi
     * <p>0表示GPRS流量
     * <p>-1表示没有联网
     * <p>2表示其它网络
     */
    public static int detecteNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) return -1;
        else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            return 1;
        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) return 0;
        else return 2;
    }
}
