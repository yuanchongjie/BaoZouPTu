package a.baozouptu.control;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import a.baozouptu.R;
import a.baozouptu.dataAndLogic.AllDate;
import a.baozouptu.dataAndLogic.RePealRedoList;
import a.baozouptu.dataAndLogic.StepData;
import a.baozouptu.tools.BitmapTool;
import a.baozouptu.tools.FileTool;
import a.baozouptu.tools.Util;
import a.baozouptu.view.FloatImageView;
import a.baozouptu.view.FloatTextView;
import a.baozouptu.view.IconBitmapCreator;
import a.baozouptu.view.PtuFrameLayout;
import a.baozouptu.view.PtuView;

public class PtuActivity extends Activity implements MainFunctionFragment.Listen {
    public static final String TAG = "PtuActivity";
    static int CURRENT_EDIT_MODE = 0;
    static final int EDIT_CUT = 1;
    static final int EDIT_TEXT = 2;
    static final int EDIT_TIETU = 3;
    static final int EDIT_DRAW = 4;
    static final int EDTI_MAT = 5;
    static final int EDIT_NO = 0;

    /**
     * 主功能的fragment
     */
    MainFunctionFragment mainFrag;
    FragmentTransaction ft;
    FragmentManager fm;
    private MainFunctionFragment fragMain;
    private AddTextFragment fragText;
    private TietuFragment fragTietu;
    private PtuView ptuView;
    private PtuFrameLayout ptuFrame;
    /**
     * 子功能获取的bitmap的参数,0为获取图片相对原图片的左边距，1为获取图片相对原图片的上边距，
     * <p>2为获取图片的宽，3为获取图片的高度
     */
    private String picPath = null;
    private FloatTextView floatTextView;
    private RePealRedoList<StepData> rePealRedoList = new RePealRedoList<>();
    private float finalRatio = 1;
    private PopupWindow redoPopWindow;
    private Intent resultIntent = new Intent();

