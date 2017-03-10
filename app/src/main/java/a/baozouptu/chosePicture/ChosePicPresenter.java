package a.baozouptu.chosePicture;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import a.baozouptu.chosePicture.data.PicDirInfoManager;
import a.baozouptu.chosePicture.data.PicInfoScanner;
import a.baozouptu.chosePicture.data.PicInfoScanner.PicUpdateType;
import a.baozouptu.chosePicture.data.UsuPathManger;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.FileTool;
import a.baozouptu.common.util.Util;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by LiuGuicen on 2017/1/17 0017.
 */

class ChosePicPresenter implements ChoosePicContract.PicPresenter {
    private final String TAG = "ChosePicPresenter";
    private ChoosePicContract.View view;
    /**
     * 当前要显示的所有图片的路径
     */
    private List<String> currentPicPathList;
    private PicGridAdapter picAdapter;

    private UsuPathManger usuManager;
    private final PicDirInfoManager picDirInfoManager;
    private PicInfoScanner picInfoScanner;
    private List<String> picPathInFile;
    private MyFileListAdapter fileAdapter;
    private String latestPic;

    ChosePicPresenter(ChoosePicContract.View view) {
        this.view = view;

        usuManager = new UsuPathManger(AllData.appContext);
        picDirInfoManager = new PicDirInfoManager();
        picInfoScanner = new PicInfoScanner(usuManager, picDirInfoManager);
        picPathInFile = new ArrayList<>();
    }

