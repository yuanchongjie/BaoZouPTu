package a.baozouptu.ptu;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;

import a.baozouptu.CertainLeaveDialog;
import a.baozouptu.R;
import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.FileTool;
import a.baozouptu.base.util.Util;
import a.baozouptu.base.view.FirstUseDialog;
import a.baozouptu.chosePicture.ProcessUsuallyPicPath;
import a.baozouptu.ptu.control.MainFunctionFragment;
import a.baozouptu.ptu.cut.CutFragment;
import a.baozouptu.ptu.draw.DrawFragment;
import a.baozouptu.ptu.mat.MatFragment;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TietuStepData;
import a.baozouptu.ptu.saveAndShare.SaveSetDialogManager;
import a.baozouptu.ptu.saveAndShare.SaveSetInstance;
import a.baozouptu.ptu.text.FloatTextView;
import a.baozouptu.ptu.text.TextFragment;
import a.baozouptu.ptu.tietu.TietuFragment;
import a.baozouptu.ptu.view.PtuFrameLayout;
import a.baozouptu.ptu.view.PtuTopRelativeLayout;
import a.baozouptu.ptu.view.PtuView;

import static a.baozouptu.ptu.PtuUtil.EDIT_MAIN;
import static a.baozouptu.ptu.PtuUtil.EDIT_CUT;
import static a.baozouptu.ptu.PtuUtil.EDIT_TEXT;
import static a.baozouptu.ptu.PtuUtil.EDIT_TIETU;
import static a.baozouptu.ptu.PtuUtil.EDIT_DRAW;
import static a.baozouptu.ptu.PtuUtil.EDIT_MAT;


/**
 *
 */
public class PtuActivity extends AppCompatActivity implements MainFunctionFragment.Listen {
    private static final String TAG = "PtuActivity";
    static int CURRENT_EDIT_MODE = 0;

    /**
     * 主功能的fragment
     */
    MainFunctionFragment mainFrag;
    FragmentManager fm;
    private TextFragment textFrag;
    private TietuFragment tietuFrag;
    private DrawFragment drawFrag;
    private CutFragment cutFrag;
    private MatFragment matFrag;
    public PtuView ptuView;

    private PtuFrameLayout ptuFrame;
    /**
     * 子功能获取的bitmap的参数,0为获取图片相对原图片的左边距，1为获取图片相对原图片的上边距，
     * <p>2为获取图片的宽，3为获取图片的高度
     */
    private String picPath = null;
    private final int MAX_STEP = 10;
    private RepealRedoManager<StepData> repealRedoManager;

    private PtuTopRelativeLayout topRelativeLayout;

    /**
     * 整个PtuFragment的范围,初始时为0,要用与判断是否已经加载了图片
     */
    private Rect totalBound = new Rect(0, 0, 0, 0);
    private SaveSetDialogManager saveSetmanager;
    private ProgressDialog mProgressDialog;

