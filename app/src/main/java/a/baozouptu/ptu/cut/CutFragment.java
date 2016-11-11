package a.baozouptu.ptu.cut;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import a.baozouptu.R;
import a.baozouptu.ptu.BaseFunction;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;

public class CutFragment extends Fragment implements BaseFunction{
    private String TAG="CutFragment";
    private Context mContext;
    private LinearLayout reset;
    private LinearLayout scale;
    private LinearLayout rotate;
    private LinearLayout reversal;
    private CutFrameView cutFrameView;
    private View view;

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
        return null;
    }

    @Override
    public void releaseResource() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cut, null);
        mContext = getActivity();

        rotate = (LinearLayout) view.findViewById(R.id.cut_rotate);
        scale = (LinearLayout) view.findViewById(R.id.cut_scale);
        reset = (LinearLayout) view.findViewById(R.id.cut_reset);
        reversal = (LinearLayout) view.findViewById(R.id.cut_reversal);
        setClick();
        ((PtuActivity)getActivity()).ptuView.setCanDoubleClick(false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((PtuActivity)getActivity()).ptuView.setCanDoubleClick(true);
    }

    private void setClick() {
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        scale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PtuActivity)getActivity()).ptuView.resetShow();
            }
        });
        reversal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow reversalWindow;
            }
        });
    }


    public void setRealRedoManager(RepealRedoManager realRedoManager){
    }

    public View createCutView(Context context,Rect totalBound,Rect picBound) {
        cutFrameView = new CutFrameView(context,picBound);
        return cutFrameView;
    }
}
