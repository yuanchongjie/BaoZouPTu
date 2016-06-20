package a.baozouptu.dataAndLogic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import a.baozouptu.tools.FileTool;


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

    /**
     * 添加出常用的图片
     *
     * @param context
     */
    private List<String> usualyFilesList = new ArrayList<>();
    private FileTool fileTool = new FileTool();

    public ProcessUsualyPicPath(Context context) {
        mContext = context;
        mHandler = null;
    }

    public ProcessUsualyPicPath(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public List<String> getUsualyPathFromDB() {
        try {
            mDB = MyDatabase.getInstance(mContext);
            mDB.quaryAllUsedPic(mUsualyPicPathList);
            curUsedNumber = mUsualyPicPathList.size();
            mDB.quaryAllRecentPic(mUsualyPicPathList, recentTimesList);
            curRecentNumber = mUsualyPicPathList.size() - curUsedNumber;
            mDB.quaryAllUsualyPic(mUsualyPicPathList);
            for (String path : usualyFilesList) {
                fileTool.getOrderedPicListInFile(path, mUsualyPicPathList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
        return mUsualyPicPathList;
    }

    /**
     * 添加最近编辑过的图片
     *
     * @param path
     */
    //注意数据库，内存双添加,以及相关参数改变
    public void addUsedPath(String path) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            //如果存在，需要先删除原来的
            if (mUsualyPicPathList.indexOf(path) < curUsedNumber) {
                mDB.deleteUsedPic(path);
                mUsualyPicPathList.remove(path);
            }
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
     * 添加最近图片
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
     * 添加最近图片
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
    public boolean addUsualyPath(String path) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            mDB.insertUsualyPic(path, lastTime++);
            //如果已经设置为常用
            if (mUsualyPicPathList.indexOf(path) >= getUsualyStart()) return false;
            else {
                mUsualyPicPathList.add(curUsedNumber + curRecentNumber, path);
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
        return false;
    }

    /**
     * 通过文件列表添加选定的常用图片
     *
     * @param pathsList 常用文件的列表
     */
    private void muitlAddUsualyPath(List<String> pathsList) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            for (String path : pathsList) {
                mDB.insertUsualyPic(path, lastTime++);
                //不在常用列表时才添加
                if (mUsualyPicPathList.lastIndexOf(path) < getUsualyStart())
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
    public void deleteUsualyPath(String path, int index) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            mUsualyPicPathList.remove(index);
            mDB.deleteUsualyPic(path);
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
    private void muitlDeleteUsualyPath(List<String> pathsList) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            for (String path : pathsList) {
                mDB.deleteUsualyPic(path);
                int index = mUsualyPicPathList.lastIndexOf(path);
                //当有重复时，只删除常用列表里面的
                //如果选定常用列表里面它还存在，没被删除
                if (index >= getUsualyStart())
                    mUsualyPicPathList.remove(index);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    public void addAllUsuPathInFile(String filepath) {
        List<String> pathsList = new ArrayList<>();
        new FileTool().getOrderedPicListInFile(filepath, pathsList);
        muitlAddUsualyPath(pathsList);
    }

    public void deleteAllUsuPathInFile(String filePath) {
        List<String> pathsList = new ArrayList<>();
        new FileTool().getOrderedPicListInFile(filePath, pathsList);
        muitlDeleteUsualyPath(pathsList);
    }

    /**
     * 开启新线程，查询所有的图片
     */
    public void getAllPicInfoAndRecent() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 排序处理得到的图片的map
                List<Pair<Long, String>> oderedPicPathsByTime = new ArrayList<>();
                quaryPicInfoInSD(oderedPicPathsByTime, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                quaryPicInfoInSD(oderedPicPathsByTime, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                AllDate.scanTime =System.currentTimeMillis();
                Collections.sort(oderedPicPathsByTime, new Comparator<Pair<Long, String>>() {
                    @Override
                    public int compare(Pair<Long, String> o1, Pair<Long, String> o2) {
                        return o1.first.compareTo(o2.first);
                    }
                });
                if(oderedPicPathsByTime.get(0).first<AllDate.lastScanTime)
                {
                    AllDate.lastScanTime=AllDate.scanTime;
                    return;
                }
                //处理最近图片
                try {
                    mDB = MyDatabase.getInstance(mContext);
                    int index = 0;
                    for (Pair<Long, String> pair : oderedPicPathsByTime) {
                        long time = -pair.first;
                        if ((recentTimesList.size() < MAX_RECENT_NUMBER || time < recentTimesList.get(index))
                                && index < MAX_RECENT_NUMBER) {
                            if (recentTimesList.size() > MAX_RECENT_NUMBER)
                                recentTimesList.remove(recentTimesList.size() - 1);
                            recentTimesList.add(index, time);
                            muitlAddRecentPath(pair.second, index, time);
                            index++;
                        } else {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mDB.close();
                }

                // 处理文件信息,将要显示的文件信息获取出来
                filePathList.add("aaaaa");
                fileRepresentPathList.add(mUsualyPicPathList.get(0));
                fileInfoList.add("  常用图片 (" + mUsualyPicPathList.size() + ")");

                for (Map.Entry<String, Integer> entry : picFileNumberMap.entrySet()) {
                    String path = entry.getKey();
                    filePathList.add(path);
                    fileRepresentPathList.add(PicFileRepresentMap.get(path));
                    String name = path.substring(path.lastIndexOf("/") + 1, path.length());
                    fileInfoList.add("  " + name + " (" + String.valueOf(entry.getValue()) + ")");
                }
                Message msg = new Message();
                msg.obj = "change_pic";
                mHandler.sendMessage(msg);
                Message msg1 = new Message();
                msg1.obj = "change_file";
                mHandler.sendMessage(msg1);
            }
        };
        new Thread(runnable).start();
    }

    public List<String> getAllUsualyFile() {
        return usualyFilesList;
    }

    /**
     * 启动一个新线程从图片数据库中获取图片信息
     */
    private void quaryPicInfoInSD(final List<Pair<Long, String>> sortPictureList, final Uri uri) {
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
                long modifyTime = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));// 最近修改时间
                if (5000 < size && size < 16000000) {// 图片符合条件
                    sortPictureList.add(new Pair(-modifyTime, path));
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
     * @param picPath
     */
    public boolean deleteOnePicInfile(String picPath) {
        File file = new File(picPath);
        String path = file.getParent();
        if (!file.exists()) return true;
        if (file.exists()) {
            if (file.delete() == false) {
                return false;
            }
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(new File(picPath)));
            mContext.sendBroadcast(scanIntent);
            MediaScannerConnection.scanFile(mContext, new String[]{path}, null, null);
        }


        int id = filePathList.indexOf(path);
        if (id != -1) {
            filePathList.remove(id);
            if (filePathList.size() == 0) {
                fileRepresentPathList.remove(id);
                fileInfoList.remove(id);
                return true;
            }
            filePathList.add(id, path);
            List<String> paths = new ArrayList<>();
            fileTool.getOrderedPicListInFile(path, paths);
            if (fileRepresentPathList.get(id).equals(picPath)) {
                fileRepresentPathList.remove(id);
                fileRepresentPathList.add(id, paths.get(0));
            }
            String name = path.substring(path.lastIndexOf("/") + 1, path.length());
            fileInfoList.add(id, "  " + name + " (" + paths.size() + ")");
        }
        return true;
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


    public int getUsualyStart() {
        return curUsedNumber + curRecentNumber;
    }

    public void deletePicture(String path) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            //如果存在，需要先删除原来的
            if (mUsualyPicPathList.indexOf(path) < curUsedNumber) {
                mDB.deleteUsedPic(path);
                mUsualyPicPathList.remove(path);
            }
            if (mUsualyPicPathList.indexOf(path) < curRecentNumber) {
                mDB.deleteRecentPic(path);
                mUsualyPicPathList.remove(path);
            }
            if (mUsualyPicPathList.contains(path)) {
                mDB.deleteUsualyPic(path);
                mUsualyPicPathList.remove(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }
}
