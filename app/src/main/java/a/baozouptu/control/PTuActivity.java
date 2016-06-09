package a.baozouptu.control;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import a.baozouptu.R;
import a.baozouptu.tools.BitmapTool;
import a.baozouptu.tools.FileTool;
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

    public static interface ResultInterface {
        /**
         * <p> 获取子功能生成点的bitmap，以及bitmap的大小，位置的相关参数
         * <p> 传入initRatio：ptuView上的图片一开始缩放的比例，
         * <p>  finalRatio最终需要缩放的比例，
         * <p>方法内部再用RealRatio=finalRation/initRation,算出实际的缩放比例,
         * <p>然后得出子功能获得参数：
         * <p>相对left：rleft=（FloatView的letf-ptuView的图片的left）*realRatio,rtop一样
         * <p>FloatView的宽mwidth*=realRatio获取的实际的宽，高一样
         * <p>最后采用相应方法获取相应大小的Bitmap对象，
         * <p>将参数装入bitmapPara，返回bitmap对象
         *
         * @param bitmapPara 子功能获取的bitmap的参数,0为获取图片相对原图片的左边距，1为获取图片相对原图片的上边距，
         *                   <p>2为获取图片的宽，3为获取图片的高度
         *                   <p> 注意, 每个子功能fragment被添加时，要将此接口赋值，保证调用的是对应的接口
         */
        Bitmap getResultBitmap(float initRatio, float finalRatio, float[] bitmapPara);
    }

    private ResultInterface resultInterface;


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
                            Bitmap bitmap = resultInterface.getResultBitmap(ptuView.getInitRatio(),
                                    finalRatio, childFunctionbitmapPara);//先获取

                            ptuFrame.removeViewAt(1);
                            ptuView.addBitmap(bitmap, childFunctionbitmapPara);
                        }
                        Bitmap bitmap = ptuView.getFinalPicture(finalRatio);
                        String newPath = FileTool.getNewPicturePath(picPath);
                        if(BitmapTool.saveBitmap(bitmap, newPath).equals("创建成功"))
                        {//创建成功，退出应用，activity
                            PTuActivity.this.finish();
                        }
                    }
                }
        );
    }

    void setResultInterface(ResultInterface result) {
        resultInterface = result;
    }

    private void setViewContent() {
        Intent intent = getIntent();
        picPath = intent.getStringExtra("picPath");
        ptuView.setBitmapAndInit(picPath);
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
                FloatTextView floatTextView = ptuFrame.initAddFloat(ptuView.getBound());
                fragText.setFloatView(floatTextView);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(ptuFrame.getChildCount()>1)
            ptuFrame.removeViewAt(1);
        super.onBackPressed();
    }
}
