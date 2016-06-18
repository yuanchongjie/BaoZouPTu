package a.baozouptu.dataAndLogic;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.List;

import a.baozouptu.tools.Util;

/**
 * 一定注意，使用这个类时，使用完了关闭数据库
 */
public class MyDatabase {
    private static MyDatabase myDatabase;
    private static MySQLiteOpenHandler dbHelper;
    private static SQLiteDatabase db;

    private MyDatabase(Context context) {
        dbHelper = new MySQLiteOpenHandler(context);
        db = dbHelper.getWritableDatabase();
    }

    public static MyDatabase getInstance(Context context) {
        if (myDatabase == null || dbHelper == null || db == null)
            myDatabase = new MyDatabase(context);
        return myDatabase;
    }

    /**
     * usedpic(path text primary key,time varchar(20))
     * inert时如果存在就替换，使用replace，不然就会出错，
     * 这样就不需要update了
     */
    public void insertUsedPic(String path, long time) throws IOException {
        db.execSQL("replace into usedpic(path,time) values(?,?) ", new Object[]{path, String.valueOf(time)});
    }

    /**
     * 超出最多图片数时，删除最早添加进去的图片
     * usedpic(path text primary key,time varchar(20))
     */
    public void deleteOdlestUsedPic() throws IOException {
        db.execSQL("delete from usedpic where time = ( select min(time) from usedpic )", new Object[]{});
    }

    /**
     * 删除
     * usedpic(path text primary key,time varchar(20))
     *
     * @param path
     */
    public void deleteUsedPic(String path) throws IOException {
        db.execSQL("delete from usedpic where path = ?", new Object[]{path});
    }

    /**
     * usedpic(path text primary key,time varchar(20))
     *
     * @param path
     * @param time
     */
    public void updateUsedPic(String path, long time) throws IOException {
        insertUsedPic(path, time);
    }

    /**
     * 获取存入数据库的所有使用过得图片
     * usedpic(path text primary key,time varchar(20))
     *
     * @param pathList
     */
    //    有两个返回值，不能直接返回，传入应用获取
    public void quaryAllUsedPic(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select path from usedpic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            pathList.add(cursor.getString(0));
        }
    }
    public void tquaryAllUsedPic(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select * from usedpic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            Util.P.le(cursor.getString(0),cursor.getString(1));
        }
    }

    /**
     * recentpic(path text primary key,time varchar(20))
     * inert时如果存在就替换，使用replace，不然就会出错，
     * 这样就不需要update了
     */
    public void insertRecentPic(String path, long time) throws IOException {
        db.execSQL("replace into recentpic(path,time) values(?,?) ", new Object[]{path, String.valueOf(time)});
    }

    /**
     * recentpic(path text primary key,time varchar(20))
     *
     * @param path
     */
    public void deleteRecentPic(String path) throws IOException {
        db.execSQL("delete from recentpic where path = ?", new Object[]{path});
    }

    /**
     * 超出最多图片数时，删除最早添加进去的图片
     * usedpic(path text primary key,time varchar(20))
     */
    public void deleteOdlestRecentPic() throws IOException {
        db.execSQL("delete from recentpic where time = ( select min(time) from recentpic )", new Object[]{});
    }

    /**
     * recentpic(path text primary key,time varchar(20))
     *
     * @param path
     * @param time
     */
    public void updateRecentPic(String path, long time) throws IOException {
        insertRecentPic(path, time);
    }

    /**
     * * 获取存入数据库的所有使用最近的图片
     * recentpic(path text primary key,time varchar(20))
     *
     * @param pathList
     */
    //    有两个返回值，不能直接返回，传入应用获取
    public void quaryAllRecentPic(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select path from recentpic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            pathList.add(cursor.getString(0));
        }
    }
    /**
     * 获取存入数据库的所有使用最近的图片的路径和时间
     * recentpic(path text primary key,time varchar(20))
     * @param pathList
     */
    //    有两个返回值，不能直接返回，传入应用获取
    public void quaryAllRecentPic(List<String> pathList,List<Long> timesList) throws IOException {
        Cursor cursor = db.rawQuery("select * from recentpic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            pathList.add(cursor.getString(0));
            timesList.add(Long.valueOf(cursor.getString(1)));
        }
    }

    /**
     * usualypic(path text primary key,time varchar(20))
     * inert时如果存在就替换，使用replace，不然就会出错，
     * 这样就不需要update了
     */
    public void insertUsualyPic(String path, long time) throws IOException {
        db.execSQL("replace into usualypic(path,time) values(?,?) ", new Object[]{path, String.valueOf(time)});
    }

    /**
     * usualypic(path text primary key,time varchar(20))
     *
     * @param path
     */
    public void deleteUsualyPic(String path) throws IOException {
        db.execSQL("delete from usualypic where path = ?", new Object[]{path});
    }

    /**
     * usualypic(path text primary key,time varchar(20))
     *
     * @param path
     * @param time
     */
    public void updateUsualyPic(String path, long time) throws IOException {
        insertUsualyPic(path, time);
    }

    /**
     * 获取存入数据库的所有的选择出常用的得图片
     * usualypic(path text primary key,time varchar(20))
     *
     * @param pathList
     */
    //    有两个返回值，不能直接返回，传入应用获取
    public void quaryAllUsualyPic(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select path from usualypic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            pathList.add(cursor.getString(0));
        }
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
        if (db != null) {
            db.close();
            db = null;
        }
    }
}
