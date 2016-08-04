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
import a.baozouptu.ptu.BaseFunction;
import a.baozouptu.ptu.repealRedo.StepData;

/**
 * Created by Administrator on 2016/7/26.
 */
public class MatFragment extends Fragment implements BaseFunction {
    private Context mContext;
    private LinearLayout shape;
    private LinearLayout mear;
    private LinearLayout pen;
    private MatView matView;

    public MatFragment(Context context) {
        super();
        mContext = context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_mat,null);
        pen = (LinearLayout)view.findViewById(R.id.mat_pen);
        mear = (LinearLayout)view.findViewById(R.id.mat_smear);
        shape = (LinearLayout)view.findViewById(R.id.mat_shape);
        mContext=getActivity();
        setCkick();
        return view;
    }

    private void setCkick() {
        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matView.startDrawLine();
            }
        });
        mear.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public void repeal() {

    }

    @Override
    public void redo() {

    }

    @Override
    public Bitmap getResultBm(float ratio) {
        return null;
    }

    @Override
    public StepData getResultData(float ratio) {
        return null;
    }

    @Override
    public void releaseResourse() {
        matView.releaseResource();
    }

    public MatView createMatView(Rect bound,Bitmap bitmap) {
        matView = new MatView(mContext,bound);
        matView.setBitmapAndInit(bitmap,bound.width(),bound.height());
        return matView;
    }
}
