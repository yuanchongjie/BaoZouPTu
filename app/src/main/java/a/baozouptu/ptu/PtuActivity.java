package a.baozouptu.ptu;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;

import a.baozouptu.R;
import a.baozouptu.base.dataAndLogic.AllDate;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.FileTool;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.addtext.AddTextFragment;
import a.baozouptu.ptu.addtext.FloatTextView;
import a.baozouptu.ptu.control.MainFunctionFragment;
import a.baozouptu.ptu.cut.CutFragment;
import a.baozouptu.ptu.draw.DrawFragment;
import a.baozouptu.ptu.mat.MatFragment;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TextStepData;
import a.baozouptu.ptu.repealRedo.TietuStepData;
import a.baozouptu.ptu.tietu.FloatImageView;
import a.baozouptu.ptu.tietu.TietuFragment;
import a.baozouptu.ptu.view.PtuFrameLayout;
import a.baozouptu.ptu.view.PtuTopRealtiveLayout;
import a.baozouptu.ptu.view.PtuView;

public class PtuActivity extends AppCompatActivity implements MainFunctionFragment.Listen {
    public static final String TAG = "PtuActivity";
    static int CURRENT_EDIT_MODE = 0;
    static final int EDIT_NO = 0;
    static final int EDIT_CUT = 1;
    /**
     * 包含三个元素
     * sd.floatTextView = floatTextView;
     * sd.innerRect = innerRect;
     * sd.boundRectInPic = boundRectInPic;
     */
    static final int EDIT_TEXT = 2;
    static final int EDIT_TIETU = 3;
    static final int EDIT_DRAW = 4;
    static final int EDTI_MAT = 5;

    /**
     * 主功能的fragment
     */
    MainFunctionFragment mainFrag;
    FragmentTransaction ft;
    FragmentManager fm;
    private AddTextFragment textFrag;
    private TietuFragment tietuFrag;
    private DrawFragment drawFrag;
    private CutFragment cutFrag;
    private MatFragment matFrag;
    private PtuView ptuView;
    private PtuFrameLayout ptuFrame;
    /**
     * 子功能获取的bitmap的参数,0为获取图片相对原图片的左边距，1为获取图片相对原图片的上边距，
     * <p>2为获取图片的宽，3为获取图片的高度
     */
    private String picPath = null;
    private FloatTextView floatTextView;
    private final int MAX_STEP = 10;
    private RepealRedoManager repealRedoManager = new RepealRedoManager(MAX_STEP);
    private float finalRatio = 1;
    private Intent resultIntent = new Intent();

    boolean hasChanged = false;

    private PtuTopRealtiveLayout topRelativeLayout;
    private ImageButton cancelBtn;
    private ImageButton sureBtn;
    private ImageButton goSendBtn;
    private final String NAME_TIE_TU = "tietu";
    private final String NAME_TEXT = "text";
    private final String NAME_CUT = "cut";
    private final String NAME_MAT = "mat";
    private final String NAME_DRAW = "draw";
    private final String NAME_MAIN = "main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Util.P.le("进入P图Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ptu_toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        initWindow();
        initView();
        //如果加载数据不成功，（图片加载失败）返回，
        if (!initData()) return;
        initFragment();

