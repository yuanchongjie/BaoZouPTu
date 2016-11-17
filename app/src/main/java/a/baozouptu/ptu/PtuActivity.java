package a.baozouptu.ptu;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.Iterator;

import a.baozouptu.R;
import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.FileTool;
import a.baozouptu.base.util.Util;
import a.baozouptu.chosePicture.ProcessUsuallyPicPath;
import a.baozouptu.ptu.control.MainFunctionFragment;
import a.baozouptu.ptu.cut.CutFragment;
import a.baozouptu.ptu.draw.DrawFragment;
import a.baozouptu.ptu.mat.MatFragment;
import a.baozouptu.ptu.repealRedo.CutStepData;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TextStepData;
import a.baozouptu.ptu.repealRedo.TietuStepData;
import a.baozouptu.ptu.saveAndShare.SaveSetDialogManager;
import a.baozouptu.ptu.text.FloatTextView;
import a.baozouptu.ptu.text.TextFragment;
import a.baozouptu.ptu.tietu.FloatImageView1;
import a.baozouptu.ptu.tietu.TietuFragment;
import a.baozouptu.ptu.tietu.TietuSizeControler;
import a.baozouptu.ptu.view.PtuFrameLayout;
import a.baozouptu.ptu.view.PtuTopRelativeLayout;
import a.baozouptu.ptu.view.PtuView;

/**
 *
 */
public class PtuActivity extends AppCompatActivity implements MainFunctionFragment.Listen {
    private static final String TAG = "PtuActivity";
    static int CURRENT_EDIT_MODE = 0;
    public static final int EDIT_MAIN = 0;
    public static final int EDIT_CUT = 1;
    /**
     * 包含三个元素
     * sd.floatTextView = floatTextView;
     * sd.innerRect = innerRect;
     * sd.boundRectInPic = boundRectInPic;
     */
    public static final int EDIT_TEXT = 2;
    public static final int EDIT_TIETU = 3;
    public static final int EDIT_DRAW = 4;
    public static final int EDIT_MAT = 5;

    /**
     * 主功能的fragment
     */
    MainFunctionFragment mainFrag;
    FragmentTransaction ft;
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
    private FloatTextView floatTextView;
    private final int MAX_STEP = 10;
    private RepealRedoManager<StepData> repealRedoManager;

    private PtuTopRelativeLayout topRelativeLayout;
    private ImageButton cancelBtn;
    private ImageButton sureBtn;
    private ImageButton goSendBtn;

    /**
     * 整个PtuFragment的范围,初始时为0,要用与判断是否已经加载了图片
     */
    private Rect totalBound = new Rect(0, 0, 0, 0);
    private SaveSetDialogManager saveSetmanager;
    private ProgressDialog mProgressDialog;
    private Handler mhandler;

