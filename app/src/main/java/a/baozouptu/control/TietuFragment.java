package a.baozouptu.control;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import a.baozouptu.R;
import a.baozouptu.dataAndLogic.AllDate;

/**
 * Created by Administrator on 2016/7/1.
 */
public class TietuFragment extends Fragment {
    Context mContext;
    RecyclerView tietuList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tietu, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tietuList = (RecyclerView) view.findViewById(R.id.recycle_list_tietu);
        TietuRecycleAdapter tietuAdapter=new TietuRecycleAdapter();
        tietuList.setAdapter(tietuAdapter);
        tietuList.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));

        LinearLayout linear=(LinearLayout)view.findViewById(R.id.function_tietu_more);
        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ShowTieTuActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    /**
     * Created by Administrator on 2016/6/17.
     */
    public class TietuRecycleAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout lineaner = new LinearLayout(mContext);
            lineaner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AllDate.screenWidth / 5));
            ImageView image = new ImageView(mContext);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AllDate.screenWidth / 5));
            image.setTag("image");
            lineaner.addView(image);

            MyViewHolder holder = new MyViewHolder(lineaner);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.iv.setImageResource(R.mipmap.icon1);
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewWithTag("image");
        }
    }
}
