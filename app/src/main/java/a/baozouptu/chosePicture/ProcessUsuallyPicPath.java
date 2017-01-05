package a.baozouptu.chosePicture;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.dataAndLogic.MyDatabase;
import a.baozouptu.common.util.FileTool;
import a.baozouptu.common.util.Util;


/**
 * 用来处理常用图片的类，作为activity与底层的接口。包括数据库，ContentProvider，文件等
 * 增删改查等功能
 * <p>
 * 数据的顺序，最近常用用图片
 * <p>最近图片
 * <p>
 * <p>喜爱图片图片
 * <p>
 * <p>
 * </p>
 */
public class ProcessUsuallyPicPath {
    static String USED_FLAG = "@%^@#GDa_USED_FLAG";
    static String RECENT_FLAG = "@%^@#GDa_RECENT_FLAG";
    static String PREFER_FLAG = "@%^@#GDa_PREFER_FLAG";
    private final Handler mHandler;
    private Context mContext;
    private final int MAX_USED_NUMBER = 4;
    private final int MIN_RECENT_NUMBER = 6;
    private final int MAX_RECENT_NUMBER = 20;
    /**
     * 3天的毫秒数
     */
    private final long RECENT_DURATION_TIME = 3l * 24l * 3600l * 1000l;
    /**
     * 当前拥有的编辑过的和最近的图片张数
     */
    private int usedNumber = 0, recentNumber = 0;
    private MyDatabase mDB;
    /**
     * 只获取一次系统时间，以后都以它为基础相加，避免加入太快，毫秒不能记数
     */
    private long lastTime = System.currentTimeMillis();
    List<String> mUsualyPicPathList = new ArrayList<>();

    /**
     * 文件的信息
     */
    private List<String> dirPathList = new ArrayList<>();
    private List<String> dirInfoList = new ArrayList<>();
    private List<String> dirRepresentPathList = new ArrayList<>();

    /**
     * 添加出常用的图片
     */
    private List<String> usualyFilesList = new ArrayList<>();
    private int totalPicNumber = 0;

    public ProcessUsuallyPicPath(Context context) {
        mContext = context;
        mHandler = null;
    }

