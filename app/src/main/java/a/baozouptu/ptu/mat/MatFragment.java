package a.baozouptu.ptu.mat;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import a.baozouptu.R;
import a.baozouptu.common.util.Util;
import a.baozouptu.ptu.BaseFunction;
import a.baozouptu.ptu.repealRedo.StepData;

/**
 * Created by liuguicen on 2016/7/26.
 *
 * @description
 */
public class MatFragment extends Fragment implements BaseFunction {
    private static final String TAG = "MatFragment";
    private Context mContext;
    private LinearLayout shape;
    private LinearLayout smear;
    private LinearLayout pen;
    private LinearLayout rubber;
    private MatView matView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mat, null);
        mContext = getActivity();

        pen = (LinearLayout) view.findViewById(R.id.mat_pen);
        smear = (LinearLayout) view.findViewById(R.id.mat_smear);
        shape = (LinearLayout) view.findViewById(R.id.mat_shape);
        rubber = (LinearLayout) view.findViewById(R.id.mat_rubber);
        setClick();
        Util.P.le(TAG,"创建MatView完成");
        return view;
    }

    private void setClick() {
        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matView.startDrawLine();
            }
        });
        smear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matView.startSmear();
            }
        });
        shape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matView.startMatByShape();
            }
        });
        rubber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matView.startRubber();
            }
        });
    }

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
        matView.releaseResource();
    }

    public MatView createMatView(Context context,Rect bound, Bitmap bitmap) {
        matView = new MatView(context, bitmap, bound);
        matView.setBitmapAndInit(bitmap, bound);
        matView.setCanDoubleClick(false);
        matView.setCanLessThanScreen(false);
        return matView;
    }
}