        setViewContent();
        test();
    }

    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(Util.getColor(this, R.color.ptu_toolbar_background));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    /**
     * 如果加载数据不成功，（图片加载失败）返回false，
     *
     * @return 是否成功加载图片到PtuView
     */
    private boolean initData() {
        //如果是从其它应用过来需要编辑图片的
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            Util.P.le(uri.getPath());
            picPath = FileTool.getImagePathFromUri(this, uri);
        } else {
            picPath = intent.getStringExtra("picPath");
        }

        if (!new File(picPath).exists()) {
            ptuView.setBitmapAndInit(picPath, 200, 200);
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
            return false;
        }
        return true;
    }

    private void test() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switchFragment(NAME_TEXT);
                floatTextView.setText("ahas大S大航红啊");
            }
        }, 500);
    }

    private void initView() {
        ptuFrame = (PtuFrameLayout) findViewById(R.id.ptu_frame);
        ptuView = (PtuView) findViewById(R.id.ptu_view);

        int top_btn_width = AllDate.screenWidth / 8;
        int smallDividerWidth = Util.dp2Px(16);
        int bigDividerWidth = (int) (AllDate.screenWidth * 7.5 / 112);

        topRelativeLayout = (PtuTopRealtiveLayout) findViewById(R.id.ptu_toolbar_relative);

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
                        if (CURRENT_EDIT_MODE != EDIT_NO)
                            sure();
                        resultIntent.setAction("finish");
                        savePtuView();
                    }
                }
        );

        cancelBtn = topRelativeLayout.createCancel(top_btn_width, smallDividerWidth);
        sureBtn = topRelativeLayout.createSure(top_btn_width, smallDividerWidth);
        View returnBtn = topRelativeLayout.createReturn(top_btn_width, smallDividerWidth);
        View saveSetBtn = topRelativeLayout.createSaveSet(top_btn_width, smallDividerWidth);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        returnBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PtuActivity.this.finish();
                    }
                }
        );
        saveSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSet();
            }
        });
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_EDIT_MODE != EDIT_NO)
                    sure();
            }
        });

        topRelativeLayout.addReturn();
        topRelativeLayout.addSaveSet();
    }

    private void saveSet() {

    }

    private void setViewContent() {
        ptuFrame.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ptuView.setBitmapAndInit(picPath, ptuFrame.getWidth(), ptuFrame.getHeight());
                        repealRedoManager.setBaseBm(ptuView.getSourceBm()
                                .copy(Bitmap.Config.ARGB_8888, true));
                        ptuFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Intent intent = getIntent();
                        final String action = intent.getStringExtra("action");
                        if (action != null) {
                            switch (action) {
                                case "text":
                                    if (textFrag == null) {
                                        textFrag = new AddTextFragment();
                                    }
                                    ptuView.initialDraw();
                                    fm.beginTransaction().add(R.id.fragment_function, textFrag)
                                            .addToBackStack("main")
                                            .commit();
                                    floatTextView = ptuFrame.initAddTextFloat(ptuView.getBound());
                                    textFrag.setFloatView(floatTextView);
                            }
                        }
                    }
                });
    }


    /*************************************************************
     * repealRedo部分
     *************************************************/

    private void addStep(Bitmap bm, StepData sd) {
        switch (sd.EDIT_MODE) {
            case EDIT_TEXT:
                AddTextFragment.addBigStep(bm, sd);
                break;
            case EDIT_TIETU:
                FloatImageView.addBigStep(bm, sd);
                break;
            case EDIT_DRAW:
                DrawFragment.addBigStep(bm, sd);
                break;
        }

    }

    private void bigRepeal() {
        if (repealRedoManager.canRepeal()) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();

            repealRedoManager.repeal();
            ptuView.releaseSource();
            Bitmap newSourceBm = repealRedoManager.getBaseBitmap().
                    copy(Bitmap.Config.ARGB_8888, true);
            ptuView.replaceSourceBm(newSourceBm);

            int index = repealRedoManager.getCurrentIndex();
            for (int i = 0; i <= index; i++) {
                StepData sd = repealRedoManager.getStepdata(i);
                addStep(newSourceBm, sd);
            }
            ptuView.resetShow();
            progressDialog.dismiss();
            checkRepealRedo();
        }
    }

    public void bigRedo() {
        if (repealRedoManager.canRedo()) {
            Bitmap sourceBm = ptuView.getSourceBm();
            addStep(sourceBm, repealRedoManager.redo());
            ptuView.resetShow();
            checkRepealRedo();
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
    public void switchFragment(String function) {
        ptuView.resetShow();
        if (function.equals(NAME_MAIN)) {
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                            R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                    .replace(R.id.fragment_function, mainFrag)
                    .commit();
        } else {
            onToSecondFunction();
            switch (function) {
                case NAME_CUT:

                    CURRENT_EDIT_MODE = EDIT_CUT;
                    break;
                case NAME_TEXT:

                    if (textFrag == null) {
                        textFrag = new AddTextFragment();
                    }
                    fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                                    R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                            .replace(R.id.fragment_function, textFrag)
                            .commit();
                    CURRENT_EDIT_MODE = EDIT_TEXT;
                    floatTextView = ptuFrame.initAddTextFloat(ptuView.getBound());
                    textFrag.setFloatView(floatTextView);

               /* //让文本框一开始就获得输入法
                floatTextView.setFocusable(true);
                floatTextView.requestFocus();
                onFocusChange(floatTextView.isFocused());*/
                    break;
                case NAME_TIE_TU:
                    if (tietuFrag == null) {
                        tietuFrag = new TietuFragment();
                    }
                    tietuFrag.setFloatImageView(ptuFrame.initAddImageFloat(ptuView.getBound()));
                    fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_bottom_out,
                                    R.animator.slide_bottom_in, R.animator.slide_bottom_out)
                            .replace(R.id.fragment_function, tietuFrag)
                            .commit();
                    CURRENT_EDIT_MODE = EDIT_TIETU;
                    break;
                case NAME_DRAW:

                    CURRENT_EDIT_MODE = EDIT_TIETU;
                    break;
                case NAME_MAT:


                    CURRENT_EDIT_MODE = EDIT_TIETU;
                    break;
            }
        }
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
        if (CURRENT_EDIT_MODE != EDIT_NO) {
            cancel();
        } else super.onBackPressed();
    }

    /**
     * 回到主功能界面时,如果是从子功能回来，
     * （1）会添加子功能的view级参数到撤销重做list中。
     * （2）移除浮动图，获取浮动图的图片显示到putview上面
     * 注意sure会使用延时，不要在它后面字节写函数
     */
    private void sure() {
        if (CURRENT_EDIT_MODE == EDIT_CUT) {
            Bitmap newSourceBm = cutFrag.getResultBm(1);
            ptuView.replaceSourceBm(newSourceBm);
            ptuView.resetShow();
        }//添加文字
        else if (CURRENT_EDIT_MODE == EDIT_TEXT) {
            final RectF innerRect = new RectF(), boundRectInPic = new RectF();
            boolean canGet = floatTextView.prepareResultBitmap(ptuView.getInitRatio(),
                    innerRect, boundRectInPic);//先获取
            if (!canGet) {//有些情况下会返回空
                Util.T(PtuActivity.this, "操作失败，获取到的图像为空");
                ptuFrame.removeViewAt(1);
                return;
            }

            floatTextView.getViewTreeObserver().
                    addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    Bitmap textBitmap = RepealRedoManager.getInnerBmFromView(floatTextView, innerRect);
                                    ptuView.addBitmap(textBitmap, boundRectInPic, 0);

                                    TextStepData tsd = new TextStepData(EDIT_TEXT);
                                    tsd.floatTextView = floatTextView;
                                    tsd.innerRect = innerRect;
                                    tsd.boundRectInPic = boundRectInPic;

                                    //释放，删除等部分
                                    textBitmap.recycle();
                                    floatTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    ptuFrame.removeViewAt(1);

                                    afterSure(tsd);
                                }
                            });
        } //贴图
        else if (CURRENT_EDIT_MODE == EDIT_TIETU) {

            TietuStepData ttsd = (TietuStepData) tietuFrag.getResultData(finalRatio);
            ttsd.EDIT_MODE = EDIT_TIETU;
            Bitmap source = tietuFrag.getResultBm(finalRatio);
            ptuView.addBitmap(source,
                    ttsd.boundRectInPic,
                    ttsd.rotateAngle);

            //释放，删除等部分
            ptuFrame.removeViewAt(1);
            tietuFrag.releaseResourse();

            afterSure(ttsd);
        } //绘图
        else if (CURRENT_EDIT_MODE == EDIT_DRAW) {
            StepData dsd = drawFrag.getResultData(1);
            afterSure(dsd);
        }
    }

    /**
     * 将步骤数据提交，如果步数超限，则让BaseBitmap前进一步
     */
    private void afterSure(StepData sd) {
        StepData resd = repealRedoManager.commit(sd);
        if (resd != null)//超限了，爸最开始的的一步添加到基图上
            addStep(repealRedoManager.getBaseBitmap(), resd);
        checkRepealRedo();
        cancel();
    }

    /**
     * 取消，点击取消按钮，back按键，点击sure按钮保存之后都会有它
     */
    private void cancel() {
        if (ptuFrame.getChildCount() > 1) {
            ptuFrame.removeViewAt(1);
        }
        topRelativeLayout.removeCancle();
        topRelativeLayout.removeSure();
        topRelativeLayout.addReturn();
        topRelativeLayout.addSaveSet();
        CURRENT_EDIT_MODE = EDIT_NO;
        switchFragment(NAME_MAIN);
    }

    private void savePtuView() {
        new Handler().postDelayed(new Runnable() {

            private String result = null;

            @Override
            public void run() {
                if (!hasChanged && finalRatio == 1) {
                    result = "图片未改变";
                } else {
                    Bitmap bitmap = ptuView.getFinalPicture(finalRatio);
                    String newPath = FileTool.getNewPictureFile(picPath);
                    result = BitmapTool.saveBitmap(PtuActivity.this, bitmap, newPath);
                    resultIntent.putExtra("picPath", picPath);
                    resultIntent.putExtra("newPath", newPath);
                }
                setResult(0, resultIntent);
                Util.P.le(TAG, result);
                PtuActivity.this.finish();
            }
        }, 600);
    }
}