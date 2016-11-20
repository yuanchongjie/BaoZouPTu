package a.baozouptu.ptu.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import a.baozouptu.R;
import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.view.FirstUseDialog;
import a.baozouptu.ptu.BaseFunction;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.RepealRedoListener;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TextStepData;
import a.baozouptu.ptu.view.PtuFrameLayout;
import a.baozouptu.ptu.view.PtuView;

/**
 * 添加文字功能的fragment
 * Created by Administrator on 2016/5/1.
 */
public class TextFragment extends Fragment implements BaseFunction {
    PtuActivity mAcitivty;
    LinearLayout toumingdu;
    LinearLayout style;
    LinearLayout color;
    LinearLayout typeface;
    LinearLayout rubber;
    //LinearLayout bouble;
    private FunctionPopWindowBuilder textPopupBuilder;
    private FloatTextView floatTextView;
    private String TAG = "TextFragment";
    private RubberView rubberView;
    RepealRedoListener repealRedoListener;
    PtuView ptuView;

    /**
     * 已经存在bitmap的情况下，更快速的添加
     * @param addBm 要添加的图片
     */
    public void addBigStep(Bitmap addBm,StepData sd){
        TextStepData tsd = (TextStepData) sd;
        //擦除的东西添加上去
        Canvas canvas = new Canvas(ptuView.getSourceBm());
        ArrayList<Pair<Path, Paint>> pathPaintList = tsd.getRubberData();
        for (Pair<Path, Paint> pair : pathPaintList) {
            canvas.drawPath(pair.first, pair.second);
        }
        if (addBm != null) {
            ptuView.addBitmap(addBm, tsd.boundRectInPic, 0);
        }else//需要重绘显示出来
        {
            ptuView.invalidate();
        }
    }

    public void addBigStep(StepData sd) {

        TextStepData tsd = (TextStepData) sd;
        //擦除的东西添加上去
        Canvas canvas = new Canvas(ptuView.getSourceBm());
        ArrayList<Pair<Path, Paint>> pathPaintList = tsd.getRubberData();
        for (Pair<Path, Paint> pair : pathPaintList) {
            canvas.drawPath(pair.first, pair.second);
        }
        if (sd.picPath != null) {
            Bitmap bm = BitmapTool.getLosslessBitmap(sd.picPath);
            ptuView.addBitmap(bm, tsd.boundRectInPic, 0);
        }else//需要重绘显示出来
        {
            ptuView.invalidate();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAcitivty = (PtuActivity) getActivity();//onAttach貌似不会执行，需要在这里获取context
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        initView(view);
        setClick();
        return view;
    }

    @Override
    public void onResume() {
        textPopupBuilder = new FunctionPopWindowBuilder(mAcitivty,floatTextView,this);
        textPopupBuilder.setRubberView(rubberView);
        super.onResume();
    }

    /**
     * 获取addTextFragment上的view组件
     *
     * @param view
     */
    private void initView(View view) {
        toumingdu = (LinearLayout) view.findViewById(R.id.add_text_toumingdu);
        style = (LinearLayout) view.findViewById(R.id.add_text_style);
        color = (LinearLayout) view.findViewById(R.id.add_text_color);
        typeface = (LinearLayout) view.findViewById(R.id.add_text_typeface);
        rubber = (LinearLayout) view.findViewById(R.id.text_rubber);
        //bouble = (LinearLayout) view.findViewById(R.id.add_text_bubble);
    }

    /**
     * 设置点击功能后的反应
     */
    private void setClick() {
        toumingdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPopupBuilder.setToumingduPopWindow(v);
            }
        });
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPopupBuilder.setColorPopWindow(v);
            }
        });
        typeface.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textPopupBuilder.setTypefacePopWindow(v);
            }
        });
        style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPopupBuilder.setStylePopWindow(v);
            }
        });
        rubber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AllData.appConfig.hasReadTextRubber()) {
                    final FirstUseDialog firstUseDialog = new FirstUseDialog(getActivity());
                    firstUseDialog.createDialog(null, "滑动即可擦除,可在左边选择颜色粗细", new FirstUseDialog.ActionListener() {
                        @Override
                        public void onSure() {
                            AllData.appConfig.writeConfig_TextRubber(true);
                        }
                    });
                }
                switchRubber();
            }
        });
    }

    private void switchRubber() {
        PtuFrameLayout ptuFrame = ((PtuActivity) getActivity()).getPtuFrame();
        if (ptuFrame.indexOfChild(floatTextView) == -1) {
            style.setVisibility(View.VISIBLE);
            typeface.setVisibility(View.VISIBLE);
            ((TextView)toumingdu.getChildAt(1)).setText("透明度");
            ptuFrame.addView(floatTextView);
        } else {
            style.setVisibility(View.INVISIBLE);
            typeface.setVisibility(View.INVISIBLE);
            ((TextView)toumingdu.getChildAt(1)).setText("尺寸");
            ptuFrame.removeView(floatTextView);
        }
    }

    public void setFloatView(FloatTextView floatView) {
        this.floatTextView = floatView;
        floatTextView.setTypeface(Typeface.DEFAULT);
    }

    public Bitmap getResultBm() {
        return floatTextView.getResultBm();
    }

    public void releaseResource() {
        floatTextView.releaseResource();
    }

    public void setRepealRedoListener(RepealRedoListener repealRedoListener) {
        this.repealRedoListener = repealRedoListener;
    }

    public void setPtuView(PtuView ptuView) {
        this.ptuView = ptuView;
    }

    public void addRubberView(Context context, PtuFrameLayout ptuFrame) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ptuView.getDstRect().width(), ptuView.getDstRect().height());
        params.setMargins(ptuView.getDstRect().left, ptuView.getDstRect().top, 0, 0);
        rubberView = new RubberView(context, ptuView);
        rubberView.setRepealRedoListener(repealRedoListener);
        ptuFrame.addView(rubberView, params);
    }


    @Override
    public void onDestroy() {
        //textPopBuilder的状态都取消了
        textPopupBuilder = null;
        super.onDestroy();
    }

    @Override
    public void smallRepeal() {
        rubberView.smallRepeal();
    }

    @Override
    public void smallRedo() {
        rubberView.smallRedo();
    }

    @Override
    public void repeal() {

    }

    @Override
    public void redo(StepData sd) {

    }

    @Override
    public Bitmap getResultBm(float ratio) {
        return null;
    }

    @Override
    public StepData getResultData(float ratio) {
        TextStepData tsd = floatTextView.getResultData(ptuView);
        if (rubberView != null) {
            if (tsd == null)
                tsd = new TextStepData(PtuUtil.EDIT_TEXT);
            tsd.addRubberDate(rubberView.getResultData());
        }
        return tsd;
    }

    public boolean onBackPressed() {
        if (((PtuActivity) getActivity()).getPtuFrame().indexOfChild(floatTextView) == -1) {
            switchRubber();
            return false;
        }
        return true;
    }
}
