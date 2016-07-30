package a.baozouptu.ptu.control;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import a.baozouptu.R;

/**
 * Created by Administrator on 2016/4/29.
 */
@TargetApi(Build.VERSION_CODES.M)
public class MainFunctionFragment extends Fragment {
    Context mcontext;
    /**
     * 代表主功能的组件,剪切
     */
    private LinearLayout cutFunction;
    /**
     * 代表主功能的组件,文字
     */
    private LinearLayout textFunction;
    /**
     * 代表主功能的组件,贴图
     */
    private LinearLayout tietuFunction;
    /**
     * 代表主功能的组件,绘图
     */
    private LinearLayout huituFunction;
    /**
     * 代表主功能的组件,抠图
     */
    private LinearLayout koutuFunction;

    private View view;
    private Listen listen;

    public interface Listen {
        void switchFragment(String function);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mcontext = getActivity();
        listen = (Listen) mcontext;
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_function, container, false);
        initView(view);
        setOnClick();
        return view;
    }

    private void setOnClick() {
        cutFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen.switchFragment("cut");
            }
        });
        textFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen.switchFragment("text");
            }
        });
        tietuFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen.switchFragment("tietu");
            }
        });
        huituFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen.switchFragment("huitu");
            }
        });
        koutuFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen.switchFragment("koutu");
            }
        });

    }

    private void initView(View view) {
        cutFunction = (LinearLayout) view.findViewById(R.id.main_function_cut);
        textFunction = (LinearLayout) view.findViewById(R.id.main_function_text);
        tietuFunction = (LinearLayout) view.findViewById(R.id.main_function_tietu);
        huituFunction = (LinearLayout) view.findViewById(R.id.main_function_huitu);
        koutuFunction = (LinearLayout) view.findViewById(R.id.main_function_koutu);
    }
}
