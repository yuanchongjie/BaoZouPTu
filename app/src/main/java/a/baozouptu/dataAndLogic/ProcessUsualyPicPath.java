package a.baozouptu.dataAndLogic;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import a.baozouptu.control.ShowPictureActivity;


/**
 * 用来处理常用图片的类，作为activity与底层的接口。包括数据库，ContentProvider，文件等
 * 增删改查等功能
 */
public class ProcessUsualyPicPath {
    private final Handler mHandler;
    Context mContext;
    private final int MAX_USED_NUMBER = 6;
    private final int MAX_RECENT_NUMBER = 20;
    /**
     * 当前拥有的编辑过的和最近的图片张数
     */
    private int curUsedNumber = 0, curRecentNumber = 0;
    private MyDatabase mDB;
    /**
     * 只获取一次系统时间，以后都以它为基础相加，避免加入太快，毫秒不能记数
     */
    private long lastTime = System.currentTimeMillis();
    List<String> mUsualyPicPathList = new ArrayList<>();

    /**
     * 最近图片的信息
     */
    private List<Long> recentTimesList = new ArrayList<>();

    /**
     * 文件的信息
     */
    private Map<String, Integer> picFileNumberMap = new TreeMap();
    private Map<String, String> PicFileRepresentMap = new TreeMap();
    private List<String> filePathList = new ArrayList<>();
    private List<String> fileInfoList = new ArrayList<>();
    private List<String> fileRepresentPathList = new ArrayList<>();

    public ProcessUsualyPicPath(Context context) {
        mContext = context;
        mHandler=null;
    }

    public ProcessUsualyPicPath(Context context, Handler handler) {
        mContext=context;
        mHandler=handler;
    }

