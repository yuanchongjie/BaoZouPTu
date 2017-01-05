package a.baozouptu.user.userSetting;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 * 主持者和View契约类
 */

public interface SettingContract {
    interface View extends BaseView<Presenter>{
        void switchSendShortCutNotify(boolean isSend);
        void switchSendShortCutNotifyExit(boolean isSend);
    }

    interface Presenter extends BasePresenter{
        void saveSendShortCutExit(boolean isSend);
        /**
         * 是联动的
         * 当发送快捷通知改变时，要设置退出时是否发生
         */
        void onShortCutNotifyChanged(boolean checked);
    }

}