package a.baozouptu.base.dataAndLogic;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;
import java.util.List;

import a.baozouptu.common.AppConfig;

/**
 * 一定注意，使用这个类时，使用完了关闭数据库
 */
public class MyDatabase {
    private static MyDatabase myDatabase;
    private static MySQLiteOpenHandler dbHelper;
    private static SQLiteDatabase db;


    private MyDatabase(Context context) {
        dbHelper = new MySQLiteOpenHandler(context, AppConfig.getDatabaseVersion());
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
    public void queryAllUsedPic(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select path from usedpic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            String path = cursor.getString(0);
            //从数据库读出文件路径时就检测是否已被删除，删除了则不添加，并且从数据库删除，
            if (!(new File(path).exists())) {
                deleteUsedPic(path);
            } else
                pathList.add(path);
        }
    }

    /**
     * 查询图片路径加上时间
     */
    public void queryAllUsedPicWithTime(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select * from usedpic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            String path = cursor.getString(0);
            if (!(new File(path).exists()))
                deleteUsedPic(path);
            else
                pathList.add(path);
        }
    }

    /**
     * usualypic(path text primary key,time varchar(20))
     * inert时如果存在就替换，使用replace，不然就会出错，
     * 这样就不需要update了
     */
    public void insertPreferPic(String path, long time) throws IOException {
        db.execSQL("replace into usualypic(path,time) values(?,?) ", new Object[]{path, String.valueOf(time)});
    }

    /**
     * usualypic(path text primary key,time varchar(20))
     *
     * @param path
     */
    public void deleteFrequentlyPic(String path) throws IOException {
        db.execSQL("delete from usualypic where path = ?", new Object[]{path});
    }

    /**
     * usualypic(path text primary key,time varchar(20))
     *
     * @param path
     * @param time
     */
    public void updateFrequentlyPic(String path, long time) throws IOException {
        insertPreferPic(path, time);
    }

    /**
     * 获取存入数据库的所有的选择出常用的得图片
     * usualypic(path text primary key,time varchar(20))
     *
     * @param pathList
     */
    //    有两个返回值，不能直接返回，传入应用获取
    public void queryAllPreferPic(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select path from usualypic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            String path = cursor.getString(0);
            if (!(new File(path).exists()))
                deleteFrequentlyPic(path);
            else
                pathList.add(path);
        }
    }

    /**
     * 获取所有的优先分享的ac的title
     * "create table  IF NOT EXISTS prefer_share(title text primary key,time varchar(50))"
     * 按时间倒序，即越前面优先级越高
     */
    //    有两个返回值，不能直接返回，传入应用获取
    public void queryAllPreferShare(List<String> titleList) throws IOException {
        Cursor cursor = db.rawQuery("select title from prefer_share order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            String title = cursor.getString(0);
            titleList.add(title);
        }
    }


    /**
     * "create table  IF NOT EXISTS prefer_share(title text primary key,time varchar(50))"
     * inert时如果存在就替换，使用replace，不然就会出错，
     * 这样就不需要update了
     */
    public void insertPreferShare(String title, long time) throws IOException {
        db.execSQL("replace into prefer_share(title,time) values(?,?) ", new Object[]{title, String.valueOf(time)});
    }

    /**
     * "create table  IF NOT EXISTS prefer_share(title text primary key,time varchar(50))"
     *
     * @param title ac的title
     */
    public void deletePreferShare(String title) throws IOException {
        db.execSQL("delete from prefer_share where title = ?", new Object[]{title});
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }

    }
}
