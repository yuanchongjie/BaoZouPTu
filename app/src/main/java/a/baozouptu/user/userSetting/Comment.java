package a.baozouptu.user.userSetting;

import a.baozouptu.common.appInfo.UserExclusiveIdentify;
import a.baozouptu.common.dataAndLogic.AllData;
import cn.bmob.v3.BmobObject;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public class Comment extends BmobObject{
    public String userIdentify;
    public String comment;
    public String contact;

    /**
     * 至少要有一个comment
     * @param comment 反馈意见
     */
    Comment(String comment){
        userIdentify= new UserExclusiveIdentify(AllData.appContext).toString();
        this.comment=comment;
    }


    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
    public void setUserIdentify(String userIdentify) {
        this.userIdentify = userIdentify;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserIdentify() {

        return userIdentify;
    }

    public String getComment() {
        return comment;
    }

}