    private String endType = "normal";
    private RepealRedoListener repealRedoListener;
    private SaveSetInstance saveSetInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveSetInstance = new SaveSetInstance();
        setContentView(R.layout.activity_ptu);
        setTitle("");
        Log.e(TAG, "使用最新图片返回图片-1");
        initView();
        Log.e(TAG, "使用最新图片返回图片-0");
        initToolbar();
        //如果加载数据不成功，（图片加载失败）返回，
        if (!initData()) return;
        initFragment();
        setViewContent();
        test();
        Util.P.le(TAG, "onCreate()");
    }


    private void initFragment() {
        mainFrag = new MainFunctionFragment();
        fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_function, mainFrag)
                .commit();
        repealRedoListener = new RepealRedoListener() {
            @Override
            public void canRedo(boolean canRedo) {
                Log.e(TAG, "进入监听器方法");
                topRelativeLayout.setRedoBtnColor(canRedo);
            }

            @Override
            public void canRepeal(boolean canRepeal) {
                topRelativeLayout.setRepealBtnColor(canRepeal);
            }
        };
    }

    @Override
    public void switchFragment(int function) {
        ptuView.resetShow();
        if (function == EDIT_MAIN) {
            mainFrag.eraseChosenColor();
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                            R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                    .replace(R.id.fragment_function, mainFrag)
                    .commit();
        } else {
            onToSecondFunction();
            switch (function) {
                case EDIT_CUT:
                    if (cutFrag == null) {
                        cutFrag = new CutFragment();
                        cutFrag.setPtuView(ptuView);
                    }
                    fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                                    R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                            .replace(R.id.fragment_function, cutFrag)
                            .commit();
                    FrameLayout.LayoutParams cutFloatParams =
                            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    cutFloatParams.setMargins(0, 0, 0, 0);
                    ptuFrame.addView(
                            cutFrag.createCutView(this, totalBound, ptuView.getSourceBm())
                            , cutFloatParams);

                    CURRENT_EDIT_MODE = EDIT_CUT;
                    break;
                case EDIT_TEXT:
                    if (textFrag == null) {
                        textFrag = new TextFragment();
                        textFrag.setPtuView(ptuView);
                        textFrag.setRepealRedoListener(repealRedoListener);
                    }
                    fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                                    R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                            .replace(R.id.fragment_function, textFrag)
                            .commit();
                    CURRENT_EDIT_MODE = EDIT_TEXT;
                    textFrag.addRubberView(this, ptuFrame);
                    FloatTextView floatTextView = ptuFrame.initAddTextFloat(ptuView.getPicBound());
                    textFrag.setFloatView(floatTextView);
                    break;
                case EDIT_TIETU:
                    if (tietuFrag == null) {
                        tietuFrag = new TietuFragment();
                        tietuFrag.setPtuView(ptuView);
                    }
                    tietuFrag.setTietuLayout(ptuFrame.initAddImageFloat(totalBound));
                    fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                                    R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                            .replace(R.id.fragment_function, tietuFrag)
                            .commit();
                    CURRENT_EDIT_MODE = EDIT_TIETU;
                    break;
                case EDIT_DRAW:
                    if (drawFrag == null) {
                        drawFrag = new DrawFragment();
                        drawFrag.setRepealRedoListener(repealRedoListener);
                    }
                    fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                                    R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                            .replace(R.id.fragment_function, drawFrag)
                            .commit();
                    FrameLayout.LayoutParams drawFloatParams =
                            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    drawFloatParams.setMargins(ptuView.getDstRect().left, ptuView.getDstRect().top, ptuView.getDstRect().left, ptuView.getDstRect().top);
                    ptuFrame.addView(
                            drawFrag.createDrawView(this, totalBound, ptuView)
                            , drawFloatParams);

                    CURRENT_EDIT_MODE = EDIT_DRAW;
                    break;
                case EDIT_MAT:
                    if (matFrag == null) {
                        matFrag = new MatFragment();
                    }

                    fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                                    R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                            .replace(R.id.fragment_function, matFrag)
                            .commit();

                    //设置布局
                    FrameLayout.LayoutParams floatParams =
                            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    ptuFrame.addView(matFrag.createMatView(PtuActivity.this, totalBound, ptuView.getSourceBm()), floatParams);
                    CURRENT_EDIT_MODE = EDIT_MAT;
                    break;
            }
        }
        Util.P.le(TAG, "switchFragment完成");
    }

    @TargetApi(19)
    private void initToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(Util.getColor(this, R.color.base_toolbar_background));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    /**
     * 如果加载数据不成功，（图片加载失败）返回false，
     *
     * @return 是否成功加载图片到PtuView
     */
    private boolean initData() {
        repealRedoManager = new RepealRedoManager<>(MAX_STEP);
        //如果是从其它应用过来需要编辑图片的
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            Util.P.le(uri.getPath());
            picPath = FileTool.getImagePathFromUri(this, uri);
        } else if (intent.getAction() != null && intent.getAction().equals("notify_latest")) {
            return useLatestPic();
        } else {
            picPath = intent.getStringExtra("pic_path");
        }
        if (picPath == null || !new File(picPath).exists()) {
            showFailDialog();
            return false;
        }
        Handler mhandler = new Handler();
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                processSecondaryOperation();
            }
        }, 5000);
        return true;
    }

    private void processSecondaryOperation() {
    }

    private boolean useLatestPic() {
        picPath = null;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj.equals("latest_pic")) {
                    picPath = msg.getData().getString("pic_path");
                    if (picPath == null || !new File(picPath).exists()) {
                        showFailDialog();
                    }
                    Log.e(TAG, "使用最新图片返回图片成功" + " 路径" + picPath);
                    progressDialog.dismiss();
                    if (totalBound.width() > 0)
                        ptuView.replaceSourceBm(BitmapTool.getLosslessBitmap(picPath));
                }
            }
        };
        ProcessUsuallyPicPath picPathProcess = new ProcessUsuallyPicPath(this, handler);
        picPathProcess.prepareLatestPic();
        return true;
    }

    private void showFailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PtuActivity.this);
        builder.setTitle("图片加载失败，图片不存在，或已失效已失效");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent resultIntent = new Intent();
                resultIntent.setAction("load_failed");
                resultIntent.putExtra("failed_path", picPath);
                setResult(0, resultIntent);
                finish();
            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void test() {

        if (getIntent().getStringExtra("test") == null) return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switchFragment(EDIT_TEXT);
            }
        }, 500);
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saveSet();
            }
        }, 1000);
        */
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

    /**
     * 初始化视图,尤其顶部的视图条
     */
    private void initView() {
        if (AllData.screenHeight == 0) getScreenWidth();
        ptuFrame = (PtuFrameLayout) findViewById(R.id.ptu_frame);
        ptuView = (PtuView) findViewById(R.id.ptu_view);

        int top_btn_width = AllData.screenWidth / 10;
        int smallDividerWidth = Util.dp2Px(16);
        int bigDividerWidth = (int) ((AllData.screenWidth - smallDividerWidth * 2 - top_btn_width * 5) * 1f / 4);

        topRelativeLayout = (PtuTopRelativeLayout) findViewById(R.id.ptu_toolbar_relative);

        //初始化中间的按钮
        ImageButton[] btns = topRelativeLayout.createCenterView(top_btn_width, bigDividerWidth);
        ImageButton repealBtn = btns[0];
        ImageButton redoBtn = btns[1];
        ImageButton goSendBtn = btns[2];
        repealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_EDIT_MODE == EDIT_MAIN)
                    bigRepeal();
                else smallRepeal();
            }
        });
        redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_EDIT_MODE == EDIT_MAIN)
                    bigRedo();
                else smallRedo();
            }
        });
        goSendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!AllData.appConfig.hasReadGoSend()) {
                            FirstUseDialog firstUseDialog = new FirstUseDialog(PtuActivity.this);
                            firstUseDialog.createDialog("快捷发送", "页面将会关闭，" +
                                    "点击通讯软件的发送图片即可快捷发送", new FirstUseDialog.ActionListener() {
                                @Override
                                public void onSure() {
                                    AllData.appConfig.wiriteConfig_GoSend(true);
                                }
                            });
                        } else goSend();

                    }
                }
        );

        ImageButton cancelBtn = topRelativeLayout.createCancel(top_btn_width, smallDividerWidth);
        ImageButton sureBtn = topRelativeLayout.createSure(top_btn_width, smallDividerWidth);
        View returnLayout = topRelativeLayout.createReturn(top_btn_width);
        View saveSetLayout = topRelativeLayout.createSaveSet();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        returnLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        certainLeave();
                    }
                }
        );
        saveSetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.DoubleClick.isDoubleClick())
                    saveSet();
            }
        });
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_EDIT_MODE != EDIT_MAIN)
                    sure();
            }
        });
        topRelativeLayout.addReturn();
        topRelativeLayout.addSaveSet();
    }

    private void certainLeave() {
        if (repealRedoManager.hasChangePic()) {
            CertainLeaveDialog certainLeaveDialog = new CertainLeaveDialog(PtuActivity.this);
            certainLeaveDialog.createDialog(null, null, new CertainLeaveDialog.ActionListener() {
                @Override
                public void onSure() {
                    setReturnResultAndFinish("leave", new Bundle(), false);
                }
            });
        } else
            setReturnResultAndFinish("leave", new Bundle(), false);
    }


    private void goSend() {

        if (CURRENT_EDIT_MODE != EDIT_MAIN)
            sure();
        mProgressDialog = ProgressDialog.show(PtuActivity.this, "请稍后",
                "正在处理...", true);
        saveResultBm(1);
        mProgressDialog.dismiss();
        setReturnResultAndFinish("finish", new Bundle(), true);
    }

    //另外开启，加载一些其他的数据，不阻塞UI线程
    @Override
    protected void onStart() {
        Util.P.le(TAG, "onStart_1");
        if (saveSetmanager == null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveSetmanager = saveSetInstance.getInstance(PtuActivity.this);
                }
            }).start();
        super.onStart();
        Util.P.le(TAG, "onStart_2");
    }

    private void setViewContent() {
        ptuFrame.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        totalBound.set(0, 0, ptuFrame.getWidth(), ptuFrame.getHeight());
                        ptuView.setTotalBound(totalBound);
                        ptuView.setBitmapAndInit(picPath, totalBound);
                        repealRedoManager.setBaseBm(ptuView.getSourceBm()
                                .copy(Bitmap.Config.ARGB_8888, true));
                        ptuFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        switchFragment(EDIT_MAIN);
                        Util.P.le(TAG, "初始化加载PtuView完成");
                    }
                }
        );
    }


    private void saveSet() {
        if (saveSetmanager == null)
            saveSetmanager = saveSetInstance.getInstance(this);
        saveSetmanager.init(BitmapTool.getSize(
                ptuView.getSourceBm()));
        saveSetmanager.createDialog();
        saveSetmanager.setClickListener(new SaveSetDialogManager.clickListenerInterface() {

            /**
             * @param saveRatio 注意先完成自己的操作，在调用此方法。这里会结束activity
             */
            @Override
            public void mSure(float saveRatio) {
                String newPath = saveResultBm(saveRatio);
                Bundle bundle = new Bundle();
                bundle.putString("new_path", newPath);
                setReturnResultAndFinish("save_and_leave", bundle, false);
            }

            @Override
            public void mCancel() {
            }

            @Override
            public String onShareItemClick(float saveRatio) {
                endType = "share";
                String savePath = FileTool.getNewPictureFileDefult(picPath);
                if (savePath == null) return null;
                String res = BitmapTool.saveBitmap(PtuActivity.this, ptuView.getSourceBm(), savePath, true);
                if (!res.equals("success")) return null;
                repealRedoManager.setHasSavePic(true);//分享过后图片就相当于已经保存了
                return savePath;
            }
        });
    }

    /**
     * 会完成点击sure之后的所有工作
     * 回到主功能界面时,如果是从子功能回来，
     * （1）会添加子功能的view级参数到撤销重做list中。
     * （2）移除浮动图，获取浮动图的图片显示到putview上面
     * 注意sure会使用延时，不要在它后面字节写函数
     */
    private void sure() {
        ProgressDialog mProgressDialog = new ProgressDialog(PtuActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.show();

        if (CURRENT_EDIT_MODE == EDIT_CUT) {
            Bitmap newSourceBm = cutFrag.getResultBm(1);
            StepData csd = cutFrag.getResultData(1);
            cutFrag.releaseResource();
            ptuView.replaceSourceBm(newSourceBm);
            asynchronousSaveStepBm(newSourceBm, csd);
            switchFragment(EDIT_MAIN);
            afterSure(csd);
        }
        //添加文字
        else if (CURRENT_EDIT_MODE == EDIT_TEXT) {
            StepData tsd = textFrag.getResultData(1);
            if (tsd == null) {//有些情况下会返回空
                cancel();
                return;
            }
            Bitmap source = textFrag.getResultBm();
            textFrag.addBigStep(source, tsd);
            asynchronousSaveStepBm(source, tsd);
            //释放，删除等部分
            textFrag.releaseResource();
            afterSure(tsd);
            Util.P.le(TAG, "添加文字完成");
        }
        //贴图
        else if (CURRENT_EDIT_MODE == EDIT_TIETU) {
            TietuStepData ttsd = (TietuStepData) tietuFrag.getResultData(1);
            ttsd.EDIT_MODE = EDIT_TIETU;
            tietuFrag.addBigStep(ttsd);
            //释放，删除等部分
            tietuFrag.releaseResource();
            Util.P.le(TAG, "释放资源成功");
            afterSure(ttsd);
        } //绘图
        else if (CURRENT_EDIT_MODE == EDIT_DRAW) {
            StepData dsd = drawFrag.getResultData(1);
            dsd.EDIT_MODE = EDIT_DRAW;
            drawFrag.addBigStep(dsd);
            //释放，删除等部分
            drawFrag.releaseResource();
            Util.P.le(TAG, "释放资源成功");
            afterSure(dsd);
        }
        mProgressDialog.dismiss();
    }

    /**
     * 取消，点击取消按钮，back按键，点击sure按钮保存之后都会有它
     */
    private void cancel() {
        checkRepealRedo();
        int count = ptuFrame.getChildCount();
        for (int i = count - 1; i > 0; i--) {
//            移除除了PtuView以外的视图
            ptuFrame.removeViewAt(i);
        }
        if (ptuView.getVisibility() == View.INVISIBLE) {
            ptuView.setVisibility(View.VISIBLE);
        }
        topRelativeLayout.removeCancel();
        topRelativeLayout.removeSure();
        topRelativeLayout.addReturn();
        topRelativeLayout.addSaveSet();
        CURRENT_EDIT_MODE = EDIT_MAIN;
        switchFragment(EDIT_MAIN);
        Util.P.le(TAG, "主界面转换成功");
    }

    private void smallRepeal() {
        switch (CURRENT_EDIT_MODE) {
            //主界面的重做
            case EDIT_MAIN:
                break;
            case EDIT_TEXT:
                textFrag.smallRepeal();
                break;
            case EDIT_TIETU:
                tietuFrag.smallRepeal();
                break;
            case EDIT_DRAW:
                drawFrag.smallRepeal();
                break;
            case EDIT_MAT:
                matFrag.smallRepeal();
                break;
        }
    }

    private void smallRedo() {
        switch (CURRENT_EDIT_MODE) {
            //主界面的重做
            case EDIT_MAIN:
                break;
            case EDIT_TEXT:
                textFrag.smallRedo();
                break;
            case EDIT_TIETU:
                tietuFrag.smallRedo();
                break;
            case EDIT_DRAW:
                drawFrag.smallRedo();
                break;
            case EDIT_MAT:
                matFrag.smallRedo();
                break;
        }

    }

    private void bigRepeal() {
        Util.P.le(TAG, "开始执行撤销");
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();

        if (repealRedoManager.canRepeal()) {
            repealRedoManager.repealPrepare();
            ptuView.releaseResource();
            Bitmap newSourceBm = repealRedoManager.getBaseBitmap().
                    copy(Bitmap.Config.ARGB_8888, true);
            ptuView.replaceSourceBm(newSourceBm);
            Util.P.le(TAG, "撤销的替换基图成功");
            int index = repealRedoManager.getCurrentIndex();
            for (int i = 0; i <= index; i++) {
                StepData sd = repealRedoManager.getStepdata(i);
                addBigStep(sd);
            }
            Util.P.le(TAG, "撤销的多步添加图片完成");
            ptuView.resetShow();
        }

        progressDialog.dismiss();
        checkRepealRedo();
    }

    public void bigRedo() {
        if (repealRedoManager.canRedo()) {
            StepData sd = repealRedoManager.redo();
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();
            addBigStep(sd);
            progressDialog.dismiss();
            checkRepealRedo();
        }
        Util.P.le(TAG, "重做成功");
    }

    /*************************************************************
     * repealRedo部分
     *************************************************/
    private void addBigStep(StepData sd) {
        switch (sd.EDIT_MODE) {
            case EDIT_CUT:
                cutFrag.addBigStep(sd);
                break;
            case EDIT_TEXT:
                textFrag.addBigStep(sd);
                break;
            case EDIT_TIETU:
                tietuFrag.addBigStep(sd);
                break;
            case EDIT_DRAW:
                drawFrag.addBigStep(sd);
                break;
            case EDIT_MAT:
                matFrag.addBigStep(sd);
                break;
        }

    }

    private void checkRepealRedo() {
        topRelativeLayout.setRedoBtnColor(repealRedoManager.canRedo());
        topRelativeLayout.setRepealBtnColor(repealRedoManager.canRepeal());
    }

    public void setRedoBtnColor(boolean canRedo) {
        topRelativeLayout.setRedoBtnColor(canRedo);
    }

    /**
     * @param canRepeal 能否撤销
     */
    public void setRepealBtnColor(boolean canRepeal) {
        topRelativeLayout.setRepealBtnColor(canRepeal);
    }

    /**
     * 将步骤数据提交，如果步数超限，则让BaseBitmap前进一步
     */
    private void afterSure(StepData sd) {
        StepData resd = repealRedoManager.commit(sd);
        if (resd != null)//超限了，把最开始的的一步添加到基图上
            addBigStep(resd);
        Util.P.le(TAG, "撤销重做资源处理成功");
        cancel();
    }

    /**
     * 异步保存
     * <p>注意保存之后图片加会释放，
     * <p>释放之后不能加其他操作了
     *
     * @param bm 图片
     * @param sd 数据
     */
    private void asynchronousSaveStepBm(final Bitmap bm, final StepData sd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Util.P.le("开始异步保存图片");
                String picPath = FileTool.createTempPicPath(PtuActivity.this);
                BitmapTool.saveBitmap(PtuActivity.this, bm, picPath, false);
                sd.picPath = picPath;
                Util.P.le("异步保存图片完成");
            }
        }).start();

    }

    /**
     * 到二级功能必定会做的动作
     */
    private void onToSecondFunction() {
        topRelativeLayout.removeReturn();
        topRelativeLayout.removeSaveSet();
        topRelativeLayout.addCancel();
        topRelativeLayout.addSure();
        topRelativeLayout.setRedoBtnColor(false);
        topRelativeLayout.setRepealBtnColor(false);
    }

    /**
     * @return 返回保存的路径
     */
    @Nullable
    private String saveResultBm(final float saveRatio) {
        String result = null;
        Bitmap bitmap = ptuView.getFinalPicture(saveRatio);
        String savePath = FileTool.getNewPictureFileDefult(picPath);
        if (savePath == null) {
            Toast.makeText(this, "创建SD卡文件失败", Toast.LENGTH_LONG).show();
            return null;
        }
        result = BitmapTool.saveBitmap(PtuActivity.this, bitmap, savePath);
        if (!result.equals("success")) {
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
        return savePath;
    }

    public void setReturnResultAndFinish(String mAction, Bundle bundle, boolean isGoSend) {
        Intent resultIntent = new Intent(mAction);
        bundle.putString("recent_use_pic", picPath);
        resultIntent.putExtras(bundle);
        setResult(0, resultIntent);
        finish();
        if (isGoSend) {
            overridePendingTransition(0, R.anim.go_send_exit);
        }
    }

    @Override
    protected void onStop() {
        if (endType.equals("share")) ;
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (CURRENT_EDIT_MODE != EDIT_MAIN) {
            if (CURRENT_EDIT_MODE == EDIT_TEXT) {
                if (textFrag.onBackPressed())
                    cancel();
                return;
            }
            cancel();
        } else {
            certainLeave();
        }
    }

    @Override
    protected void onDestroy() {
        if (ptuView != null)
            ptuView.releaseResource();
        if (repealRedoManager != null)
            repealRedoManager.clear(this);
        super.onDestroy();
    }

    public PtuFrameLayout getPtuFrame() {
        return ptuFrame;
    }
}
