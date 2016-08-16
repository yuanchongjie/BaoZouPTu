/**********************************************************************
 * AUTHOR：YOLANDA
 * DATE：2015年4月5日下午1:03:11
 * DESCRIPTION：create the File, and add the content.
 ***********************************************************************/
package a.baozouptu.ptu.saveAndShare;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ShareUtil {
    /**
     * @param preferApps 优先旋转的应用Activity的title，越前面，优先级越高
     * @return
     */
    public static List<ListDrawableItem> getSortedAppData(Context context,List<String> preferApps,List<ResolveInfo> resolveInfos) {
        List<ListDrawableItem> appInfos=getShowData(context,resolveInfos);
        int size = preferApps.size();
        for (int p = size-1; p >= 0; p--) {
            CharSequence cs = preferApps.get(p);
            for (int i = 0; i < appInfos.size(); i++) {
                if (appInfos.get(i).getTitle().equals(cs)) {
                    resolveInfos.add(0,resolveInfos.remove(i));

                    appInfos.add(0, appInfos.remove(i));
                    break;
                }
            }
        }
        return appInfos;
    }

    /**
     * 拿到要显示的应用数据
     */
    private static ArrayList<ListDrawableItem> getShowData(Context context, List<ResolveInfo> resolveInfos) {
        ArrayList<ListDrawableItem> drawableItems = new ArrayList<ListDrawableItem>();
        PackageManager mPackageManager = context.getPackageManager();
        for (int i = 0; i < resolveInfos.size(); i++) {
            ResolveInfo info = resolveInfos.get(i);
            ListDrawableItem dialogItemEntity = new ListDrawableItem(info.loadLabel(mPackageManager), info.loadIcon(mPackageManager));
            drawableItems.add(dialogItemEntity);
        }
        return drawableItems;
    }

    /**
     * 通过系统分享内容出去
     *
     * @param packageName   包名
     * @param imgPathOrText 图片路径或者文字
     * @param type          分享内容的类型
     */
    public static void exeShare(Context context, String chooserTitle,
                                String packageName, String imgPathOrText, Type type) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        switch (type) {
            case Image:
                intent.setType("image/*");
                File imgPath = new File(imgPathOrText);
                Uri uri = Uri.fromFile(imgPath);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                break;
            case Text:
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, imgPathOrText);
                break;
        }
        intent.setPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(Intent.createChooser(intent, chooserTitle));
        } catch (ActivityNotFoundException e) {
        }
    }

    /**
     * 得到支持分享的应用
     *
     * @param context
     * @return
     * @author YOLANDA
     */
    public  static List<ResolveInfo> getShareTargets(Context context, Type type) {
        List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        switch (type) {
            case Image:
                intent.setType("image/*");
                break;
            default:
                intent.setType("text/plain");
                break;
        }
        PackageManager pm = context.getPackageManager();
        mApps = pm.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return mApps;
    }

    /**
     * 分享类型
     *
     * @author YOLANDA
     * @Project SmartControl
     * @Class ShareUtil.java
     * @Time 2015年3月4日 上午10:21:16
     */
    public enum Type {
        /**
         * 图片
         **/
        Image,
        /**
         * 文字
         **/
        Text
    }
}

