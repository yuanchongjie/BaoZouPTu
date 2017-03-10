package a.baozouptu.ptu.tietu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import javax.inject.Inject;
import javax.inject.Named;

import a.baozouptu.R;
import a.baozouptu.common.util.BitmapTool;
import a.baozouptu.common.util.CustomToast;
import a.baozouptu.common.util.FileTool;
import a.baozouptu.common.util.Util;
import a.baozouptu.chosePicture.ChosePictureActivity;
import a.baozouptu.common.view.PtuConstraintLayout;
import a.baozouptu.ptu.BasePtuFragment;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TietuStepData;
import a.baozouptu.ptu.tietu.onlineTietu.PriorTietuManager;
import a.baozouptu.ptu.tietu.onlineTietu.TietuRecyclerAdapter;
import a.baozouptu.ptu.tietu.onlineTietu.tietu_material;
import a.baozouptu.ptu.tietu.tietuImpact.PictureSynthesis;
import a.baozouptu.ptu.tietu.tietuImpact.SynthesisImagePopupWindow;
import a.baozouptu.ptu.view.PtuFrameLayout;
import a.baozouptu.ptu.view.PtuSeeView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/7/1.
 */
public class TietuFragment extends BasePtuFragment implements TietuContract.TietuView {
    private static String TAG = "TietuFragment";
    private TietuFrameLayout tietuLayout;
    @Inject
    TietuContract.TietuPresenter presenter;
    @Inject
    @Named("expression")
    TietuRecyclerAdapter expressionAdapter;
    @Inject
    @Named("property")
    TietuRecyclerAdapter propertyAdapter;
    Context mContext;
    List<Integer> tietuIds = new ArrayList<>();

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private ConstraintLayout more;

    private PtuSeeView ptuSeeView;
    private PtuFrameLayout ptuFrame;
    private String curCategory = "-----------";


    public void setTietuLayout(TietuFrameLayout tietuLayout) {
        this.tietuLayout = tietuLayout;
        tietuLayout.setOnTietuRemoveListener(
                new TietuFrameLayout.TietuChangeListener() {
                    @Override
                    public void onTietuRemove(FloatImageView view) {
                        //暂无操作
                    }
                });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        DaggerTietuComponent.builder().tietuModule(new TietuModule(this)).build().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tietu, container, false);


        //底部的贴图列表
        recyclerView = new RecyclerView(mContext);
        layoutManager = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        more = (ConstraintLayout) view.findViewById(R.id.function_tietu_more);
//图片融合按钮