    boolean hasChanged = false;
    private Fragment currenFra;
    private FloatImageView floatImageView;
    private Bitmap dituBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Util.P.le("进入P图Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptu);
        initWindow();
        initView();
        //如果加载数据不成功，（图片加载失败）返回，
        if (!initData()) return;
        initFragment();

        setViewContent();
        setOnClick();
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
     * @return
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

        dituBitmap = new BitmapTool().getLosslessBitmap(picPath);
        if (dituBitmap == null) {
            ptuView.setBitmapAndInit(Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888),
                    200, 200);
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
                changeFragment("text");
            }
        }, 500);
    }

    private void initView() {
        ptuFrame = (PtuFrameLayout) findViewById(R.id.ptu_frame);
        ptuView = (PtuView) findViewById(R.id.ptu_view);
        ptuView.setBackgroundColor(getResources().getColor(R.color.grey));
        ImageButton repealBtn = (ImageButton) findViewById(R.id.repeal);
        repealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (redoPopWindow == null) {
                    redoPopWindow = new PopupWindow(PtuActivity.this);
                    ImageView image = new ImageView(PtuActivity.this);
                    image.setImageResource(R.mipmap.redo);
                     /*image.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    */
                    redoPopWindow.setContentView(image);
                    redoPopWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                    redoPopWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            redoMainFunction();
                        }
                    });
                } else if (!redoPopWindow.isShowing())
                    redoPopWindow.showAsDropDown(v);
                else repealMainFunction();
            }
        });
        //去发送按钮
        ImageButton goSend = (ImageButton) findViewById(R.id.menu_go_send);
        goSend.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ptuFrame.getChildCount() > 1)
                            onFinishStep();
                        resultIntent.setAction("finish");
                        savePtuView();
                    }
                }
        );

        ImageButton cancel = (ImageButton) findViewById(R.id.cancel);
        cancel.setImageBitmap(IconBitmapCreator.getCancelBitmap(this,
                AllDate.toolbarHeight,
                Util.getColor(this, R.color.text_color1), 0xffffffff));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ptuFrame.getChildCount() > 1) {
                    ptuFrame.removeViewAt(1);
                    changeFragment("main");
                }
            }
        });

        ImageButton sure = (ImageButton) findViewById(R.id.sure);
        sure.setImageBitmap(IconBitmapCreator.getSureBitmap(this,
                AllDate.toolbarHeight,
                Util.getColor(this, R.color.text_color1), 0xffffffff));
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishStep();
            }
        });
    }

    private void setViewContent() {
        ptuFrame.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ptuView.setBitmapAndInit(dituBitmap, ptuFrame.getWidth(), ptuFrame.getHeight());
                        ptuFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Intent intent = getIntent();
                        final String action = intent.getStringExtra("action");
                        if (action != null) {
                            switch (action) {
                                case "text":
                                    if (fragText == null) {
                                        fragText = new AddTextFragment();
                                    }
                                    ptuView.initialDraw();
                                    fm.beginTransaction().add(R.id.fragment_function, fragText)
                                            .addToBackStack("main")
                                            .commit();
                                    floatTextView = ptuFrame.initAddTextFloat(ptuView.getBound());
                                    fragText.setFloatView(floatTextView);
                            }
                        }
                    }
                });
    }

    private void addStepData(StepData sd) {
        rePealRedoList.addStep(sd);
    }

    private void repealMainFunction() {
        rePealRedoList.startRepeal();
        int pointer = rePealRedoList.getCurrentPoint();
        for (int i = 0; i < pointer; i++) {
            StepData sd = rePealRedoList.get(i);
            addStepToView(sd);
        }
    }

    private void redoMainFunction() {
        StepData sd = rePealRedoList.redo();
        addStepToView(sd);
    }

    private void addStepToView(StepData sd) {
        hasChanged = true;

        if (sd.EDIT_MODE == EDIT_TEXT) {
            Bitmap textBitmap = getInnerBmFromView(floatTextView, sd.innerRect);
            ptuView.addBitmap(textBitmap, sd.boundRectInPic, 0);
        } else if (sd.EDIT_MODE == EDIT_TIETU) {

        }
    }

    private void setOnClick() {

    }
    //
    //观察Fragment的生命周期，
    // （1）是否Activity创建，不管有没有添加，都会执行
    //(2)执行不同的FragmentTransetion事务其反应如何
    //

    private void initFragment() {
        fragMain = new MainFunctionFragment();
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.fragment_function, fragMain);
        ft.commit();
    }

    @Override
    public void changeFragment(String function) {
        ptuView.resetDraw();
        switch (function) {
            case "main":
                if (fm.getBackStackEntryCount() > 1)
                    fm.beginTransaction().remove(currenFra).commit();
            case "cut":

                break;
            case "text":

                if (fragText == null) {
                    fragText = new AddTextFragment();
                }
                if (fm.getBackStackEntryCount() > 1)
                    fm.beginTransaction().remove(currenFra);
                fm.beginTransaction()
                        .add(R.id.fragment_function, fragText)
                        .addToBackStack("main")
                        .commit();
                CURRENT_EDIT_MODE = EDIT_TEXT;
                currenFra = fragText;
                floatTextView = ptuFrame.initAddTextFloat(ptuView.getBound());
                floatTextView.setText("ahas大S大航红啊");
                fragText.setFloatView(floatTextView);

               /* //让文本框一开始就获得输入法
                floatTextView.setFocusable(true);
                floatTextView.requestFocus();
                onFocusChange(floatTextView.isFocused());*/
                break;
            case "tietu":
                if (fm.getBackStackEntryCount() > 1) fm.beginTransaction().remove(currenFra);
                if (fragTietu == null) {
                    fragTietu = new TietuFragment();
                }
                floatImageView = ptuFrame.initAddImageFloat(ptuView.getBound());
                fragTietu.setFloatImageView(floatImageView);
                fm.beginTransaction()
                        .add(R.id.fragment_function, fragTietu)
                        .addToBackStack("main")
                        .commit();
                currenFra = fragTietu;
                CURRENT_EDIT_MODE = EDIT_TIETU;
                break;
            case "draw":

                break;
            case "mat":

                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (ptuFrame.getChildCount() > 1) {
            ptuFrame.removeViewAt(1);
            super.onBackPressed();
        } else super.onBackPressed();
    }

    /**
     * view的显示在图片上的部分的截图
     *
     * @param view
     * @param innerRect view的显示在图片上的部分的区域
     * @return view的显示在图片上的部分的截图
     */
    private Bitmap getInnerBmFromView(View view, RectF innerRect) {
        final Bitmap[] innerBitmap = new Bitmap[1];
        try {

            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap viewBitmap = view.getDrawingCache();
            innerBitmap[0] = Bitmap.createBitmap(viewBitmap, (int) innerRect.left, (int) innerRect.top,
                    (int) (innerRect.right - innerRect.left), (int) (innerRect.bottom - innerRect.top));//获取floatview内部的内容

            viewBitmap.recycle();
        } catch (OutOfMemoryError e) {
            innerBitmap[0].recycle();
            Util.T(this, "内存超限");
            e.printStackTrace();
        }
        Util.P.le(TAG, "getInnerBmFromView完成");
        return innerBitmap[0];
    }

    /**
     * 回到主功能界面时,如果是从子功能回来，
     * （1）会添加子功能的view级参数到撤销重做list中。
     * （2）移除浮动图，获取浮动图的图片显示到putview上面
     */
    private void onFinishStep() {
        if (CURRENT_EDIT_MODE == EDIT_CUT) {
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
                                    Bitmap textBitmap = getInnerBmFromView(floatTextView, innerRect);
                                    ptuView.addBitmap(textBitmap, boundRectInPic, 0);

                                    StepData sd = new StepData(EDIT_TEXT);
                                    sd.floatTextView = floatTextView;
                                    sd.innerRect = innerRect;
                                    sd.boundRectInPic = boundRectInPic;

                                    addStepData(sd);

                                    floatTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    ptuFrame.removeViewAt(1);
                                }
                            });
        } //贴图
        else if (CURRENT_EDIT_MODE == EDIT_TIETU) {
            StepData sd = floatImageView.getResultData(ptuView.getInitRatio());
            sd.EDIT_MODE = EDIT_TIETU;
            Bitmap source = floatImageView.getSourceBitmap();

            ptuView.addBitmap(source,
                    sd.boundRectInPic,
                    sd.angle);
            ptuFrame.removeViewAt(1);
            floatImageView.releaseResourse();

            rePealRedoList.addStep(sd);
        } else if (CURRENT_EDIT_MODE == EDIT_DRAW) {

        }

    }

    private void savePtuView() {
        new Handler().postDelayed(new Runnable() {

            private String result = null;

            @Override
            public void run() {
                if (hasChanged == false && finalRatio == 1) {
                    result = "图片未改变";
                } else {
                    Bitmap bitmap = ptuView.getFinalPicture(finalRatio);
                    String newPath = FileTool.getNewPictureFile(picPath);
                    result = BitmapTool.saveBitmap(PtuActivity.this, bitmap, newPath);
                    resultIntent.putExtra("path", picPath);
                    resultIntent.putExtra("newPath", newPath);
                }
                setResult(0, resultIntent);
                Util.P.le(TAG, result);
                PtuActivity.this.finish();
            }
        }, 600);
    }

}