    public ProcessUsuallyPicPath(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public List<String> getUsuallyPathFromDB() {
        try {
            mDB = MyDatabase.getInstance(mContext);
            mUsualyPicPathList.add(USED_FLAG);
            mDB.queryAllUsedPic(mUsualyPicPathList);
            usedNumber = mUsualyPicPathList.size() - 1;
            mUsualyPicPathList.add(RECENT_FLAG);
            mUsualyPicPathList.add(PREFER_FLAG);
            mDB.queryAllPreferPic(mUsualyPicPathList);
            for (String path : usualyFilesList) {
                FileTool.getOrderedPicListInFile(path, mUsualyPicPathList);
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
     * 同时会添加到数据库中
     */
    //注意数据库，内存双添加,以及相关参数改变
    public void addUsedPath(String path) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            //如果存在，需要先删除原来的
            if (mUsualyPicPathList.indexOf(path) <= usedNumber) {
                mDB.deleteUsedPic(path);
                mUsualyPicPathList.remove(path);
                usedNumber--;
            }
            if (usedNumber > MAX_USED_NUMBER) {
                mDB.deleteOdlestUsedPic();//超过预定数量时，删除一个，再添加
                mUsualyPicPathList.remove(usedNumber);
                usedNumber--;
            }
            mDB.insertUsedPic(path, lastTime++);
            mUsualyPicPathList.add(1, path);
            usedNumber++;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    /**
     * 添加最近图片
     */
    void addRecentPath(String path) {
        mUsualyPicPathList.add(usedNumber + 2 + recentNumber, path);
        recentNumber++;
    }
    /**
     * 添加最近图片，最前面
     */
    public void addRecentPathFirst(String path) {
        mUsualyPicPathList.add(usedNumber + 2, path);
        recentNumber++;
    }

    /**
     * 添加选定的常用图片
     */
    public boolean addPreferPath(String path) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            mDB.insertPreferPic(path, lastTime++);
            mUsualyPicPathList.add(getPreferStart(), path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
        return false;
    }

    /**
     * 删除常用图片
     */
    public void deletePreferPath(String path, int index) {
        try {
            mDB = MyDatabase.getInstance(mContext);
            mUsualyPicPathList.remove(index);
            mDB.deleteFrequentlyPic(path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    /**
     * 开启新线程，查询所有的图片,先清空以前的list里面的数据
     */
    public void getAllPicInfoAndRecent() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /**
                 * @param sortPictureList     里面存放pair，第一个是时间，第二个是路径
                 */
                List<Pair<Long, String>> sortedPicPathsByTime = new ArrayList<>();
                Map<String, Integer> picFileNumberMap = new TreeMap<>();
                Map<String, String> PicFileRepresentMap = new TreeMap<>();
                Map<String, Long> fileRepresentTime = new HashMap<>();
                queryPicInfoInSD(sortedPicPathsByTime,
                        picFileNumberMap, PicFileRepresentMap, fileRepresentTime,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                queryPicInfoInSD(sortedPicPathsByTime,
                        picFileNumberMap, PicFileRepresentMap, fileRepresentTime,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                long scanTime = System.currentTimeMillis();
                Collections.sort(sortedPicPathsByTime, new Comparator<Pair<Long, String>>() {
                    @Override
                    public int compare(Pair<Long, String> o1, Pair<Long, String> o2) {
                        return o2.first.compareTo(o1.first);
                    }
                });

                detectRecentExit();
                if (sortedPicPathsByTime.size() == 0) {
                    Message msg = new Message();
                    msg.obj = "change_no";
                    mHandler.sendMessage(msg);
                    return;
                }
                if (sortedPicPathsByTime.get(0).first < AllData.lastScanTime &&
                        totalPicNumber == sortedPicPathsByTime.size()) {
                    AllData.lastScanTime = scanTime;
                    Message msg = new Message();
                    msg.obj = "change_no";
                    mHandler.sendMessage(msg);
                    return;
                }
                AllData.lastScanTime = scanTime;
                totalPicNumber = sortedPicPathsByTime.size();

                updateRecent(sortedPicPathsByTime);
                updateFileInfo(picFileNumberMap, PicFileRepresentMap);

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

    private void updateFileInfo(Map<String, Integer> picFileNumberMap, Map<String, String> PicFileRepresentMap) {
        //处理文件信息
        dirPathList.clear();
        dirRepresentPathList.clear();
        dirInfoList.clear();
        // 处理文件信息,将要显示的文件信息获取出来
        dirPathList.add("aaaaa");
        dirRepresentPathList.add(mUsualyPicPathList.get(0));
        dirInfoList.add("常用图片 (" + mUsualyPicPathList.size() + ")");

        for (Map.Entry<String, Integer> entry : picFileNumberMap.entrySet()) {
            String path = entry.getKey();
            dirPathList.add(path);
            dirRepresentPathList.add(PicFileRepresentMap.get(path));
            String name = path.substring(path.lastIndexOf("/") + 1, path.length());
            dirInfoList.add("" + name + " (" + String.valueOf(entry.getValue()) + ")");
        }
    }

    /**
     * 处理最近图片,原理是删除现有的，再加上需要的，因为数量不多，这样简洁，不易出错
     *
     * @param sortedPicPathsByTime 排好序的最近图片
     */
    private void updateRecent(List<Pair<Long, String>> sortedPicPathsByTime) {
        //加入最近图片，至少MIN_RENT_NUMBER，最多MAX_RECENT_NUMBER
        //正常最近三天
        clearRecent();
        long lastTime = System.currentTimeMillis() - RECENT_DURATION_TIME;
        for (Pair<Long, String> pair : sortedPicPathsByTime) {
            if (pair.first < lastTime && recentNumber >= MIN_RECENT_NUMBER) break;
            if (recentNumber > MAX_RECENT_NUMBER) break;
            addRecentPath(pair.second);
        }
    }

    private void clearRecent() {
        for (int i = usedNumber + 2; i < usedNumber + recentNumber + 2; i++) {
            mUsualyPicPathList.remove(i);
        }
        recentNumber = 0;
    }

    /**
     * 检测最近的图片时已删除
     */
    private void detectRecentExit() {
        for (int i = usedNumber + 2; i < recentNumber + usedNumber + 2; i++) {
            String path = mUsualyPicPathList.get(i);
            if (!new File(path).exists()) {
                mUsualyPicPathList.remove(i);
                recentNumber--;
            }
        }
    }

    private boolean isInRecent(String path) {
        for (int i = usedNumber + 2; i < recentNumber + usedNumber + 2; i++) {
            if (mUsualyPicPathList.get(i).equals(path))
                return true;
        }
        return false;
    }

    /**
     * 启动一个新线程从图片数据库中获取图片信息
     *
     * @param sortPictureList  里面存放pair，第一个是时间，第二个是路径
     * @param fileNumberMap    文件内图片张数,文件路径为key
     * @param fileRepresentMap 文件的代表图片的路径
     */
    private void queryPicInfoInSD(final List<Pair<Long, String>> sortPictureList,
                                  Map<String, Integer> fileNumberMap,
                                  Map<String, String> fileRepresentMap,
                                  Map<String, Long> fileRepresentTime,
                                  final Uri uri) {
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
                long modifyTime = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)) * 1000;// 最近修改时间
                if (AllData.PIC_FILE_SIZE_MIN < size && size < AllData.PIC_FILE_SIZE_MAX) {// 图片符合条件
                    sortPictureList.add(new Pair(modifyTime, path));
                    String parentPath = path.substring(0,
                            path.lastIndexOf('/'));
                    if (fileNumberMap.containsKey(parentPath)) {
                        fileNumberMap.put(parentPath, fileNumberMap.get(parentPath) + 1);
                        if (modifyTime > fileRepresentTime.get(parentPath)) {
                            fileRepresentTime.put(parentPath, modifyTime);
                            fileRepresentMap.put(parentPath, path);
                        }
                    } else {
                        fileNumberMap.put(parentPath, 1);
                        fileRepresentTime.put(parentPath, modifyTime);
                        fileRepresentMap.put(parentPath, path);
                    }
                }
            }
            cursor.close();
        }
    }

    /**
     * 删除图片文件，并更新目录列表信息
     * <p>更新文件信息，文件是否还存在，图片张数，最新图片，描述信息的字符串
     * <p>不会操作数据库
     * <p>注意发送删除通知
     *
     * @return 是否删除成功
     */
    public boolean onDeleteOnePicInfile(String picPath) {
        File file = new File(picPath);
        String dirPath = file.getParent();
        if (file.exists()) {
            if (!file.delete()) {
                return false;
            }
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(new File(picPath)));
            mContext.sendBroadcast(scanIntent);
            MediaScannerConnection.scanFile(mContext, new String[]{dirPath}, null, null);
        }

//更新文件目录信息
        int id = dirPathList.indexOf(dirPath);//图片所在目录的位置id
        if (id != -1) {
            List<String> paths = new ArrayList<>();
            FileTool.getOrderedPicListInFile(dirPath, paths);
            if (paths.size() == 0)//如果此目录下面已经没有图片
            {
                dirPathList.remove(id);
                dirRepresentPathList.remove(id);
                dirInfoList.remove(id);
            } else {//还有图片则更新信息
                dirRepresentPathList.remove(id);
                dirRepresentPathList.add(id, paths.get(0));
                dirInfoList.remove(id);
                String name = dirPath.substring(dirPath.lastIndexOf("/") + 1, dirPath.length());
                dirInfoList.add(id, "  " + name + " (" + paths.size() + ")");
            }
        }
        return true;
    }

    public List<String> getDirPathList() {
        return dirPathList;
    }

    public List<String> getDirRepresentPathList() {
        return dirRepresentPathList;
    }

    public List<String> getDirInfoList() {
        return dirInfoList;
    }

    /**
     * 获取喜爱图片的开始位置
     */
    public int getPreferStart() {
        return usedNumber + recentNumber + 3;
    }

    public void onDeleteUsuallyPicture(String path) {
        mDB = MyDatabase.getInstance(mContext);
        try {
            //如果包含在最近使用列表

            int id = mUsualyPicPathList.indexOf(path);
            if (1 <= id && id <= usedNumber) {
                mDB.deleteUsedPic(path);
                mUsualyPicPathList.remove(id);
                usedNumber--;
            }
            //如果包含在最近图片列表
            id = mUsualyPicPathList.indexOf(path);
            if (usedNumber + 2 <= id && id < usedNumber + recentNumber + 2) {
                mUsualyPicPathList.remove(id);
                recentNumber--;
            }
            //如果包含在常用列表
            if (mUsualyPicPathList.contains(path)) {
                mDB.deleteFrequentlyPic(path);
                mUsualyPicPathList.remove(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }
    }

    public void prepareLatestPic() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Util.P.le("准备获取最新图片线程开始执行");
                // 排序处理得到的图片的map
                Map<Long, String> sortedPicPathsByTime = new TreeMap<>();
                queryPicInfoInSD(sortedPicPathsByTime, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Util.P.le("准备获取最新图片线程开始执行2");
                queryPicInfoInSD(sortedPicPathsByTime, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                Util.P.le("准备获取最新图片线程开始执行3");
                Message msg = new Message();
                msg.obj = "latest_pic";
                Bundle bundle = new Bundle();
                List<String> list = new ArrayList<>(sortedPicPathsByTime.values());
                bundle.putString("pic_path", list.get(list.size() - 1));
                Util.P.le("准备获取最新图片线程开始执行4"+"图片已获取到");
                msg.setData(bundle);
                if (mHandler == null) {
                    throw new IllegalArgumentException(this.getClass().getSimpleName() + "更新图片的handler为空");
                }
                mHandler.sendMessage(msg);
            }

            private void queryPicInfoInSD(Map<Long, String> sortPictureList, Uri uri) {
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
                        long modifyTime = cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)) * 1000;// 最近修改时间
                        if (AllData.PIC_FILE_SIZE_MIN < size && size < AllData.PIC_FILE_SIZE_MAX) {// 图片符合条件
                            sortPictureList.put(modifyTime, path);
                        }
                    }
                    cursor.close();
                }
            }
        };
        new Thread(runnable).start();
    }

    public int getUsedNumber() {
        return usedNumber;
    }

    public int getRecentNumber() {
        return recentNumber;
    }

    public int getPreferNumber() {
        return usualyFilesList.size() - usedNumber - recentNumber - 3;
    }

    public boolean isUsuPic(List<String> imagUrls) {
        return imagUrls == mUsualyPicPathList;
    }

    public boolean hasRecentPic(String picPath) {
        int id=mUsualyPicPathList.indexOf(picPath);
        if( usedNumber + 2<=id&&id<recentNumber + usedNumber + 2)return true;
        return false;
    }
}