        setOnclick(view);
        return view;
    }

    private FloatImageView chosenTietu;

    private void asySynthesis() {
        //RxJava异步处理
        Observable.create(
                new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {
                        chosenTietu = tietuLayout.chosenView;
                        int innerLeft = chosenTietu.getLeft() + FloatImageView.pad - ptuSeeView.getLeft();
                        int innerTop = chosenTietu.getTop() + FloatImageView.pad - ptuSeeView.getTop();
                        Bitmap aboveBm;
                        if (chosenTietu.getPicPath() != null)//重新获取一张图片用于调色
                            aboveBm = TietuSizeControler.getBitmap2Transfer(chosenTietu.getPicPath());
                        else
                            aboveBm = TietuSizeControler.getBitmap2Transfer(chosenTietu.getPicId());
                        Bitmap underBm = Bitmap.createScaledBitmap(ptuSeeView.getSourceBm(),
                                ptuSeeView.getDstRect().width(), ptuSeeView.getDstRect().height(), true);
                        //调色
                        Bitmap bitmap = new PictureSynthesis().
                                changeBm(underBm, aboveBm,
                                        new Rect(innerLeft, innerTop,
                                                innerLeft + chosenTietu.getWidth() - FloatImageView.pad * 2,
                                                innerTop + chosenTietu.getHeight() - FloatImageView.pad * 2));

                        //如果转换失败，获取的数据为空
                        if (bitmap == null) {
                            subscriber.onError(null);
                        } else subscriber.onNext(bitmap);

                    }

                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        //出错，不处理
                        CustomToast.makeText(getActivity(), "不好意思，处理失败了", Toast.LENGTH_SHORT).show();
                       /* Bitmap bitmap;
                        if (chosenTietu.getPicPath() != null) {
                            bitmap = TietuSizeControler.getBitmapInSize(chosenTietu.getPicPath());
                        } else
                            bitmap = BitmapFactory.decodeResource(getResources(), chosenTietu.getPicId());
                        chosenTietu.setImageBitmap(bitmap);*/
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        chosenTietu.setImageBitmap(bitmap);
                    }
                });

    }


    /**
     * 添加一个tietu，
     */
    private void addTietuByPath(String path) {
        Bitmap srcBitmap = TietuSizeControler.getBitmapInSize(path);
        if (srcBitmap == null||srcBitmap.getWidth()==0||srcBitmap.getHeight()==0) {
            CustomToast.makeText("获取贴图失败", Toast.LENGTH_SHORT).show();
            return;
        }
        FloatImageView floatImageView = new FloatImageView(mContext);
        floatImageView.setAdjustViewBounds(true);
        floatImageView.setImageBitmapAndPath(srcBitmap, path);
        FrameLayout.LayoutParams params = TietuSizeControler.getFeatParams(floatImageView, srcBitmap.getWidth(), srcBitmap.getHeight(),
                ptuSeeView.getPicBound());
        tietuLayout.addView(floatImageView, params);
    }

    /**
     * 添加一个tietu，
     */
    private void addTietuById(Integer id) {
        Bitmap srcBitmap = TietuSizeControler.getBitmapInSize(id);
        if (srcBitmap == null||srcBitmap.getWidth()==0||srcBitmap.getHeight()==0)  {
            CustomToast.makeText("获取贴图失败", Toast.LENGTH_SHORT).show();
            return;
        }
        FloatImageView floatImageView = new FloatImageView(mContext);
        floatImageView.setAdjustViewBounds(true);
        floatImageView.setImageBitmapAndId(srcBitmap, id);
        FrameLayout.LayoutParams params = TietuSizeControler.getFeatParams(floatImageView, srcBitmap.getWidth(), srcBitmap.getHeight(),
                ptuSeeView.getPicBound());
        Log.e(TAG, "添加位置" + params.leftMargin + " " + params.topMargin);
        tietuLayout.addView(floatImageView, params);
    }

    private void setOnclick(final View view) {
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChosePictureActivity.class);
                intent.setAction("tietu");
                startActivityForResult(intent, 11);
            }
        });

        final ConstraintLayout layoutExpression = ((ConstraintLayout) view.findViewById(R.id.tietu_function_expression));
        layoutExpression.setTag("tietuExpression");
        layoutExpression.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prepareSomeTietu(view, tietu_material.CATEGORY_EXPRESSION);
                    }
                });
        final ConstraintLayout layoutProperty = (ConstraintLayout) view.findViewById(R.id.tietu_function_property);
        layoutProperty.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prepareSomeTietu(view, tietu_material.CATEGORY_PROPERTY);
                    }
                });
        layoutProperty.setTag("tietuProperty");

        expressionAdapter.setOnItemClickListener(tietuRecyclerListener);
        propertyAdapter.setOnItemClickListener(tietuRecyclerListener);

        final ConstraintLayout layoutSynthesis = ((ConstraintLayout) view.findViewById(R.id.function_tietu_synthesis));
        layoutSynthesis.setTag("tietuSynthesis");
        layoutSynthesis.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Util.DoubleClick.isDoubleClick() && tietuLayout.getChildCount() != 0)
                            asySynthesis();
                    }
                });
    }

    private void prepareSomeTietu(View view, String category) {
        PtuConstraintLayout parent = (PtuConstraintLayout) view.getParent().getParent();
        if (curCategory.equals(category) && recyclerView.getParent() != null) {
            recyclerView.setAdapter(null);
            ((ViewGroup) recyclerView.getParent()).removeView(recyclerView);
            curCategory = "-------------";
            return;
        }
        if (parent.indexOfChild(recyclerView) == -1) {
            parent.addTietuListView(recyclerView, view);
        }
        presenter.prepareTietuByCategory(category);
        curCategory = category;
    }

    private TietuRecyclerAdapter.OnRecyclerViewItemClickListener tietuRecyclerListener =
            new TietuRecyclerAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, Object data) {
                    if (data instanceof Integer)
                        addTietuById((Integer) data);
                    else
                        addTietuByPath(PriorTietuManager.getTietuFilePath(((tietu_material) data)));
                }
            };

    @Override
    public void smallRepeal() {
        /*if (tietuLayout.getChildCount() > 0)
            tietuLayout.removeViewAt(tietuLayout.getChildCount() - 1);*/
    }

    public void smallRedo() {

    }

    @Override
    public Bitmap getResultBm(float ratio) {
        return null;
    }

    /**
     * 获取结果，因为会有多个贴图，所以返回的 {@link TietuStepData} 里面放的是{@link StepData}的链表
     *
     * @return {@link StepData}
     */
    @Override
    public StepData getResultDataAndDraw(float ratio) {
        TietuStepData tsd = new TietuStepData(PtuUtil.EDIT_TIETU);
        int count = tietuLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            //获取数据
            FloatImageView fiv = (FloatImageView) tietuLayout.getChildAt(i);
//获取每个tietu的范围
            RectF boundRectInPic = new RectF();
            float[] realLocation = PtuUtil.getLocationAtPicture(fiv.getLeft() + FloatImageView.pad, fiv.getTop() + FloatImageView.pad,
                    ptuSeeView.getSrcRect(), ptuSeeView.getDstRect());
            boundRectInPic.left = realLocation[0];
            boundRectInPic.top = realLocation[1];

            realLocation = PtuUtil.getLocationAtPicture(fiv.getRight() - FloatImageView.pad, fiv.getBottom() - FloatImageView.pad,
                    ptuSeeView.getSrcRect(), ptuSeeView.getDstRect());
            boundRectInPic.right = realLocation[0];
            boundRectInPic.bottom = realLocation[1];
            Bitmap tietuBm = fiv.getSrcBitmap();

            //暂存数据到sd卡上面
            String tempPath = FileTool.createTempPicPath();
            BitmapTool.asySaveTempBm(tempPath, tietuBm, new MySubscriber<String>(tietuBm) {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(TAG, "onNext: 保存出错" + throwable.getMessage());
                }

                @Override
                public void onNext(String s) {
                    Log.e(TAG, "onNext: 保存完成" + Thread.currentThread().getName());
                }

            });

            TietuStepData.OneTietu oneTietu = new TietuStepData.OneTietu(tempPath, boundRectInPic, fiv.getRotation());
            tsd.addOneTietu(oneTietu);

            //绘制结果到ptuView上面
            ptuSeeView.addBitmap(tietuBm,
                    oneTietu.getBoundRectInPic(), oneTietu.getRotateAngle());
        }
        return tsd;

    }

    public void addBigStep(StepData sd) {
        TietuStepData ttsd = (TietuStepData) sd;
        Iterator<TietuStepData.OneTietu> iterator = ttsd.iterator();
        while (iterator.hasNext()) {
            TietuStepData.OneTietu oneTietu = iterator.next();
            Bitmap tietuBm = BitmapTool.getLosslessBitmap(oneTietu.getPicPath());
            Log.e(TAG, "addBigStep: 重做贴图" + tietuBm);
            ptuSeeView.addBitmap(tietuBm,
                    oneTietu.getBoundRectInPic(), oneTietu.getRotateAngle());
        }
    }

    @Override
    public void releaseResource() {
        int count = tietuLayout.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            ((FloatImageView) tietuLayout.getChildAt(i)).releaseResourse();
            tietuLayout.removeViewAt(i);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public void initBeforeCreateView(final PtuFrameLayout ptuFrame, final PtuSeeView ptuSeeView) {
        this.ptuSeeView = ptuSeeView;
        this.ptuFrame = ptuFrame;
        setTietuLayout(ptuFrame.initAddImageFloat(new Rect(
                ptuFrame.getLeft(), ptuFrame.getTop(), ptuFrame.getRight(), ptuFrame.getBottom()
        )));
    }

    @Override
    public boolean onBackPressed() {
        if (recyclerView.getParent() != null) {
            ((ViewGroup) recyclerView.getParent()).removeView(recyclerView);
            return true;
        }
        return false;
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void showExpressionList() {
        recyclerView.swapAdapter(expressionAdapter, true);
    }

    @Override
    public void showPropertyList() {
        recyclerView.swapAdapter(propertyAdapter, true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11 && data != null) {
            String path = data.getStringExtra("pic_path");
            if (path != null)
                addTietuByPath(path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

abstract class MySubscriber<T> extends Subscriber<T> {
    Bitmap bitmapData;

    MySubscriber(Bitmap bitmapData) {
        super();
        this.bitmapData = bitmapData;
    }
}
//else addTietuByPath();

                /*
//                处理设置贴图失败的情况，暂不实现
                boolean flag=false;
                while(!floatImageView.setBitmapAndInit(data)&&tietuIds.fixed_size()>0)
                {
                    flag=true;
                    int id=tietuIds.indexOf(data);
                    tietuIds.remove(data);
                    if(tietuIds.fixed_size()==0){
                        ((ViewGroup)floatImageView.getParent()).removeView(floatImageView);
                        Toast.makeText(mContext,"贴图加载失败了！",Toast.LENGTH_SHORT).show();
                        tietuAdapter.notifyDataSetChanged();
                        return;
                    }
                    else{
                        data=tietuIds.get(id%tietuIds.fixed_size());
                    }
                }
                if(flag) {
                    Toast.makeText(mContext,"贴图加载失败了！",Toast.LENGTH_SHORT).show();
                    tietuAdapter.notifyDataSetChanged();
                }*/