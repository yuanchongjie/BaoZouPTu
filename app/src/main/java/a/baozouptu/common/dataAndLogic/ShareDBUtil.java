package a.baozouptu.common.dataAndLogic;

import android.content.Context;
import android.util.Pair;

import java.io.IOException;
import java.util.List;

/**
 * Created by liuguicen on 2016/8/21.
 *
 * @description 分享功能的数据库
 */
public class ShareDBUtil {
    public static void deletePreferInfo(Context context, String packageName, String title) {
        MyDatabase mdb = MyDatabase.getInstance(context);
        try {
            mdb.deletePreferShare(packageName, title);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mdb.close();
        }
    }

    public static void deletePreferInfo(Context context, List<Pair<String, String>> acInfos) {
        MyDatabase mdb = MyDatabase.getInstance(context);
        try {
            for (Pair<String, String> aci : acInfos)
                mdb.deletePreferShare(aci.first, aci.second);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mdb.close();
        }
    }


    public static void inseartMultiPreferInfo(Context context, List<Pair<String, String>> acInfos) {
        MyDatabase mdb = MyDatabase.getInstance(context);
        try {
            for (Pair<String, String> aci : acInfos)
                mdb.insertPreferShare(aci.first, aci.second, System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mdb.close();
        }
    }

    public static void inseartPreferInfo(Context context, String packageName, String title) {
        MyDatabase mdb = MyDatabase.getInstance(context);
        try {
            mdb.insertPreferShare(packageName, title, System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mdb.close();
        }
    }
}