    /**
     * 获取所有图片的文件信息，最近的图片，
     * <p>并且为图片grid，文件列表加载数据
     */
    @Override
    public void start() {
        Observable.create(
                new Observable.OnSubscribe<Integer>() {

                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        //线程数据库获取里面甩所有的图片信息
                        usuManager.initFromDB();
                        Log.e(TAG, "call: 从数据库获取数据完成");
                        //扫描器扫描信息，然后通知UI更新，先会更新图片，在是文件的
                        picInfoScanner.updateAllPicInfo();
                        subscriber.onNext(1);//更新图片
                        subscriber.onNext(2);//更新文件信息
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Func1<Integer, PicUpdateType>() {
                    @Override
                    public PicUpdateType call(Integer type) {
                        Log.e(TAG, "call: 当前线程" + Thread.currentThread().getName());
                        if (type == 1)
                            return picInfoScanner.updateRecentPic(usuManager);
                        else return picInfoScanner.updateAllFileInfo(usuManager);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PicUpdateType>() {
                               @Override
                               public void onCompleted() {
                                   Log.e(TAG, "Rx的onCompleted: ");
                               }

                               @Override
                               public void onError(Throwable throwable) {
                                   Log.e(TAG, "Rx的 onError: ");
                                   view.showPicList();
                               }

                               /**
                                * 读取数据成功，第一次显示图片
                                * @param updateType 更新的类型
                                */
                               @Override
                               public void onNext(PicUpdateType updateType) {
                                   Log.e(TAG, "Rx 的onNext: ");
                                   switch (updateType) {
                                       case CHANGE_ALL_PIC:
                                           currentPicPathList = usuManager.getUsuPaths();
                                           picAdapter.setImageUrls(currentPicPathList);
                                           view.showPicList();
                                           Util.P.le(TAG, "初始化显示图片完成");
                                           break;
                                       case CHANGE_ALL_FILE:
                                           view.showFileInfoList();

                                   }
                               }
                           }
                );
    }

    @Override
    public void addUsedPath(String recent_use_pic) {
        usuManager.addUsedPath(recent_use_pic);
        picDirInfoManager.updateUsuInfo(usuManager.getUsuPaths());
        fileAdapter.notifyDataSetChanged();
        if (currentPicPathList == usuManager.getUsuPaths()) { //当前在常用图片下
            view.refreshPicList();
        }
    }

    @Override
    public PicGridAdapter getPicAdapter() {
        picAdapter = new PicGridAdapter((Context) view, usuManager);
        return picAdapter;
    }

    @Override
    public MyFileListAdapter getFileAdapter() {
        fileAdapter = new MyFileListAdapter((Context) view, picDirInfoManager.getPicDirInfos());
        return fileAdapter;
    }

    @Override
    public int getPreferStart() {
        return usuManager.getPreferStart();
    }

    @Override
    public void deletePreferPath(String path, int finalPosition) {
        usuManager.deletePreferPath(path);
        picDirInfoManager.updateUsuInfo(usuManager.getUsuPaths());
        fileAdapter.notifyDataSetChanged();
        if (currentPicPathList == usuManager.getUsuPaths())
            view.refreshPicList();
    }

    @Override
    public void addPreferPath(String path) {
        usuManager.addPreferPath(path);
        picDirInfoManager.updateUsuInfo(usuManager.getUsuPaths());
        fileAdapter.notifyDataSetChanged();
        if (currentPicPathList == usuManager.getUsuPaths())
            view.refreshPicList();
    }

    @Override
    public boolean onDeleteOnePicInfile(String path) {
        return picDirInfoManager.onDeleteOnePicInfile(path);
    }

    @Override
    public void detectAndUpdateInfo() {
        Observable.create(
                new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        if (picInfoScanner.updateAllPicInfo()) {
                            subscriber.onNext(1);//更新图片
                            subscriber.onNext(2);//更新文件信息
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Integer, PicUpdateType>() {
                    @Override
                    public PicUpdateType call(Integer type) {
                        Log.e(TAG, "call: 进行图片更新了");
                        if (type == 1) {
                            PicUpdateType picUpdateType = picInfoScanner.updateRecentPic(usuManager);
                            if (latestPic != null && !usuManager.hasRecentPic(latestPic) &&
                                    !usuManager.getUsuPaths().contains(latestPic))//解决最新添加的图片扫描不到的问题，手动添加
                                usuManager.addRecentPathFirst(latestPic);
                            return picUpdateType;
                        } else return picInfoScanner.updateAllFileInfo(usuManager);
                    }
                })
                .subscribe(new Subscriber<PicUpdateType>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError: 跑出了错误");
                        view.refreshFileInfosList();
                    }

                    @Override
                    public void onNext(PicUpdateType updateType) {
                        switch (updateType) {
                            case CHANGE_ALL_PIC:
                                if (usuManager.isUsuPic(currentPicPathList)) {
                                    picAdapter.setImageUrls(usuManager.getUsuPaths());
                                    view.refreshPicList();
                                }
                                Util.P.le(TAG, "初始化显示图片完成");
                                break;
                            case CHANGE_ALL_FILE:
                                if (!usuManager.isUsuPic(currentPicPathList))
                                    view.refreshFileInfosList();
                        }
                    }
                });

    }

    @Override
    public void togglePicData(int position) {
        if (position == 0) {
            currentPicPathList = usuManager.getUsuPaths();
            picAdapter.setImageUrls(usuManager.getUsuPaths());
        } else {//获取将要显示的图片的列表，并且将当前要显示的列表{@code currentPicPathList}和adpter内的数据指向获取的列表
            String picDirPath = picDirInfoManager.getDirPath(position);
            picPathInFile.clear();
            FileTool.getOrderedPicListInFile(picDirPath, picPathInFile);
            currentPicPathList = picPathInFile;
            picAdapter.setImageUrls(picPathInFile);
        }
        view.onTogglePicList(picAdapter);
    }

    @Override
    public String getCurrentPath(int position) {
        if (position >= currentPicPathList.size())//多线程+设计问题，列表数据出问题了，先只有这样死守outOfBound了
            return currentPicPathList.get(currentPicPathList.size() - 1);
        return currentPicPathList.get(position);
    }

    @Override
    public int getCurrentSize() {
        return currentPicPathList.size();
    }

    @Override
    public void onDelOnePicSuccess(String path) {
        if (usuManager.getUsuPaths().contains(path)) {//包含才常用列表里面，删除常用列表中的信息
            usuManager.onDeleteUsuallyPicture(path);
            picDirInfoManager.updateUsuInfo(usuManager.getUsuPaths());
            fileAdapter.notifyDataSetChanged();
        }
        currentPicPathList.remove(path);
        view.refreshPicList();
    }

    @Override
    public void removeCurrent(String failedPath) {
        currentPicPathList.remove(failedPath);
    }

    @Override
    public void addUsedAndNewPath(String recent_use_pic, String newPicPath) {
        usuManager.addUsedPath(recent_use_pic);
        if (newPicPath != null) {//最新图片不为空，最新图等于它，并且添加到常用列表，更新文件信息
            latestPic = newPicPath;
            usuManager.addRecentPathFirst(newPicPath);
            // 这里图片没有保存到当前文件夹下面
            //更新文件信息
            if (picDirInfoManager.onAddNewPic(newPicPath)) {
                view.refreshPicList();
            }
        }
        if (currentPicPathList == usuManager.getUsuPaths())
            view.refreshPicList();

    }

    @Override
    public boolean isInPrefer(String path) {
        return usuManager.lastIndexOf(path) >= getPreferStart();
    }

    @Override
    public boolean isInUsu() {
        return currentPicPathList == usuManager.getUsuPaths();
    }

    @Override
    public void toggleToUsu() {
        togglePicData(0);//就像点击到文件0一样
    }
}