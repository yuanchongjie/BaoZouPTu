package a.baozouptu.base.dataAndLogic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import a.baozouptu.base.util.Util;

/**
 * Created by Administrator on 2016/6/17.
 */
public class MySQLiteOpenHandler extends SQLiteOpenHelper {
    private static final String name="mysqlite";
    public MySQLiteOpenHandler(Context context,int version){
        super(context,name,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Util.P.le("执行了创建数据库");
        db.execSQL("create table  IF NOT EXISTS usedpic(path text primary key,time varchar(50))");
        db.execSQL("create table  IF NOT EXISTS usualypic(path text primary key,time varchar(50))");
        db.execSQL("create table  IF NOT EXISTS prefer_share(title text primary key,time varchar(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Util.P.le("执行了更新数据库");
    }
}
