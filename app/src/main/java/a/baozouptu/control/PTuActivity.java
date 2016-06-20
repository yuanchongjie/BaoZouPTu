package a.baozouptu.control;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import a.baozouptu.R;
import a.baozouptu.dataAndLogic.MainStepData;
import a.baozouptu.dataAndLogic.RePealRedoList;
import a.baozouptu.tools.BitmapTool;
import a.baozouptu.tools.FileTool;
import a.baozouptu.tools.Util;
import a.baozouptu.view.FloatTextView;
import a.baozouptu.view.FloatView;
import a.baozouptu.view.PtuFrameLayout;
import a.baozouptu.view.PtuView;

public class PTuActivity extends Activity implements MainFunctionFragment.Listen {
    public static final String DEBUG_TAG = "PTuActivity";
    /**
     * 主功能的fragment
     */
    MainFunctionFragment mainFrag;
    FragmentTransaction ft;
    FragmentManager fm;
    private MainFunctionFragment fragMain;
    private AddTextFragment fragText;
    private PtuView ptuView;
    private PtuFrameLayout ptuFrame;
    /**
     * 子功能获取的bitmap的参数,0为获取图片相对原图片的左边距，1为获取图片相对原图片的上边距，
     * <p>2为获取图片的宽，3为获取图片的高度
     */
    private String picPath = null;
    private FloatTextView floatTextView;
    private RePealRedoList<MainStepData> rePealRedoList = new RePealRedoList<>();
    private float finalRatio = 1;
    private PopupWindow redoPopWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptu);

        initView();
        setViewContent();
        setFragment();
        setOnClick();
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
                    redoPopWindow = new PopupWindow(PTuActivity.this);
                    ImageView image = new ImageView(PTuActivity.this);
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
                            onReturnMainFunction();
                        savePtuView();
                    }
                }
        );
        ImageButton cancel = (ImageButton) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ptuFrame.removeViewAt(1);
            }
        });
        ImageButton sure = (ImageButton) findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReturnMainFunction();
            }
        });
    }

    private void addStep(MainStepData md) {
        rePealRedoList.addStep(md);
        addBitmapToPtuView(md);
    }

    private void repealMainFunction() {
        rePealRedoList.startRepeal();
        int pointer = rePealRedoList.getCurrentPoint();
        for (int i = 0; i < pointer; i++) {
            MainStepData md = rePealRedoList.get(i);
            addBitmapToPtuView(md);
        }
    }

    private void redoMainFunction() {
        MainStepData md = rePealRedoList.redo();
        addBitmapToPtuView(md);
    }

    private void addBitmapToPtuView(MainStepData md) {
        Bitmap addBm = getInnerBmFromView(md.getView(), md.getInnerRect());
        ptuView.addBitmap(addBm, md.getOutRect());
    }

    private void setViewContent() {
        Intent intent = getIntent();
        picPath = intent.getStringExtra("picPath");
        ptuView.setBitmapAndInit(picPath);
    }

    /**
     * view的显示在图片上的部分的截图
     *
     * @param view
     * @param innerRect view的显示在图片上的部分的区域
     * @return view的显示在图片上的部分的截图
     */
    private Bitmap getInnerBmFromView(View view, RectF innerRect) {
        Bitmap innerBitmap = null;
        try {
            Bitmap viewBitmap = Bitmap.createBitmap(floatTextView.getWidth(), floatTextView.getHeight(),
                    Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(viewBitmap));
            innerBitmap = Bitmap.createBitmap(viewBitmap, (int) innerRect.left, (int) innerRect.top,
                    (int) (innerRect.right - innerRect.left), (int) (innerRect.bottom - innerRect.top));//获取floatview内部的内容
            viewBitmap.recycle();
        } catch (OutOfMemoryError e) {
            innerBitmap.recycle();
            Util.T(this, "内存超限");
            e.printStackTrace();
        }
        Util.P.le(DEBUG_TAG, "getInnerBmFromView完成");
        return innerBitmap;
    }

    private void setOnClick() {

    }
    //
    //观察Fragment的生命周期，
    // （1）是否Activity创建，不管有没有添加，都会执行
    //(2)执行不同的FragmentTransetion事务其反应如何
    //

    private void setFragment() {
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
            case "text":

                if (fragText == null) {
                    fragText = new AddTextFragment();
                }
                fm.beginTransaction().add(R.id.fragment_function, fragText)
                        .addToBackStack("main")
                        .commit();
                floatTextView = ptuFrame.initAddFloat(ptuView.getBound());
                fragText.setFloatView(floatTextView);

               /* //让文本框一开始就获得输入法
                floatTextView.setFocusable(true);
                floatTextView.requestFocus();
                onFocusChange(floatTextView.isFocused());*/
                break;
        }
    }

    private void onFocusChange(boolean hasFocus) {

        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        floatTextView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (isFocus) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    imm.hideSoftInputFromWindow(floatTextView.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    @Override
    public void onBackPressed() {
        if(ptuFrame.getChildCount()>1) {
            ptuFrame.removeViewAt(1);
            super.onBackPressed();
        }
        else super.onBackPressed();
    }

    /**
     * 回到主功能界面时,如果是从子功能回来，
     * （1）会添加子功能的view级参数到撤销重做list中。
     * （2）移除浮动图，获取浮动图的图片显示到putview上面
     */
    private void onReturnMainFunction() {
        if (ptuFrame.getChildCount() > 1) {
            final View view = ptuFrame.getChildAt(1);
            if (view instanceof FloatView) {
                final RectF innerRect = new RectF(), picRect = new RectF();
                boolean canGet = ((FloatView) view).prepareResultBitmap(ptuView.getInitRatio(),
                        innerRect, picRect);//先获取
                if (!canGet) {//有些情况下会返回空
                    Util.T(PTuActivity.this, "操作失败，获取到的图像为空");
                    return;
                } else {
                    //能获取到，将图绘制到sourceBitmap上，再绘制PtuView上，并且将view放入撤销重做list中
                    final Bitmap[] textBitmap = new Bitmap[1];
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ptuFrame.removeViewAt(1);
                            textBitmap[0] = getInnerBmFromView(floatTextView, innerRect);
                           /* Dialog dialog = new Dialog(PTuActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.test);
                            ImageView image = (ImageView) dialog.findViewById(R.id.test_image);
                            image.setImageBitmap(textBitmap[0]);
                            dialog.show();*/
                            addStep(new MainStepData(view, innerRect, picRect));
                        }
                    }, 300);
                }
            }
        }
    }

    private void savePtuView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ptuView.getFinalPicture(finalRatio);
                String newPath = FileTool.getNewPicturePath(picPath);
                String result = BitmapTool.saveBitmap(PTuActivity.this, bitmap, newPath);
                setResult(0,new Intent().putExtra("path",picPath).putExtra("newPath",newPath));
                if (("创建成功".equals(result))) {//创建成功，退出应用，activity
                    Util.P.le(DEBUG_TAG,result);
                    PTuActivity.this.finish();
                } else
                    Util.T(PTuActivity.this, result);
            }
        }, 600);
    }
}
