package a.baozouptu.chosePicture;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.dataAndLogic.AsyncImageLoader3;
import a.baozouptu.base.util.FileTool;
import a.baozouptu.base.util.Util;
import a.baozouptu.base.view.FirstUseDialog;
import a.baozouptu.ptu.PtuActivity;

/**
 * 显示所选的最近的或某个文件夹下面的所有图片
 * 并且有选择文件夹，相机，空白图画图的功能
 */
public class ChosePictureActivity extends AppCompatActivity {
    private String TAG = "ChosePictureActivity";
    /**
     * 进度条
     */
    private ProgressDialog m_ProgressDialog = null;
    /**
     * 保存最近图片的路径
     */
    public static List<String> usualyPicPathList = new ArrayList<>();
    /**
     * 获取和保存某个文件下面所有图片的路径
     */
    private List<String> picPathInFile = new ArrayList<>();
    /**
     * 当前要现实的所有图片的路径
     */
    private List<String> currentPicPathList = new ArrayList<>();

    /**
     * 新P的图片，不好添加，用这个显示添加
     */
    String editedPicPath;

    /**
     * 文件列表的几个相关list
     */
    private List<String> dirInfoList;
    private List<String> dirPathList;

    private DrawerLayout fileListDrawer;
    private PicGridAdapter picAdpter;
    private RecyclerView pictureGridview;
    private ProcessUsuallyPicPath usuPicProcessor;

    private ListView pictureFileListView;
    private MyFileListAdapter fileAdapter;
    boolean isFirst = true;
    private GridLayoutManager gridLayoutManager;

    private String chosedPath;

    /**
     * Called when the activity is first created.
     * 过程描述：启动一个线程获取所有图片的路径，再启动一个子线程设置好GridView，而且要求这个子线程必须在ui线程之前启动
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_picture);
//        test();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                m_ProgressDialog.dismiss();// 表示此处开始就解除这个进度条Dialog，应该是在相对起始线程的另一个中使用
                if (msg.obj.equals("change_pic")) {
                    if (isFirst) {
                        pictureGridview.setAdapter(picAdpter);
                        isFirst = false;
                    } else {
                        picAdpter.notifyDataSetChanged();
                    }
                    Util.P.le(TAG, "finish update picture");
                } else if (msg.obj.equals("change_file")) {
                    Util.P.le(TAG, "finish update file");
                    fileAdapter.notifyDataSetChanged();
                    picAdpter.notifyDataSetChanged();
                } else {
                    if (editedPicPath != null) {
                        if (!usuPicProcessor.hasRecentPic(editedPicPath)) {
                            usuPicProcessor.addRecentPathFirst(editedPicPath);
                            picAdpter.notifyDataSetChanged();
                            Util.P.le("已更新图片");
                        }
                        editedPicPath = null;
                    }
                }
            }
        };
        usuPicProcessor = new ProcessUsuallyPicPath(this, handler);
        getScreenWidth();
        initView();
        initToolbar();
        m_ProgressDialog = ProgressDialog.show(ChosePictureActivity.this, "请稍后",
                "数据读取中...", true);
        initPicInfo();
    }


    private void test() {
        if (getIntent().getStringExtra("test") == null) return;
        Intent intent1 = new Intent(this, PtuActivity.class);
        intent1.putExtras(getIntent());
        String thePath = "/storage/sdcard1/中大图(调试用，不能删).jpg";
        intent1.putExtra("pic_path", thePath);
        chosedPath = thePath;
        startActivityForResult(intent1, 0);
    }

    /**
     * 获取所有图片的文件信息，最近的图片，
     * <p>并且为图片grid，文件列表加载数据
     */
    private void initPicInfo() {
        usualyPicPathList = usuPicProcessor.getUsuallyPathFromDB();
        currentPicPathList = usualyPicPathList;
        Util.P.le(TAG, "获取了上次数据库中的图片");
        disposeShowPicture();
        Util.P.le(TAG, "初始化显示图片完成");
        disposeDrawer();
        Util.P.le(TAG, "初始化显示Drawer完成");
    }

