/**********************************************************************
 * AUTHOR：YOLANDA
 * DATE：2015年3月7日下午2:18:51
 * Copyright © 56iq. All Rights Reserved
 * ======================================================================
 * EDIT HISTORY
 * ----------------------------------------------------------------------
 * |  DATE      | NAME       | REASON       | CHANGE REQ.
 * ----------------------------------------------------------------------
 * | 2015年3月7日    | YOLANDA    | Created      |
 * <p>
 * DESCRIPTION：create the File, and add the content.
 ***********************************************************************/
package a.baozouptu.ptu.saveAndShare;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * List Item数据，图片是Drawable
 */
public class ListDrawableItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private CharSequence packageName;
    private CharSequence title;
    private Drawable icon;
    /**
     * @author YOLANDA
     * @return the title
     */
    public CharSequence getTitle() {
        return title;
    }

    public CharSequence getPackageName() {
        return packageName;
    }

    /**
     * @author YOLANDA
     * @param title the title to set
     */
    public void setTitle(CharSequence title) {
        this.title = title;
    }

    /**
     * @author YOLANDA
     * @return the icon
     */
    public Drawable getIcon() {
        return icon;
    }

    /**
     * @author YOLANDA
     * @param icon the icon to set
     */
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    /**
     * @author YOLANDA
     * @param packageName
     * @param title
     * @param icon
     */
    public ListDrawableItem(CharSequence packageName, CharSequence title, Drawable icon) {
        super();
        this.packageName=packageName;
        this.title = title;
        this.icon = icon;
    }

    /**
     * @author YOLANDA
     */
    public ListDrawableItem() {
        super();
    }

}
