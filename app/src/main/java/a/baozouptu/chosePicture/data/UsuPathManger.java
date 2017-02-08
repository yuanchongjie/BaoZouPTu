package a.baozouptu.chosePicture.data;

import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import a.baozouptu.common.dataAndLogic.MyDatabase;
import a.baozouptu.common.util.FileTool;


/**
 * 用来处理常用图片的类，作为activity与底层的接口。包括数据库，ContentProvider，文件等
 * 增删改查等功能
 * <p>
 * 数据的顺序，最近常用用图片
 * <p>最近图片
 * <p>喜爱图片图片
 * <p>
 * </p>
 */
public class UsuPathManger {
    public final static String USED_FLAG = "@%^@#GDa_USED_FLAG";
    public final static String RECENT_FLAG = "@%^@#GDa_RECENT_FLAG";
    public final static String PREFER_FLAG = "@%^@#GDa_PREFER_FLAG";
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
    private List<String> mUsualyPicPathList = new ArrayList<>();


    /**
     * 添加出常用的图片
     */
    private List<String> usualyFilesList = new ArrayList<>();

    public UsuPathManger(Context context) {
        mContext = context;
    }

    public List<String> initFromDB() {
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
            int id = mUsualyPicPathList.indexOf(path);
            if (id != -1 && id <= usedNumber) {
                mDB.deleteUsedPic(path);
                mUsualyPicPathList.remove(path);
                usedNumber--;
            }
            if (usedNumber >= MAX_USED_NUMBER) {
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
     * 添加最近图片,最后面
     */
    private void addRecentPathEnd(String path) {
        mUsualyPicPathList.add(usedNumber + 2 + recentNumber, path);
        recentNumber++;
    }

    /**
     * 添加最近图片，最前面
     */
    public void addRecentPathFirst(String path) {
        mUsualyPicPathList.add(usedNumber + 2, path);
        recentNumber++;
        Log.e("---------", "addRecentPathFirst: 添加最近图片成功+数量=" + recentNumber);
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
     * 检测最近的图片是否存在，不存在就删除
     * @return 返回是否有变动的
     */
    public boolean checkRecentExit() {
        boolean has=false;
        for (int i = usedNumber + 2; i < recentNumber + usedNumber + 2; i++) {
            String path = mUsualyPicPathList.get(i);
            if (!new File(path).exists()) {
                mUsualyPicPathList.remove(i);
                recentNumber--;
                has=true;
            }
        }
        return has;
    }

    private boolean isInRecent(String path) {
        for (int i = usedNumber + 2; i < recentNumber + usedNumber + 2; i++) {
            if (mUsualyPicPathList.get(i).equals(path))
                return true;
        }
        return false;
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
        int id = mUsualyPicPathList.indexOf(picPath);
        if (usedNumber + 2 <= id && id < recentNumber + usedNumber + 2) return true;
        return false;
    }


    /**
     * 更新usu列表中的最近图片信息
     * <p>加入最近图片，至少MIN_RENT_NUMBER，最多MAX_RECENT_NUMBER,且最近三天的
     * <p>
     * <p>处理最近图片,原理是删除现有的，再加上需要的，因为数量不多，这样简洁，不易出错
     *
     * @param sortedPicPathsByTime 排好序的最近图片
     */
    void updateRecentInfoInUsu(List<Pair<Long, String>> sortedPicPathsByTime) {

        //先清空所有的最近图片路径
        for (int i = usedNumber + recentNumber + 1; i >= usedNumber + 2; i--) {
            mUsualyPicPathList.remove(i);
        }
        recentNumber = 0;

        //再添加找出的最近的路径
        long lastTime = System.currentTimeMillis() - RECENT_DURATION_TIME;
        for (Pair<Long, String> pair : sortedPicPathsByTime) {
            if (pair.first < lastTime && recentNumber >= MIN_RECENT_NUMBER) break;
            if (recentNumber > MAX_RECENT_NUMBER) break;
            addRecentPathEnd(pair.second);
        }
    }

    public List<String> getUsuPaths() {
        return mUsualyPicPathList;
    }

    public int lastIndexOf(String path) {
        return mUsualyPicPathList.lastIndexOf(path);
    }
}
