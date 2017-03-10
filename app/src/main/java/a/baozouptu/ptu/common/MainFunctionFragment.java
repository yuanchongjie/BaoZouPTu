package a.baozouptu.ptu.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.common.util.Util;
import a.baozouptu.ptu.BasePtuFragment;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.repealRedo.StepData;

import static a.baozouptu.common.util.Util.getStateDrawable;
import static a.baozouptu.common.util.Util.getStateList;

/**
 * Created by Administrator on 2016/4/29.
 *
 */
@TargetApi(Build.VERSION_CODES.M)
public class MainFunctionFragment extends BasePtuFragment {
    int chosedId = 0;
    Context mContext;

    private View view;
    private Listen listen;
    private List<ImageView> imageList = new ArrayList<>();
    private List<ViewGroup> layoutList = new ArrayList<>();
    private List<Integer> layoutIdList;
    private List<Integer> imageIdList;
    private List<Integer> functionConstantList;
    private List<Drawable> drawableList;
    private List<Drawable> chosenDrawableList;
    private int tietuPosition = 2;
    private final String TAG = "MainFunctionFragment";

    @Override
    public void smallRepeal() {

    }

    @Override
    public void smallRedo() {

    }

    @Override
    public Bitmap getResultBm(float ratio) {
        return null;
    }

    @Override
    public StepData getResultDataAndDraw(float ratio) {
        return null;
    }

    @Override
    public void addBigStep(StepData sd) {

    }

    @Override
    public void releaseResource() {

    }

    public interface Listen {
        void switchFragment(int function);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        listen = (Listen) mContext;
        layoutIdList = new ArrayList<>(Arrays.asList(
                R.id.main_function_cut,
                R.id.main_function_text,
                R.id.main_function_tietu,
                R.id.main_function_draw
                // R.id.main_function_mat
        ));
        imageIdList = new ArrayList<>(Arrays.asList(
                R.id.main_function_cut_iv,
                R.id.main_function_text_iv,
                R.id.main_function_tietu_iv,
                R.id.main_function_draw_iv
                // R.id.main_function_mat_iv
        ));

        functionConstantList = new ArrayList<>(Arrays.asList(
                PtuUtil.EDIT_CUT,
                PtuUtil.EDIT_TEXT,
                PtuUtil.EDIT_TIETU,
                PtuUtil.EDIT_DRAW
                //PtuUtil.EDIT_MAT
        ));

        drawableList = new ArrayList<>(Arrays.asList(
                Util.getDrawable(R.mipmap.edit).mutate(),
                Util.getDrawable(R.mipmap.text).mutate(),
                Util.getDrawable(R.mipmap.tietu).mutate(),
                Util.getDrawable(R.mipmap.draw).mutate()
                //Util.getDrawable(R.mipmap.mat).mutate()
        ));

        chosenDrawableList = new ArrayList<>(Arrays.asList(
                Util.getMyShosenIcon(R.mipmap.edit),
                Util.getMyShosenIcon(R.mipmap.text),
                getStateDrawable(Util.getDrawable(R.mipmap.tietu).mutate(), getStateList(), PorterDuff.Mode.MULTIPLY),
                Util.getMyShosenIcon(R.mipmap.draw))
              /*getStateDrawable(Util.getDrawable(R.mipmap.mat).mutate(),
                        getStateList(), PorterDuff.Mode.SRC_IN)*/
        );
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
            chosedId = -1;
            initView(view);
        }
        return view;
    }

    private void initView(View view) {
        for (int i = 0; i < layoutIdList.size(); i++) {
            final int id = i;
            ViewGroup li = (ViewGroup) view.findViewById(layoutIdList.get(i));
            layoutList.add(li);
            imageList.add((ImageView) view.findViewById(imageIdList.get(i)));
            li.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* if(id!=1&&id!=2){
                        Toast.makeText(mContext,"暂未实现此功能,敬请期待！",Toast.LENGTH_SHORT).show();
                        return;
                    }*/
                    imageList.get(id).setImageDrawable(chosenDrawableList.get(id));
                    chosedId = id;
                    listen.switchFragment(functionConstantList.get(id));
                }
            });
        }
    }

    /**
     *
     *
     */
    public void eraseChosenColor() {
        if (chosedId != -1) {
            imageList.get(chosedId).setImageDrawable(drawableList.get(chosedId));
            chosedId = -1;
        }
    }
}
