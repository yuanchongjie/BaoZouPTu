package a.baozouptu.chosePicture;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import a.baozouptu.R;
import a.baozouptu.chosePicture.view.LongPicPopupWindow;
import a.baozouptu.chosePicture.view.ToSettingPopupWindow;
import a.baozouptu.common.BaseActivity;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.dataAndLogic.AsyncImageLoader;
import a.baozouptu.common.util.Util;
import a.baozouptu.common.view.FirstUseDialog;
import a.baozouptu.ptu.PtuActivity;

/**
 * 显示所选的最近的或某个文件夹下面的所有图片
 * 并且有选择文件夹，相机，空白图画图的功能
 */
public class ChosePictureActivity extends BaseActivity implements ChoosePicContract.View {
    private String TAG = "ChosePictureActivity";
    /**
     * 进度条
     */
    private ProgressDialog m_ProgressDialog = null;

    private DrawerLayout fileListDrawer;
    private PicGridAdapter picAdapter;
    private RecyclerView pictureGridview;

    private GridLayoutManager gridLayoutManager;
    boolean isFromCreate = false;
    private ChoosePicContract.PicPresenter presenter;
    private boolean isChooseTietu = false;
    private MyFileListAdapter fileInfosAdapter;

    /**
     * Called when the activity is first created.
     * 过程描述：启动一个线程获取所有图片的路径，再启动一个子线程设置好GridView，而且要求这个子线程必须在ui线程之前启动
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_picture);
        Log.e(TAG, "启动了");
        Intent sourceIntent = getIntent();
        if (sourceIntent != null) {
            String s = sourceIntent.getAction();
            if (s != null && s.equals("tietu"))
                isChooseTietu = true;
        }
        test();
        presenter = new ChosePicPresenter(this);
        initView();
        isFromCreate = true;
    }

    private void test() {
        if (getIntent().getStringExtra("test") == null) return;
        Intent testIntent = new Intent(this, PtuActivity.class);
        testIntent.putExtras(getIntent());
        String thePath = Environment.getExternalStorageDirectory() + "/test.jpg";
        testIntent.putExtra("pic_path", thePath);
        startActivityForResult(testIntent, 0);
        Log.e(TAG, "完成时间  " + System.currentTimeMillis());
    }

    private void initView() {
        if (isChooseTietu)
            switchViewForTietu();
        else {
            final ImageButton btnToSetting = (ImageButton) findViewById(R.id.chose_pic_navigation);
            btnToSetting.setMaxWidth(Util.dp2Px(35));
            btnToSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToSettingPopupWindow.show(v, ChosePictureActivity.this);
                }
            });
        }
        fileListDrawer = (DrawerLayout) findViewById(R.id.drawer_layout_show_picture);
        pictureGridview = (RecyclerView) findViewById(R.id.gv_photolist);
        final ImageButton showFile = (ImageButton) findViewById(R.id.show_pic_file);
        showFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileListDrawer.isDrawerOpen(GravityCompat.END))
                    fileListDrawer.closeDrawer(GravityCompat.END);
                else
                    fileListDrawer.openDrawer(GravityCompat.END);
            }
        });
        initPicListView();
    }

    /**
     * 从贴图过来时，就切换到贴图的情况
     */
    private void switchViewForTietu() {
        final ImageButton returnBtn = (ImageButton) findViewById(R.id.chose_pic_navigation);
        returnBtn.setImageDrawable(Util.getDrawable(R.drawable.ic_arrow_back_white));
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initPicListView() {
        picAdapter = presenter.getPicAdapter();
        gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (picAdapter.getItemViewType(position) == PicGridAdapter.GROUP_HEADER)
                    return 3;
                return 1;
            }
        });
        pictureGridview.setLayoutManager(gridLayoutManager);
        picAdapter.setClickListener(
                new PicGridAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(PicGridAdapter.ItemHolder itemHolder) {
                        int position = itemHolder.getAdapterPosition();
                        String chosenPath = presenter.getCurrentPath(position);

                        if (isChooseTietu) {//选择贴图,不是一般的选择图片
                            Intent intent1 = new Intent();
                            intent1.putExtra("pic_path", chosenPath);
                            setResult(3, intent1);
                            ChosePictureActivity.this.finish();
                        } else {//正常的选择
                            Intent intent = new Intent(ChosePictureActivity.this, PtuActivity.class);
                            intent.putExtra("pic_path", chosenPath);
                            startActivityForResult(intent, 0);
                        }
                    }
                });


        picAdapter.setLongClickListener(
                new PicGridAdapter.LongClickListener()

                {
                    @Override
                    public boolean onItemLongClick(PicGridAdapter.ItemHolder itemHolder) {
                        View view = itemHolder.iv;
                        int position = itemHolder.getAdapterPosition();
                        Util.P.le("position " + position);

                        return LongPicPopupWindow.setPicPopWindow(presenter, ChosePictureActivity.this, view, position);
                    }
                }

        );

        pictureGridview.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    AsyncImageLoader imageLoader = AsyncImageLoader.getInstance();
                    int lastScrollState = RecyclerView.SCROLL_STATE_IDLE;

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        int first, last;
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE://停止时候
                                picAdapter.isScrollWidthoutTouch = false;
                                if (lastScrollState == RecyclerView.SCROLL_STATE_SETTLING)//上次是无触摸滑动，才需要显示
                                {
                                    first = gridLayoutManager.findFirstVisibleItemPosition();
                                    last = gridLayoutManager.findLastVisibleItemPosition();
                                    showAdjacentPic(first, last);
                                }
                                lastScrollState = RecyclerView.SCROLL_STATE_IDLE;
                                break;
                            case RecyclerView.SCROLL_STATE_DRAGGING://触摸滑动
                                //如果上一次是无触摸下的滑动
                                picAdapter.isScrollWidthoutTouch = false;
                                if (lastScrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                                    first = gridLayoutManager.findFirstVisibleItemPosition();
                                    last = gridLayoutManager.findLastVisibleItemPosition();
                                    //显示出无指滑动后没有显示的
                                    showAdjacentPic(first, last);
                                }
                                lastScrollState = RecyclerView.SCROLL_STATE_DRAGGING;
                                break;
                            case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动，无触摸滑动
                                picAdapter.isScrollWidthoutTouch = true;
                                imageLoader.cancelLoad();//取消解析，提交的任务还没有执行的就不执行了
                                lastScrollState = RecyclerView.SCROLL_STATE_SETTLING;
                                break;
                        }
                    }

                    private void showAdjacentPic(int first, int last) {
                        Log.e(TAG, "showAdjacentPic: 开始显示邻近的图片");
                        for (int position = first; position <= last; position++) {
                            if (position < 0 || position >= presenter.getCurrentSize())
                                break;//一行中左边有数据，右边没有数据，各自position仍然当做存在
                            Util.P.le(TAG, "" + position);
                            View view = pictureGridview.findViewWithTag(presenter.getCurrentPath(position));
                            if (view != null) {
                                picAdapter.myBindViewHolder(pictureGridview.getChildViewHolder(view),
                                        position);
                            }
                        }
                    }
                });

    }

    @Override
    public void showPicList() {
        if (!AllData.hasReadConfig.hasReadUsuPicUse()) {
            final FirstUseDialog firstUseDialog = new FirstUseDialog(ChosePictureActivity.this);
            firstUseDialog.createDialog(null, "长按图片即可添加到喜爱或删除", new FirstUseDialog.ActionListener() {
                @Override
                public void onSure() {
                    FirstUseDialog firstUseSetting = new FirstUseDialog(ChosePictureActivity.this);
                    firstUseSetting.createDialog(null, "点击左上角图标可进入设置", new FirstUseDialog.ActionListener() {
                        @Override
                        public void onSure() {
                            AllData.hasReadConfig.write_usuPicUse(true);
                        }
                    });
                }
            });
        }
        pictureGridview.setAdapter(picAdapter);
        m_ProgressDialog.dismiss();
    }

    /**
     * 删除一张图片
     *
     * @param path
     */
    public void deleteOnePic(final String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        AlertDialog alertDialog = builder.setTitle("完全删除此图片吗")
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        先从文件中删除，不能删除则不行
                        if (!presenter.onDeleteOnePicInfile(path))//删除图片文件并更新目录列表信息
                        {
                            AlertDialog alertDialog1 = new AlertDialog.Builder(ChosePictureActivity.this)
                                    .setTitle("删除失败，此图片无法删除")
                                    .setPositiveButton("确定", new AlertDialog.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create();
                            alertDialog1.show();
                            return;
                        }
                        presenter.onDelOnePicSuccess(path);
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void showFileInfoList() {
        ListView pictureFileListView = (ListView) findViewById(R.id.drawer_picture_file_list);
        fileInfosAdapter = presenter.getFileAdapter();
        pictureFileListView.setAdapter(fileInfosAdapter);
        pictureFileListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        fileListDrawer.closeDrawer(GravityCompat.END);
                        presenter.togglePicData(position);//切换数据，然后会切换视图
                    }
                });

    }

    @Override
    public void onTogglePicList(PicGridAdapter picAdapter) {
        pictureGridview.setAdapter(picAdapter);
    }

    @Override
    public void refreshPicList() {
        picAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshFileInfosList() {
        fileInfosAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        if (isFromCreate) {
            m_ProgressDialog = ProgressDialog.show(ChosePictureActivity.this, "请稍后",
                    "数据读取中...", true);
            presenter.start();
            isFromCreate = false;
        } else {
            presenter.detectAndUpdateInfo();
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Util.P.le(TAG, "onActivityResult:开始处理其它activity的返回");
        if (resultCode == 0 && data != null) {
            String action = data.getAction();
            if (action != null && action.equals("finish")) {//直接结束了
                setResult(0, new Intent(action));
                presenter.addUsedPath(
                        data.getStringExtra("recent_use_pic"));
                finish();
                overridePendingTransition(0, R.anim.go_send_exit);
            } else if (action != null && action.equals("load_failed")) {//加载失败
                String failedPath = data.getStringExtra("failed_path");
                presenter.removeCurrent(failedPath);
                picAdapter.notifyDataSetChanged();
            } else if (action != null && action.equals("save_and_leave")) {//保存了图片
                String newPicPath = data.getStringExtra("new_path");
                presenter.addUsedAndNewPath(data.getStringExtra("recent_use_pic"), newPicPath);
            } else if (action != null && action.equals("leave")) {//离开没有保存图片
                presenter.addUsedPath(
                        data.getStringExtra("recent_use_pic"));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        //  AsyncImageLoader.getInstance().stop();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        //AsyncImageLoader.getInstance().reStart();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if (presenter.isInUsu()) {
            super.onBackPressed();
        } else {
            presenter.toggleToUsu();
        }
    }

    @Override
    protected void onDestroy() {
//        如果设置退出后关闭应用，就需要在用户主动退出应用后关闭
        if (!AllData.settingDataSource.getSendShortcutNotifyExit()) {
            NotificationManager nm = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(0);

        }
        super.onDestroy();
    }

    @Override
    public void setPresenter(ChoosePicContract.PicPresenter presenter) {

    }
}
