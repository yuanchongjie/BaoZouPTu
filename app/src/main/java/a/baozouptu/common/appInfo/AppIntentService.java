package a.baozouptu.common.appInfo;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentSender;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import a.baozouptu.common.dataAndLogic.AllData;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by LiuGuicen on 2016/12/25 0025.
 *
 */

public class AppIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AppIntentService() {
        super("AppIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new SimpleUser().myUpdate();
    }
}
