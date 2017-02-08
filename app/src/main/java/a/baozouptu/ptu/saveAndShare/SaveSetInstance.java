package a.baozouptu.ptu.saveAndShare;

import android.content.Context;

/**
 * Created by Administrator on 2016/11/22 0022.
 * 通过代理的非静态单例模式
 */

public class SaveSetInstance {
    private SaveSetDialogManager saveSetDialogManager;
    public SaveSetInstance(){}
    public synchronized SaveSetDialogManager getInstance(Context context){
        if(saveSetDialogManager==null)
            saveSetDialogManager=new SaveSetDialogManager(context);
        return saveSetDialogManager;
    }

}