    /**
     * 获取屏幕的宽度
     */
    void getScreenWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        AllData.screenWidth = metric.widthPixels; // 屏幕宽度（像素）
        AllData.screenHeight = metric.heightPixels;
    }

    private void initView() {
        if(AllData.appConfig.hasReadUsuPicUse()) {
            FirstUseDialog firstUseDialog = new FirstUseDialog(this);
            firstUseDialog.createDialog(null, "长按图片即可添加到喜爱或删除", new FirstUseDialog.ActionListener() {
                @Override
                public void onSure() {
                    AllData.appConfig.writeConfig_usuPicUse(true);
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
    }

    @TargetApi(19)
    private void initToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(Util.getColor(this, R.color.base_toolbar_background));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    /**
     * 为显示图片的gridView加载数据
     */
    private void disposeShowPicture() {
        picAdpter = new PicGridAdapter(
                ChosePictureActivity.this, currentPicPathList, usuPicProcessor);
        gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (picAdpter.getItemViewType(position) == PicGridAdapter.GROUP_HEADER)
                    return 3;
                return 1;
            }
        });
        pictureGridview.setLayoutManager(gridLayoutManager);
        picAdpter.setClickListener(new PicGridAdapter.ItemClickListener() {
            @Override
            public void onItemClick(PicGridAdapter.ItemHolder itemHolder) {
                int position = itemHolder.getAdapterPosition();
                Intent sourceIntent = getIntent();
                if (sourceIntent != null) {
                    String s = sourceIntent.getAction();
                    if (s != null && s.equals("tietu")) {//选择贴图,不是一般的选择图片
                        Intent intent1 = new Intent();
                        intent1.putExtra("pic_path", currentPicPathList.get(position));
                        setResult(3, intent1);
                        ChosePictureActivity.this.finish();
                    } else {//正常的选择
                        Intent intent = new Intent(ChosePictureActivity.this, PtuActivity.class);
                        intent.putExtra("pic_path", currentPicPathList.get(position));
                        chosedPath = currentPicPathList.get(position);
                        startActivityForResult(intent, 0);
                    }
                }
            }
        });

        picAdpter.setLongClickListener(new PicGridAdapter.LongClickListener() {
            @Override
            public boolean onItemLongClick(PicGridAdapter.ItemHolder itemHolder) {

                View view = itemHolder.iv;
                int position = itemHolder.getAdapterPosition();
                Util.P.le("position " + position);
                return setPicPopWindow(view, position);
            }
        });

        pictureGridview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            AsyncImageLoader3 imageLoader = AsyncImageLoader3.getInstance();
            int lastScrollState = RecyclerView.SCROLL_STATE_IDLE;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int first, last;
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://停止时候
                        picAdpter.isScrollWidthoutTouch = false;
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
                        picAdpter.isScrollWidthoutTouch = false;
                        if (lastScrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                            first = gridLayoutManager.findFirstVisibleItemPosition();
                            last = gridLayoutManager.findLastVisibleItemPosition();
                            //显示出无指滑动后没有显示的
                            showAdjacentPic(first, last);
                        }
                        lastScrollState = RecyclerView.SCROLL_STATE_DRAGGING;
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://无触摸滑动
                        picAdpter.isScrollWidthoutTouch = true;
                        imageLoader.cancelLoad();//取消解析，提交的任务还没有执行的就不执行了
                        lastScrollState = RecyclerView.SCROLL_STATE_SETTLING;
                        break;
                }
            }

            private void showAdjacentPic(int first, int last) {
                for (int position = first; position <= last; position++) {
                    if (position < 0 || position >= currentPicPathList.size())
                        break;//一行中左边有数据，右边没有数据，各自position仍然当做存在
                    Util.P.le(TAG, "" + position);
                    View view = pictureGridview.findViewWithTag(currentPicPathList.get(position));
                    if (view != null) {
                        picAdpter.myBindViewHolder(pictureGridview.
                                getChildViewHolder(view), position);
                    }
                }
            }
        });

    }

    /**
     * @param view     被点击的view
     * @param position 注意是常用图片时position要先转换
     * @return
     */
    private boolean setPicPopWindow(View view, int position) {
        final PopupWindow popWindowFile = new PopupWindow(ChosePictureActivity.this);
        LinearLayout linearLayout = new LinearLayout(ChosePictureActivity.this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setDividerPadding(10);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(Util.getDrawable(R.drawable.divider_picture_opration));
        linearLayout.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT));
        linearLayout.setPadding(Util.dp2Px(2), Util.dp2Px(2), Util.dp2Px(2), Util.dp2Px(2));
        TextView frequentlyTextView = new TextView(ChosePictureActivity.this);
        frequentlyTextView.setGravity(Gravity.CENTER);
        frequentlyTextView.setWidth(view.getWidth() / 2);
        frequentlyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        final String path = currentPicPathList.get(position);
        if (usualyPicPathList.lastIndexOf(path) >= usuPicProcessor.getPreferStart()) {
            frequentlyTextView.setText("取消");
            final int finalPosition = position;
            frequentlyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usuPicProcessor.deletePreferPath(path, finalPosition);
                    dirInfoList.remove(0);
                    dirInfoList.add(0, "  " + "常用图片(" + usualyPicPathList.size() + ")");
                    if (currentPicPathList == usualyPicPathList)
                        picAdpter.notifyDataSetChanged();
                    popWindowFile.dismiss();
                }
            });
        } else {
            frequentlyTextView.setText("常用");
            frequentlyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindowFile.dismiss();
                    boolean change = usuPicProcessor.addPreferPath(path);
                    dirInfoList.remove(0);
                    dirInfoList.add(0, "  " + "常用图片(" + usualyPicPathList.size() + ")");
                    if (change && currentPicPathList == usualyPicPathList)
                        picAdpter.notifyDataSetChanged();
                }
            });
        }
        frequentlyTextView.setTextSize(22);
        frequentlyTextView.setTextColor(Util.getColor(R.color.text_deep_black));

        linearLayout.addView(frequentlyTextView);

        TextView deleteTextView = new TextView(ChosePictureActivity.this);

        deleteTextView.setGravity(Gravity.CENTER);
        deleteTextView.setWidth(view.getWidth() / 2);
        deleteTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        deleteTextView.setText("删除");
        deleteTextView.setTextSize(22);
        deleteTextView.setTextColor(Util.getColor(R.color.text_deep_black));

        final int finalPosition1 = position;
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindowFile.dismiss();
                deletePicture(currentPicPathList.get(finalPosition1));
            }
        });
        linearLayout.addView(deleteTextView);


        int[] popWH = new int[2];
        Util.getMesureWH(linearLayout, popWH);
        popWindowFile.setContentView(linearLayout);
        popWindowFile.setWidth(view.getWidth());
        popWindowFile.setHeight(popWH[1]);
        popWindowFile.setFocusable(true);
        popWindowFile.setBackgroundDrawable(Util.getDrawable(
                R.drawable.background_pic_operation));
        popWindowFile.showAsDropDown(view, 0, -view.getHeight());
        return true;
    }

    private void deletePicture(final String path) {
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
                        if (!usuPicProcessor.onDeleteOnePicInfile(path))//删除图片文件并更新目录列表信息
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
                        if (usualyPicPathList.contains(path)) {//包含才常用列表里面，删除常用列表中的信息
                            usuPicProcessor.onDeleteUsuallyPicture(path);
                        }
                        currentPicPathList.remove(path);
                        picAdpter.notifyDataSetChanged();
                        fileAdapter.notifyDataSetChanged();
                    }
                })
                .create();
        alertDialog.show();
    }


    /**
     * 加载Drawer的ListView数据
     */
    private void disposeDrawer() {

        dirPathList = usuPicProcessor.getDirPathList();
        List<String> dirRepresentPathList = usuPicProcessor.getDirRepresentPathList();
        dirInfoList = usuPicProcessor.getDirInfoList();

        pictureFileListView = (ListView) findViewById(R.id.drawer_picture_file_list);
        fileAdapter = new MyFileListAdapter(ChosePictureActivity.this,
                dirInfoList, dirRepresentPathList);
        pictureFileListView.setAdapter(fileAdapter);
        pictureFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentPicPathList = usualyPicPathList;
                    picAdpter.setList(usualyPicPathList);
                } else
                    getCurrentPicPathList(dirPathList.get(position));
                fileListDrawer.closeDrawer(GravityCompat.END);
                picAdpter.notifyDataSetChanged();
            }

            /**
             * 获取将要显示的图片的列表，并且将当前要显示的列表{@code currentPicPathList}和adpter内的数据指向获取的列表
             *
             */
            private void getCurrentPicPathList(String pictureFilePath) {
                picPathInFile.clear();
                FileTool.getOrderedPicListInFile(pictureFilePath, picPathInFile);
                currentPicPathList = picPathInFile;
                picAdpter.setList(picPathInFile);
            }
        });
    }

    @Override
    protected void onResume() {
        usuPicProcessor.getAllPicInfoAndRecent();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Util.P.le(TAG, "onActivityResult:开始处理其它activity的返回");
        if (resultCode == 0 && data != null) {
            String action = data.getAction();
            if (action != null && action.equals("finish")) {//结束
                setResult(0, new Intent(action));
                usuPicProcessor.addUsedPath(
                        data.getStringExtra("recent_use_pic"));
                finish();
                overridePendingTransition(0, R.anim.go_send_exit);
            } else if (action != null && action.equals("load_failed")) {//加载失败
                String failedPath = data.getStringExtra("failed_path");
                currentPicPathList.remove(failedPath);
                picAdpter.notifyDataSetChanged();
            } else {  //当前在常用图片下
                if (usuPicProcessor.isUsuPic(currentPicPathList)) {
                    editedPicPath = data.getStringExtra("new_path");
                    usuPicProcessor.addUsedPath(
                            data.getStringExtra("recent_use_pic"));
                    picAdpter.notifyDataSetChanged();
                } else {//不是常用的图片，是文件夹中的图片，则更新文件,
                    // 这里图片没有保存到当前文件夹下面
                    showFilePicUpdate(FileTool.getParentPath(chosedPath));
                    usuPicProcessor.addUsedPath(
                            data.getStringExtra("recent_use_pic"));//假如最近使用，不刷新视图
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 更新当前的形式的文件的图片的信息，有增删改时，即时显示出来
     */
    private void showFilePicUpdate(final String parentPath) {

        final List<String> newPaths = new ArrayList<String>();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj.equals("change")) {
                    currentPicPathList = newPaths;
                    picAdpter.setList(currentPicPathList);
                    picAdpter.notifyDataSetChanged();
                    fileAdapter.notifyDataSetChanged();
                }
                super.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileTool.getOrderedPicListInFile(parentPath, newPaths);
                Message msg = new Message();
                if (!newPaths.equals(currentPicPathList))
                    msg.obj = "change";
                else
                    msg.obj = "not";
                handler.sendMessage(msg);
            }
        }).start();

    }

    @Override
    protected void onStop() {
        //  AsyncImageLoader3.getInstance().stop();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        //AsyncImageLoader3.getInstance().reStart();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if (currentPicPathList == usualyPicPathList) {
            super.onBackPressed();
        } else {
            currentPicPathList = usualyPicPathList;
            picAdpter.setList(usualyPicPathList);
            pictureGridview.setAdapter(picAdpter);
        }
    }

    @Override
    protected void onDestroy() {
        AllData.lastScanTime = 0;
        super.onDestroy();
    }
}
