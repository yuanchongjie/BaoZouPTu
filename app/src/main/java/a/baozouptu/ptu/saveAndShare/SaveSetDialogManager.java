package a.baozouptu.ptu.saveAndShare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.dataAndLogic.ShareDBUtil;
import a.baozouptu.common.dataAndLogic.MyDatabase;
import a.baozouptu.common.util.Util;

/**
 * Created by liuguicen on 2016/8/13.
 *
 * @description
 */
public class SaveSetDialogManager {
    private Context mContext;
    private static final int SIZE_COUNT = 6;
    private float saveRatio = 1;
    AlertDialog dialog;
    private TextView cancleView;
    private TextView sureView;
    List<TextView> sizeViewList = new ArrayList<>();
    List<Double> sizeList = new ArrayList<>();
    List<Integer> sizeIdList = new ArrayList<>();
    private RecyclerView recyclerShare;
    private long sourceSize;
    private boolean hasInit;
    private final long MAX_SIZE = 40 * 1000 * 1000;
    private int choseSizeId;
    private clickListenerInterface listener;
    private List<ListDrawableItem> shareActivityInfo;
    private List<ResolveInfo> resolveInfos;
    private ShareRecyclerAdapter shareRecyclerAdapter;
    private List<Boolean> canClickList;
    private MyQQShare myQQShare;

    public interface clickListenerInterface {
        void mSure(float saveRatio);

        void mCancel();

        String onShareItemClick(float saveRatio);
    }

    SaveSetDialogManager(Context context) {
        mContext = context;
        hasInit = false;
        getShareInfo();
    }

    public void init(long sourceSize) {
        this.sourceSize = sourceSize;
        sizeList.addAll(Arrays.asList(1d / 5, 1d / 3, 1d / 2, 1d, 2d, 3d));
        choseSizeId = 3;
        canClickList = new ArrayList<>();
        for (int i = 0; i < SIZE_COUNT; i++) canClickList.add(true);
        hasInit = true;
    }

    public void createDialog() {
        //判断对话框是否已经存在了
        if (dialog != null && dialog.isShowing()) return;
        if (!hasInit) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_save_set, null);
        cancleView = (TextView) view.findViewById(R.id.tv_save_set_cancel);
        sureView = (TextView) view.findViewById(R.id.tv_save_set_sure);
        sizeIdList.addAll(Arrays.asList(R.id.item_save_set_size1, R.id.item_save_set_size2, R.id.item_save_set_size3,
                R.id.item_save_set_size4, R.id.item_save_set_size5, R.id.item_save_set_size6));
        for (int i = 0; i < SIZE_COUNT; i++) {
            sizeViewList.add((TextView) view.findViewById(sizeIdList.get(i)));
        }

        recyclerShare = (RecyclerView) view.findViewById(R.id.recycler_save_set_share);

        dialog = builder.setView(view)
                .create();
        setStyle();

        initView();
        dialog.show();
    }

    private void initView() {
        setChoseSizeUi();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
        recyclerShare.setLayoutManager(layoutManager);
        shareRecyclerAdapter = new ShareRecyclerAdapter(mContext, shareActivityInfo);
        recyclerShare.setAdapter(shareRecyclerAdapter);
    }

    private void getShareInfo() {
        MyDatabase myDatabase = MyDatabase.getInstance(mContext);
        List<Pair<String, String>> preferShare = new ArrayList<>();
        try {
            myDatabase.queryAllPreferShare(preferShare);
        } catch (IOException e) {

        } finally {
            myDatabase.close();
        }
        resolveInfos = ShareUtil.getShareTargets(mContext, ShareUtil.Type.Image);
        shareActivityInfo = ShareUtil.getSortedAppData(mContext, preferShare, resolveInfos);
    }

    public void setClickListener(final clickListenerInterface listenner) {
        this.listener = listenner;
        sureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listenner.mSure(saveRatio);
            }
        });
        cancleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listenner.mCancel();
            }
        });
        shareRecyclerAdapter.setOnItemClickListener(
                new ShareRecyclerAdapter.OnRecyclerViewItemClickListener() {

                    @Override
                    public void onItemClick(View view, ListDrawableItem data) {
                        Util.P.le("Savaset", "分享受到点击");
                        String picPath = listenner.onShareItemClick(saveRatio);
                        if (picPath == null) {
                            Toast.makeText(mContext, "文件保存失败，无法分享", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int clickPosition = shareActivityInfo.indexOf(data);
                        ResolveInfo resolveInfo = resolveInfos.get(clickPosition);

                        //将优先信息添加到数据库
                        String packageName = resolveInfo.activityInfo.packageName;
                        String title = resolveInfo.loadLabel(mContext.getPackageManager()).toString();
                        ShareDBUtil.inseartPreferInfo(mContext, packageName, title);
                        //是腾讯的分享且用户设置可带有应用图标
                        if (title.equals("发送给好友")
                                && packageName.equals("com.tencent.mobileqq")
                                && !AllData.settingDataSource.getSharedWithout()) {
                            myQQShare = new MyQQShare();
                            ((AppCompatActivity) mContext).getFragmentManager().beginTransaction().add(
                                    myQQShare, "MyQQShare"
                            ).commit();
                            myQQShare.share(picPath, mContext);
                        } else {
                            //如果shareType是Image，那么分享的内容应该为图片在SD卡的路径
                            ShareUtil.exeShare(mContext, "图片分享", resolveInfo, picPath, ShareUtil.Type.Image);
                        }
                        dialog.dismiss();
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        myQQShare.onActivityResult(requestCode, resultCode, data);
    }

    private void setChoseSizeUi() {
        for (int i = 0; i < SIZE_COUNT; i++) {
            final int id = i;
            final TextView sizeView = sizeViewList.get(i);
            if (sourceSize * sizeList.get(i) * sizeList.get(i) > MAX_SIZE
                    && i > choseSizeId) {
                canClickList.set(i, false);
                sizeView.setBackground(Util.getDrawable(R.drawable.save_set_canot_chosed));
            } else {
                if (choseSizeId == i) {
                    sizeView.setBackground(Util.getDrawable(R.drawable.save_set_chosed));
                } else {
                    sizeViewList.get(i).setBackground(Util.getDrawable(R.drawable.save_set_notchosed));
                }
                sizeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (id != choseSizeId) {
                            sizeViewList.get(choseSizeId).setBackground(Util.getDrawable(
                                    R.drawable.save_set_notchosed));
                            v.setBackground(Util.getDrawable(R.drawable.save_set_chosed));
                            saveRatio = sizeList.get(id).floatValue();
                            choseSizeId = id;
                        }
                    }
                });
            }
        }
    }

    /**
     * 设置风格：无标题
     */
    private void setStyle() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


}

