package a.baozouptu.chosePicture.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.Util;

/**
 * Created by LiuGuicen on 2017/1/18 0018.
 * 图片信息扫描器，处理各种sd卡图片扫描的问题
 */

public class PicInfoScanner {
    private long lastScanTime = 0;
    //    按时间排序的时间路径对应列表
    private List<Pair<Long, String>> sortedPicPathsByTime;
    //    文件中图片张数信息
    private Map<String, Integer> picFileNumberMap;
    //    文件代表图片信息
    private Map<String, String> picFileRepresentMap;

    /**
     * Created by LiuGuicen on 2017/1/17 0017.
     */

    public enum PicUpdateType {
        NO_CHANGE,
        /**
         * 改变所有图片
         */
        CHANGE_ALL_PIC,
        CHANGE_ALL_FILE,
        CHANGE_PIC,
        CHANGE_FILE,
        CHANGE_RECENT
    }

    private int totalPicNumber;

    private PicDirInfoManager picDirInfoManager;

    public PicInfoScanner(UsuPathManger usuPathManger, PicDirInfoManager picDirInfoManager) {
        this.picDirInfoManager = picDirInfoManager;
        totalPicNumber = 0;
    }

    /**
     * 查询所有的图片,先清空以前的list里面的数据
     * 检测是否有更新信息，有就更新信息，
     * <p>注意发生更新时文件信息会被全部更新掉
     *
     * @return 返回是否需要更新
     */
    public boolean updateAllPicInfo() {
        //首先查出所有信息
        // 里面存放pair，第一个是时间，第二个是路径
        sortedPicPathsByTime = new ArrayList<>();
        picFileNumberMap = new TreeMap<>();
        picFileRepresentMap = new TreeMap<>();
        queryPicInfoInSD(sortedPicPathsByTime,
                picFileNumberMap, picFileRepresentMap,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        queryPicInfoInSD(sortedPicPathsByTime,
                picFileNumberMap, picFileRepresentMap,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        long scanTime = System.currentTimeMillis();
        Collections.sort(sortedPicPathsByTime, new Comparator<Pair<Long, String>>() {
            @Override
            public int compare(Pair<Long, String> o1, Pair<Long, String> o2) {
                return o2.first.compareTo(o1.first);
            }
        });
        if (sortedPicPathsByTime.size() == 0) {
            return false;
        }
        if (sortedPicPathsByTime.get(0).first < lastScanTime &&
                totalPicNumber == sortedPicPathsByTime.size()) {
            lastScanTime = scanTime;
            return false;
        }
        lastScanTime = scanTime;
        return true;
    }

    /**
     * 更新最近图片信息，在usu列表中的
     *
     */
    public PicUpdateType updateRecentPic(UsuPathManger usuPathManger) {
        totalPicNumber = sortedPicPathsByTime.size();
        usuPathManger.updateRecentInfoInUsu(sortedPicPathsByTime);
        sortedPicPathsByTime.clear();
        return PicUpdateType.CHANGE_ALL_PIC;
    }

    /**
     * 更新图片文件的信息，在drawer中的，包括文件目录信息，文件中图片数目，最新图片的路径
     */
    public PicUpdateType updateAllFileInfo(UsuPathManger usuPathManger) {
        //处理文件信息
        picDirInfoManager.clear();//清理
        picDirInfoManager.updateUsuInfo(usuPathManger.getUsuPaths());//给常用图片添加信息
        picDirInfoManager.updateAllFileInfo(picFileNumberMap, picFileRepresentMap);//添加其他文件的信息
        Log.e("Rx更新", "updateAllFileInfo: 发送文件更新信息");
        picFileNumberMap.clear();
        picFileRepresentMap.clear();
        return PicUpdateType.CHANGE_ALL_FILE;
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
                                  final Uri uri) {
        if (uri == null) return;//不为空，放入图片
        String[] projection = {MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
        Map<String, Long> fileRepresentTime = new HashMap<>();

        Cursor cursor = AllData.appContext.getContentResolver().query(uri,
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
                    sortPictureList.add(new Pair<>(modifyTime, path));
                    String parentPath = path.substring(0,
                            path.lastIndexOf('/'));
                    if (fileRepresentTime.containsKey(parentPath)) {
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

    public static String getLastestPicPath() {
        Util.P.le("准备获取最新图片线程开始执行");
        // 排序处理得到的图片的map,treeMap有序的
        TreeMap<Long, String> sortedPicPathsByTime = new TreeMap<>();
        queryPicInfoInSD(sortedPicPathsByTime, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Util.P.le("准备获取最新图片线程开始执行2");
        queryPicInfoInSD(sortedPicPathsByTime, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        Util.P.le("准备获取最新图片线程开始执行3");
        List<String> list = new ArrayList<>(sortedPicPathsByTime.values());
        Util.P.le("准备获取最新图片线程开始执行4" + "图片已获取到");
        if (list.size() == 0) return null;
        return list.get(list.size() - 1);
    }

    /**
     * 从某张sd卡中获取所有图片路径，并放入有序的TreeMap中
     *
     * @param sortPictureList 有序的TreeMap中
     * @param uri             URI
     */
    private static void queryPicInfoInSD(TreeMap<Long, String> sortPictureList, Uri uri) {
        if (uri == null) return;//不为空，放入图片
        String[] projection = {MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};

        Cursor cursor = AllData.appContext.getContentResolver().query(uri,
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
}
