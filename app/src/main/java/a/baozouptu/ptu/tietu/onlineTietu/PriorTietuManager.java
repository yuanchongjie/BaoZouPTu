package a.baozouptu.ptu.tietu.onlineTietu;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.FileTool;
import a.baozouptu.network.NetWorkState;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SQLQueryListener;
import rx.Subscriber;

/**
 * Created by LiuGuicen on 2017/2/21 0021.
 * 管理贴图界面贴图列表（优先贴图）的类
 * 一定注意本地文件和服务器URL的文件名相同，使用时一一对应，出错很麻烦
 */

public class PriorTietuManager {

    /**
     * 缓存时间
     */
    public static final long cacheExpire = TimeUnit.DAYS.toMillis(1) * 5;
    static final int PRIOR_TIETU_NUMBER = 50;

    public static void queryTietuByCategory(final String category, final Subscriber<? super List<tietu_material>> subscriber) {
        Log.e("---------", "queryAllExpressions: ");
        BmobQuery<tietu_material> query = new BmobQuery<>();
        String sql = "select * " +
                " from tietu_material" +
                " where category = '" + category + "'" +
                " order by heat desc" +
                " limit " + PRIOR_TIETU_NUMBER;
        query.setSQL(sql);
        boolean useCache = false;
        if (query.hasCachedResult(tietu_material.class) || NetWorkState.detectNetworkType() == -1) {
            useCache = true;
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
        } else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.setMaxCacheAge(cacheExpire);
        }
        final boolean finalUseCache = useCache;
        query.doSQLQuery(
                new SQLQueryListener<tietu_material>() {
                    @Override
                    public void done(BmobQueryResult<tietu_material> result, BmobException e) {
                        if (e != null || result == null) {
                            subscriber.onError(e);
                        }
                        List<tietu_material> resultList = result.getResults();
                        if (resultList == null)
                            resultList = new ArrayList<>();
                        if (finalUseCache) {//如果使用cache，就不用更新本地文件夹
                            subscriber.onNext(resultList);
                        } else
                            updateLocalTietu(category, resultList, subscriber);
                    }
                });
    }

    /**
     * 更新本地的贴图，删除不用的
     */
    private static void updateLocalTietu(String category, List<tietu_material> tietuMaterials, final Subscriber<? super List<tietu_material>> subscriber) {
        File[] exitFiles = FileTool.getAllChildFiles(AllData.getTietuDir());
        //获取文件的名称列表
        List<String> fileName = new ArrayList<>();
        for (File file : exitFiles) {
            fileName.add(file.getName());
        }
        //获取贴图的名称列表
        final List<String> tietuNames = new ArrayList<>();
        for (int i = 0; i < tietuMaterials.size(); i++) {
            String name = tietuMaterials.get(i).getTheOnlyName();
            tietuNames.add(name);
        }

        //判断文件是否有效，无效的都删除
        for (int i = 0; i < fileName.size(); i++) {
            String fname = fileName.get(i);
            if (!category.equals(getCategoryByFileName(fname))) continue;//不是本类别的贴图，不管
            if (!tietuNames.contains(getTietuNameByFileName(fname))) {//新下载的贴图列表里面没有，就删除
                exitFiles[i].delete();
            }
        }
        subscriber.onNext(tietuMaterials);
    }

    /**
     * 获取本地文件上的贴图
     *
     * @return 存在时返回贴图的文件 ，本地不存在时返回空，并且下载
     */
    public static File getLocalTietuFile(final tietu_material tietuMaterial) {
        String tietuPath = PriorTietuManager.getTietuFilePath(tietuMaterial);
        if (tietuPath == null) return null;
        final File tietuFile = new File(tietuPath);
        if (!tietuFile.exists()) {
            tietuMaterial.getUrl().download(tietuFile, new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null)
                        Log.e("PriorTietuManager", "贴图: 下载成功" + tietuMaterial.getUrl().getUrl());
                    else
                        Log.e("PriorTietuManager", "贴图: 下载失败" + tietuMaterial.getUrl().getUrl());
                }

                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
            return null;
        } else
            return tietuFile;
    }

    public static String getTietuFilePath(tietu_material tietuMaterial) {
        return AllData.getTietuDir() + "/" + tietuMaterial.getCategory() + "_" + tietuMaterial.getTheOnlyName();
    }

    public static String getCategoryByFileName(String fileName) {
        int id = fileName.indexOf('_');
        if (id != -1)
            return fileName.substring(0, id);
        else return " ";
    }

    public static String getTietuNameByFileName(String fileName) {
        int id = fileName.indexOf("_");
        if (id == -1) id = 0;
        return fileName.substring(id + 1);
    }
}
