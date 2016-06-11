package a.baozouptu.control;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import a.baozouptu.R;
import a.baozouptu.tools.BitmapTool;
import a.baozouptu.tools.FileTool;
import a.baozouptu.tools.Util;
import a.baozouptu.view.FloatTextView;
import a.baozouptu.view.PtuFrameLayout;
import a.baozouptu.view.PtuView;

public class PTuActivity extends Activity implements MainFunctionFragment.Listen {
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
    private float finalRatio = 1;
    /**
     * 子功能获取的bitmap的参数,0为获取图片相对原图片的左边距，1为获取图片相对原图片的上边距，
     * <p>2为获取图片的宽，3为获取图片的高度
     */
    private float[] childFunctionbitmapPara;
    private String picPath = null;
    private FloatTextView floatTextView;

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
        //去发送按钮
        ImageButton goSend = (ImageButton) findViewById(R.id.menu_go_send);
        goSend.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //上面有图，先把图添加到PtuView上面
                        if (ptuFrame.getChildCount() > 1) {
                            childFunctionbitmapPara = new float[4];
                            boolean canGet = floatTextView.prepareResultBitmap(ptuView.getInitRatio(),
                                    finalRatio, childFunctionbitmapPara);//先获取
                            if (!canGet) {//有些情况下会返回空
                                Util.T(PTuActivity.this, "获取到的图像为空");
                                return;
                            } else {
                                //view加载完成时回调
                                final Bitmap[] textBitmap = new Bitmap[1];
                                floatTextView.getViewTreeObserver().addOnGlobalLayoutListener(
                                        new ViewTreeObserver.OnGlobalLayoutListener() {
                                            @Override
                                            public void onGlobalLayout() {
                                                textBitmap[0] = getViewBitmap(floatTextView);
                                                Dialog dialog = new Dialog(PTuActivity.this);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.setContentView(R.layout.test);
                                                ImageView image = (ImageView) dialog.findViewById(R.id.test_image);
                                                image.setImageBitmap(textBitmap[0]);
                                                dialog.show();
                                                // ptuFrame.removeViewAt(1);
                                                //ptuView.addBitmap(textBitmap[0], finalRatio, childFunctionbitmapPara);
                                                //Bitmap bitmap = ptuView.getFinalPicture(finalRatio);
                                                //String newPath = FileTool.getNewPicturePath(picPath);
                                                //String result = BitmapTool.saveBitmap(bitmap, newPath);
                                                //if (("创建成功".equals(result))) {//创建成功，退出应用，activity
                                                //     Util.P.le(result);
                                                // PTuActivity.this.finish();
                                                // } else
                                                //     Util.T(PTuActivity.this, result);
                                                // floatTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                            }
                                        });

                            }
                        }

                    }
                }
        );
    }

    private void setViewContent() {
        Intent intent = getIntent();
        picPath = intent.getStringExtra("picPath");
        ptuView.setBitmapAndInit(picPath);
    }

    private Bitmap getViewBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = null;
        try {
            if (null != view.getDrawingCache()) {
                bitmap = Bitmap.createScaledBitmap(view.getDrawingCache(),
                        floatTextView.getWidth(), floatTextView.getHeight(), true);
            } else {
                return null;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            view.setDrawingCacheEnabled(false);
            view.destroyDrawingCache();
        }
        return bitmap;
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

                floatTextView.setFocusable(true);
                floatTextView.requestFocus();
                onFocusChange(floatTextView.isFocused());
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
        if (ptuFrame.getChildCount() > 1)
            ptuFrame.removeViewAt(1);
        super.onBackPressed();
    }
}