    public List<String> getUsualyPathFromDB() {
        try {
            mDB = MyDatabase.getInstance(mContext);
            mDB.quaryAllUsedPic(mUsualyPicPathList);
            mDB.quaryAllRecentPic(mUsualyPicPathList, recentTimesList);
            mDB.quaryAllUsualyPic(mUsualyPicPathList);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
        return mUsualyPicPathList;
    }

    /**
     * 添加最近编辑过的图片，
     *
     * @param path
     */
    //注意数据库，内存双添加,以及相关参数改变
    public void addUsedPath(String path) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            if (curUsedNumber + 1 > MAX_USED_NUMBER) {
                mDB.deleteOdlestUsedPic();//超过预定数量时，删除一个，再添加
                mUsualyPicPathList.remove(curUsedNumber - 1);
                curUsedNumber--;
            }
            mDB.insertUsedPic(path, lastTime++);
            mUsualyPicPathList.add(0, path);
            curUsedNumber++;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    /**
     * 添加最近图片，
     *
     * @param path
     */
    //注意数据库，内存双添加,以及相关参数改变
    public void addRecentPath(String path, int index, long time) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            if (curRecentNumber + 1 > MAX_RECENT_NUMBER) {
                mDB.deleteOdlestRecentPic();//超过预定数量时，删除一个，再添加
                mUsualyPicPathList.remove(curUsedNumber + curRecentNumber - 1);
                curRecentNumber--;
            }
            mDB.insertRecentPic(path, time);
            mUsualyPicPathList.add(curUsedNumber + index, path);
            curRecentNumber++;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    /**
     * 添加最近图片，
     *
     * @param path
     */
    //注意数据库，内存双添加,以及相关参数改变
    private void muitlAddRecentPath(String path, int index, long time) throws IOException {
        if (curRecentNumber + 1 > MAX_RECENT_NUMBER) {
            mDB.deleteOdlestRecentPic();//超过预定数量时，删除一个，再添加
            mUsualyPicPathList.remove(curUsedNumber + curRecentNumber - 1);
            curRecentNumber--;
        }
        mDB.insertRecentPic(path, time);
        mUsualyPicPathList.add(curUsedNumber + index, path);
        curRecentNumber++;
    }

    /**
     * 添加选定的常用图片
     *
     * @param path
     */
    public void addUsualyPath(String path) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            mDB.insertUsualyPic(path, lastTime++);
            mUsualyPicPathList.add(curUsedNumber + curRecentNumber, path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    /**
     * 添加选定的常用图片
     */
    public void muitlAddUsualyPath(List<String> pathsList) throws IOException {
        try {
            mDB = MyDatabase.getInstance(mContext);
            for (String path : pathsList) {
                mDB.insertUsualyPic(path, lastTime++);
                mUsualyPicPathList.add(curUsedNumber + curRecentNumber, path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    /**
     * 添加选定的常用图片
     *
     * @param path
     */
    public void deleteUsualyPath(String path) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            mDB.deleteUsualyPic(path);
            mUsualyPicPathList.remove(path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    /**
     * 添加选定的常用图片
     *
     * @param pathsList
     */
    public void muitlDeleteUsualyPath(List<String> pathsList) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            for (String path : pathsList) {
                mDB.deleteUsualyPic(path);
                mUsualyPicPathList.remove(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    /**
     * 排序处理得到的图片的map
     */
    final Map<Integer, String> sortPictureMap = new TreeMap<Integer, String>();

    public void getAllPicInfoAndRecent() {
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                boolean change = false;
                quaryPicInfoInSD(sortPictureMap, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                quaryPicInfoInSD(sortPictureMap, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//处理最近图片
                try {
                    mDB = MyDatabase.getInstance(mContext);
                    int index = 0;
                    for (Map.Entry<Integer, String> entry : sortPictureMap.entrySet()) {
                        long time = -(entry.getKey());
                        if ((recentTimesList.size()<MAX_RECENT_NUMBER||time < recentTimesList.get(index))
                                && index < MAX_RECENT_NUMBER) {
                            if (recentTimesList.size() > MAX_RECENT_NUMBER)
                                recentTimesList.remove(recentTimesList.size() - 1);
                            recentTimesList.add(index, time);
                            muitlAddRecentPath(entry.getValue(), index, time);
                            index++;
                            change = true;
                        } else {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mDB.close();
                }
                // 处理文件信息
                filePathList.add("aaaaa");
                fileRepresentPathList.add(mUsualyPicPathList.get(0));
                fileInfoList.add("  常用图片 (" + mUsualyPicPathList.size() + ")");

                for (Map.Entry<String, Integer> entry : picFileNumberMap.entrySet()) {
                    String path = entry.getKey();
                    filePathList.add(path);
                    fileRepresentPathList.add(PicFileRepresentMap.get(path));
                    String name = path.substring(path.lastIndexOf("/") + 1, path.length());
                    fileInfoList.add(" " + name + " (" + String.valueOf(entry.getValue()) + ")");
                }
                Message msg = new Message();
                if (change) {
                    msg.obj = "change_pic";
                    mHandler.sendMessage(msg);
                }
                msg.obj="change_file";
                mHandler.sendMessage(msg);
            }
        };
        new Thread(runnable).start();
    }

    /**
     * 启动一个新线程从图片数据库中获取图片信息
     */
    private void quaryPicInfoInSD(final Map<Integer, String> sortPictureMap, final Uri uri) {
        if (uri == null) return;//不为空，放入图片

        String[] projection = {MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};


        Cursor cursor = mContext.getContentResolver().query(uri,
                projection, null, null, null);
        if (cursor != null) {// 从contentProvider之中取出图片
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                int size = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Images.Media.SIZE));
                String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                int modifyTime = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));// 最近修改时间
                if (5000 < size && size < 16000000) {// 图片符合条件
                    sortPictureMap.put(-modifyTime, path);
                    String parentPath = path.substring(0,
                            path.lastIndexOf('/'));
                    if (picFileNumberMap.containsKey(parentPath))
                        picFileNumberMap.put(parentPath, picFileNumberMap.get(parentPath) + 1);
                    else {
                        picFileNumberMap.put(parentPath, 1);
                        PicFileRepresentMap.put(parentPath, path);
                    }
                }
            }
            cursor.close();
        }
    }

    /**
     * 更新文件信息，文件名，图片张数
     *
     * @param path
     * @param number
     */
    public void updateFileInfo(String path, String representPath, int number) {
        //更新map上的信息
        picFileNumberMap.put(path, number);
        PicFileRepresentMap.put(path, representPath);

        int id = filePathList.indexOf(path);
        if (id != -1) {
            filePathList.remove(id);
            fileRepresentPathList.remove(id);
            fileInfoList.remove(id);
            filePathList.add(id,path);
            fileRepresentPathList.add(id,representPath);
            String name = path.substring(path.lastIndexOf("/") + 1, path.length());
            fileInfoList.add(id," " + name + " (" + number + ")");
        } else {
            filePathList.add(path);
            fileRepresentPathList.add(representPath);
            String name = path.substring(path.lastIndexOf("/") + 1, path.length());
            fileInfoList.add(" " + name + " (" + number + ")");
        }
    }

    public List<String> getFilePathList() {
        return filePathList;
    }

    public List<String> getFileRepresentPathList() {
        return fileRepresentPathList;
    }

    public List<String> getFileInfoList() {
        return fileInfoList;
    }

}
