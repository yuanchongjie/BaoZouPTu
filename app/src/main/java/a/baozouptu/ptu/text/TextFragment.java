package a.baozouptu.ptu.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import a.baozouptu.R;
import a.baozouptu.common.util.BitmapTool;
import a.baozouptu.common.util.FileTool;
import a.baozouptu.common.view.FirstUseDialog;
import a.baozouptu.ptu.BasePtuFragment;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.RepealRedoListener;
import a.baozouptu.ptu.common.PtuData;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TextStepData;
import a.baozouptu.ptu.view.ColorPicker;
import a.baozouptu.ptu.view.PtuFrameLayout;
import a.baozouptu.ptu.view.PtuSeeView;
import rx.Subscriber;

/**
 * 添加文字功能的fragment
 * Created by Administrator on 2016/5/1.
 */
public class TextFragment extends BasePtuFragment {
    PtuActivity mAcitivty;
    ConstraintLayout toumingdu;
    ConstraintLayout style;
    ConstraintLayout color;
    ConstraintLayout typeface;
    ConstraintLayout rubber;
    //ConstraintLayout bouble;
    private FunctionPopWindowBuilder textPopupBuilder;
    private FloatTextView floatTextView;
    private String TAG = "TextFragment";
    private RubberView rubberView;
    RepealRedoListener repealRedoListener;
    PtuSeeView ptuSeeView;

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
        textPopupBuilder = new FunctionPopWindowBuilder(mAcitivty, floatTextView, this);
        textPopupBuilder.setRubberView(rubberView);
        super.onResume();
    }

    /**
     * 获取addTextFragment上的view组件
     *
     * @param view
     */
    private void initView(View view) {
        toumingdu = (ConstraintLayout) view.findViewById(R.id.add_text_toumingdu);
        style = (ConstraintLayout) view.findViewById(R.id.add_text_style);
        color = (ConstraintLayout) view.findViewById(R.id.add_text_color);
        typeface = (ConstraintLayout) view.findViewById(R.id.add_text_typeface);
        rubber = (ConstraintLayout) view.findViewById(R.id.text_rubber);
        //bouble = (ConstraintLayout) view.findViewById(R.id.add_text_bubble);
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
                textPopupBuilder.setColorPopWindow(v, new ColorPicker.ColorTarget() {
                    @Override
                    public void setColor(int color) {
                        if (floatTextView.isClickable()) {
                            floatTextView.setTextColor(color);
                            floatTextView.setHintTextColor(color);
                        } else {
                            rubberView.setColor(color);
                        }
                    }

                    @Override
                    public int getCurColor() {
                        if (floatTextView.isClickable()) {
                            return floatTextView.getCurrentTextColor();
                        } else {
                            return rubberView.getColor();
                        }
                    }
                });
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
                if (!PtuData.ptuConfig.hasReadTextRubber()) {
                    final FirstUseDialog firstUseDialog = new FirstUseDialog(getActivity());
                    firstUseDialog.createDialog(null, "滑动即可擦除,可在左边选择颜色和粗细哟", new FirstUseDialog.ActionListener() {
                        @Override
                        public void onSure() {
                            PtuData.ptuConfig.writeConfig_TextRubber(true);
                        }
                    });
                }
                switchRubber();
            }
        });
    }

    private void switchRubber() {
        if (!floatTextView.isClickable()) {//显示文字
            style.setVisibility(View.VISIBLE);
            typeface.setVisibility(View.VISIBLE);
            ((TextView) toumingdu.getChildAt(1)).setText("透明度");
            ((ImageView) toumingdu.getChildAt(0)).setImageResource(R.mipmap.transparency);
            floatTextView.setClickable(true);
        } else {//显示橡皮
            style.setVisibility(View.INVISIBLE);
            typeface.setVisibility(View.INVISIBLE);
            ((TextView) toumingdu.getChildAt(1)).setText("尺寸");
            ((ImageView) toumingdu.getChildAt(0)).setImageResource(R.mipmap.fixed_size);
            floatTextView.setClickable(false);
        }
    }

    public void setFloatView(FloatTextView floatView) {
        this.floatTextView = floatView;
        floatTextView.setTypeface(Typeface.DEFAULT);
    }

    public void releaseResource() {
        floatTextView.releaseResource();
    }

    public void setRepealRedoListener(RepealRedoListener repealRedoListener) {
        this.repealRedoListener = repealRedoListener;
        repealRedoListener.canRepeal(false);
        repealRedoListener.canRedo(false);
    }

    public void setPtuSeeView(PtuSeeView ptuSeeView) {
        this.ptuSeeView = ptuSeeView;
    }

    public void addRubberView(Context context, PtuFrameLayout ptuFrame) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ptuSeeView.getDstRect().width(), ptuSeeView.getDstRect().height());
        params.setMargins(ptuSeeView.getDstRect().left, ptuSeeView.getDstRect().top, 0, 0);
        rubberView = new RubberView(context, ptuSeeView);
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
    public Bitmap getResultBm(float ratio) {
        return null;
    }

    @Override
    public StepData getResultDataAndDraw(float ratio) {
        //获取和保存数据
        TextStepData tsd = new TextStepData(PtuUtil.EDIT_TEXT);
        Bitmap resultBm = floatTextView.getResultData(ptuSeeView, tsd);
        if (resultBm != null) {
            String tempPath = FileTool.createTempPicPath();
            BitmapTool.asySaveTempBm(tempPath, resultBm, new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(String s) {

                }
            });
            tsd.picPath = tempPath;
        }
        if (rubberView != null) {
            tsd.setRubberDate(rubberView.getResultData());
        }

        //绘制结果
        addBigStep(tsd, resultBm);
        return tsd;
    }

    public void addBigStep(StepData sd) {
        addBigStep((TextStepData) sd, sd.picPath == null ? null : BitmapTool.getLosslessBitmap(sd.picPath));
    }

    /**
     * 已经存在bitmap的情况下，更快速的添加
     *
     * @param textAddBm 要添加的图片
     */
    private void addBigStep(TextStepData tsd, Bitmap textAddBm) {
        //擦除的东西添加上去
        ArrayList<Pair<Path, Paint>> pathPaintList = tsd.getRubberData();
        if (pathPaintList != null) {//存在橡皮数据
            Canvas canvas = new Canvas(ptuSeeView.getSourceBm());
            for (Pair<Path, Paint> pair : pathPaintList) {
                canvas.drawPath(pair.first, pair.second);
            }
        }
        if (textAddBm != null) {//存在文字数据
            ptuSeeView.addBitmap(textAddBm, tsd.boundRectInPic, 0);
        } else if (pathPaintList != null)  //存橡皮数据，只刷新橡皮
        {
            ptuSeeView.invalidate();
        }
    }

    public boolean onBackPressed() {
        if (((PtuActivity) getActivity()).getPtuFrame().indexOfChild(floatTextView) == -1) {
            switchRubber();
            return true;
        }
        return false;
    }
}