    private String endType = "normal";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Util.P.le(TAG, "进入P图Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptu);
        Util.P.le(TAG, "进入P图Activity");
        setTitle("");
        initView();
        Util.P.le(TAG, "完成initView");
        initToolbar();
        Util.P.le(TAG, "完成initToolbar");
        //如果加载数据不成功，（图片加载失败）返回，
        if (!initData()) return;
        Util.P.le(TAG, "完成initData");
        initFragment();
        Util.P.le(TAG, "完成initFragment");
        setViewContent();
        Util.P.le(TAG, "完成setViewContent");
        test();
        Util.P.le(TAG, "完成test");
        Util.P.le(TAG, "ononCreate()_2");
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
     * 如果加载数据不成功，（图片加载失败）返回false，
     *
     * @return 是否成功加载图片到PtuView
     */
    private boolean initData() {
        repealRedoManager = new RepealRedoManager(MAX_STEP);
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
        mhandler = new Handler();
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
        builder.setTitle("图片加载失败");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PtuActivity.this.finish();
            }
        });
        builder.create().show();
    }

    private void test() {

        if (getIntent().getStringExtra("test") == null) return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switchFragment(EDIT_CUT);
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

    private void initView() {
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
        goSendBtn = btns[2];
        repealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigRepeal();
            }
        });
        redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigRedo();
            }
        });
        goSendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goSend();
                    }
                }
        );

        cancelBtn = topRelativeLayout.createCancel(top_btn_width, smallDividerWidth);
        sureBtn = topRelativeLayout.createSure(top_btn_width, smallDividerWidth);
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
                        PtuActivity.this.finish();
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
                    saveSetmanager = new SaveSetDialogManager(PtuActivity.this);
                }
            }).start();
        super.onStart();
        Util.P.le(TAG, "onStart_2");
    }

    private void saveSet() {
        if (saveSetmanager == null)
            saveSetmanager = new SaveSetDialogManager(this);
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
                setReturnResultAndFinish("common", bundle, false);
            }

            @Override
            public void mCancel() {
            }

            @Override
            public String onShareItemClick(float saveRatio) {
                endType = "share";
                return picPath;
            }
        });
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


    /*************************************************************
     * repealRedo部分
     *************************************************/

    private void addStep(Bitmap bm, StepData sd) {
        switch (sd.EDIT_MODE) {
            case EDIT_TEXT:
                TextFragment.addBigStep(bm, sd);
                break;
            case EDIT_TIETU:
                FloatImageView1.addBigStep(bm, sd);
                break;
            case EDIT_DRAW:
                DrawFragment.addBigStep(bm, sd);
                break;
        }

    }

    private void bigRepeal() {
        Util.P.le(TAG, "执行开始撤销");
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        switch (CURRENT_EDIT_MODE) {
            case EDIT_MAIN:
                if (repealRedoManager.canRepeal()) {
                    repealRedoManager.repeal();
                    ptuView.releaseResource();
                    Bitmap newSourceBm = repealRedoManager.getBaseBitmap().
                            copy(Bitmap.Config.ARGB_8888, true);
                    String newPath = FileTool.getNewPictureFile(picPath);
                    BitmapTool.saveBitmap(this, newSourceBm, newPath);
                    picPath = newPath;
                    ptuView.replaceSourceBm(newSourceBm);
                    Util.P.le(TAG, "重做的替换基图成功");
                    int index = repealRedoManager.getCurrentIndex();
                    for (int i = 0; i <= index; i++) {
                        StepData sd = repealRedoManager.getStepdata(i);
                        addStep(newSourceBm, sd);
                    }
                    Util.P.le(TAG, "重做的多步添加图片完成");
                    ptuView.resetShow();
                }
                break;
            case EDIT_DRAW:
                drawFrag.repeal();
                break;
            case EDIT_TIETU:
                tietuFrag.repeal();
                break;
            case EDIT_MAT:
                matFrag.repeal();
                break;
        }
        progressDialog.dismiss();
        checkRepealRedo();
    }

    public void bigRedo() {
        if (repealRedoManager.canRedo()) {
            StepData sd = repealRedoManager.redo();
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();
            switch (sd.EDIT_MODE) {
                case EDIT_MAIN:
                case EDIT_CUT:
                    cutFrag.redo(sd);
                    break;
                case EDIT_TEXT:
                    Bitmap sourceBm = ptuView.getSourceBm();
                    addStep(sourceBm, repealRedoManager.redo());
                    ptuView.resetShow();
                    checkRepealRedo();
                    break;
                case EDIT_TIETU:
                    tietuFrag.redo(sd);
                    break;
                case EDIT_DRAW:
                    drawFrag.redo(sd);
                    break;
                case EDIT_MAT:
                    matFrag.redo(sd);
                    break;

            }
            progressDialog.dismiss();
            checkRepealRedo();
        }
        Util.P.le(TAG, "重做成功");
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

    /**************************************************************************************************************/

    //
    //观察Fragment的生命周期，
    // （1）是否Activity创建，不管有没有添加，都会执行
    //(2)执行不同的FragmentTransetion事务其反应如何
    //
    private void initFragment() {
        mainFrag = new MainFunctionFragment();
        fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_function, mainFrag)
                .commit();
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
                    }
                    fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                                    R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                            .replace(R.id.fragment_function, textFrag)
                            .commit();
                    CURRENT_EDIT_MODE = EDIT_TEXT;
                    floatTextView = ptuFrame.initAddTextFloat(ptuView.getPicBound());
                    textFrag.setFloatView(floatTextView);
               /* //让文本框一开始就获得输入法
                floatTextView.setFocusable(true);
                floatTextView.requestFocus();
                onFocusChange(floatTextView.isFocused());*/
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

                    CURRENT_EDIT_MODE = EDIT_TIETU;
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

    /**
     * 到二级功能必定会做的动作
     */
    private void onToSecondFunction() {
        topRelativeLayout.removeReturn();
        topRelativeLayout.removeSaveSet();
        topRelativeLayout.addCancel();
        topRelativeLayout.addSure();
    }

    @Override
    public void onBackPressed() {
        if (CURRENT_EDIT_MODE != EDIT_MAIN) {
            cancel();
        } else {
            super.onBackPressed();
        }
    }


    /**
     * 会完成点击sure之后的所有工作
     * 回到主功能界面时,如果是从子功能回来，
     * （1）会添加子功能的view级参数到撤销重做list中。
     * （2）移除浮动图，获取浮动图的图片显示到putview上面
     * 注意sure会使用延时，不要在它后面字节写函数
     */
    private void sure() {
       /* mProgressDialog = new ProgressDialog(PtuActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.show();*/

        if (CURRENT_EDIT_MODE == EDIT_CUT) {
            Bitmap newSourceBm = cutFrag.getResultBm(1);
            StepData csd=cutFrag.getResultData(1);
            String picPath=FileTool.createTempPicPath(this);
            BitmapTool.saveBitmap(this,newSourceBm,picPath);
            csd.picPath=picPath;
            cutFrag.releaseResource();
            ptuFrame.removeViewAt(1);
            ptuView.replaceSourceBm(newSourceBm);
            switchFragment(EDIT_MAIN);
            afterSure(csd);
        }//添加文字
        else if (CURRENT_EDIT_MODE == EDIT_TEXT) {
            TextStepData tsd = textFrag.getResultData(ptuView);
            if (tsd == null) {//有些情况下会返回空
                Util.T(PtuActivity.this, "操作失败，获取到的图像为空");
                cancel();
                return;
            }
            Bitmap source = textFrag.getResultBm();
            ptuView.addBitmap(source,
                    tsd.boundRectInPic,
                    tsd.rotateAngle);

            //释放，删除等部分
            textFrag.releaseResource();
            Util.P.le(TAG, "释放资源完成");
            afterSure(tsd);
            Util.P.le(TAG, "添加文字完成");
        } //贴图
        else if (CURRENT_EDIT_MODE == EDIT_TIETU) {
            TietuStepData ttsd = (TietuStepData) tietuFrag.getResultData(1);
            ttsd.EDIT_MODE = EDIT_TIETU;
            Iterator<StepData> iterator = ttsd.iterator();
            while (iterator.hasNext()) {
                StepData sd = iterator.next();
                ptuView.addBitmap(TietuSizeControler.getSrcBitmap(PtuActivity.this, sd.picPath),
                        sd.boundRectInPic, sd.rotateAngle);
            }
            //释放，删除等部分
            tietuFrag.releaseResource();
            ptuFrame.removeViewAt(1);
            Util.P.le(TAG, "释放资源成功");
            afterSure(ttsd);
        } //绘图
        else if (CURRENT_EDIT_MODE == EDIT_DRAW) {
            StepData dsd = drawFrag.getResultData(1);
            afterSure(dsd);
        }
        //   mProgressDialog.dismiss();
    }

    /**
     * 将步骤数据提交，如果步数超限，则让BaseBitmap前进一步
     */
    private void afterSure(StepData sd) {
        StepData resd = repealRedoManager.commit(sd);
        if (resd != null)//超限了，把最开始的的一步添加到基图上
            addStep(repealRedoManager.getBaseBitmap(), resd);
        checkRepealRedo();
        Util.P.le(TAG, "撤销重做资源处理成功");
        cancel();
    }

    /**
     * 取消，点击取消按钮，back按键，点击sure按钮保存之后都会有它
     */
    private void cancel() {
        if (ptuFrame.getChildCount() > 1) {
            ptuFrame.removeViewAt(1);
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

    /**
     * @return 返回保存的路径
     */
    private String saveResultBm(final float saveRatio) {
        String result = null;
        Bitmap bitmap = ptuView.getFinalPicture(saveRatio);
        String newPath = FileTool.getNewPictureFile(picPath);
        result = BitmapTool.saveBitmap(PtuActivity.this, bitmap, newPath);
        Util.P.le(TAG, "保存图片结果:" + result);
        return newPath;
    }

    public void setReturnResultAndFinish(String mAction, Bundle bundle, boolean isGoSend) {
        Intent resultIntent = new Intent(mAction);
        resultIntent.putExtras(bundle);
        setResult(0, resultIntent);
        finish();
        if (isGoSend) {
            overridePendingTransition(0, R.anim.go_send_exit);
        }
    }


    @Override
    protected void onStop() {
        if (endType == "share")
            finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(ptuView!=null)
            ptuView.releaseResource();
        if (repealRedoManager != null)
            repealRedoManager.clear(this);
        super.onDestroy();
    }
}
