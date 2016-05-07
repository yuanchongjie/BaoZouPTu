package a.baozouptu.control;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import a.baozouptu.R;
import a.baozouptu.tools.P;

/**
 * Created by Administrator on 2016/5/1.
 */
public class AddTextFragment extends Fragment {
    Context mcontext;

    @Override
    public void onAttach(Context context) {
        mcontext = context;
        P.le(this, "onAttach" + this.getClass().getSimpleName());
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        P.le(this.getClass(), "onCreateView");
        View view = inflater.inflate(R.layout.fragment_add_text_function, container, false);
        return view;
    }

    @Override
    public void onDestroy() {
        P.le(this.getClass(), "onDeastory");
        super.onDestroy();
    }
}
