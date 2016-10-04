package a.baozouptu.base.dataAndLogic;

import android.content.Context;

import java.io.IOException;
import java.util.List;

/**
 * Created by liuguicen on 2016/8/21.
 *
 * @description
 */
public class DBUtil {
    public static void deletePreferInfo(Context context, String title) {
        MyDatabase mdb = MyDatabase.getInstance(context);
        try {
            mdb.deletePreferShare(title);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mdb.close();
        }
    }

    public static void deletePreferInfo(Context context, List<String> titles) {
        MyDatabase mdb = MyDatabase.getInstance(context);
        try {
            for (String title : titles)
                mdb.deletePreferShare(title);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mdb.close();
        }
    }


    public static void inseartPreferInfo(Context context, List<String> titles) {
        MyDatabase mdb = MyDatabase.getInstance(context);
        try {
            for (String title : titles)
                mdb.insertPreferShare(title, System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mdb.close();
        }
    }

    public static void inseartPreferInfo(Context context, String title) {
        MyDatabase mdb = MyDatabase.getInstance(context);
        try {
            mdb.insertPreferShare(title, System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mdb.close();
        }
    }
}
