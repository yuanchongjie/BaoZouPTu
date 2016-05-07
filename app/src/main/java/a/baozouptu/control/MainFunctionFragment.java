package a.baozouptu.control;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import a.baozouptu.R;
import a.baozouptu.tools.P;
import a.baozouptu.view.HorizontalListView;

/**
 * Created by Administrator on 2016/4/29.
 */
@TargetApi(Build.VERSION_CODES.M)
public class MainFunctionFragment extends Fragment{
    Context mcontext;

    private View view;
    private Listen listen;
    public interface Listen{
        void changeFragment(String function);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mcontext=getActivity();
        listen=(Listen)mcontext;
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_function, container, false);
        LinearLayout textLin=(LinearLayout)view.findViewById(R.id.main_function_text);
        textLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen.changeFragment("text");
            }
        });
        return view;
    }
}
