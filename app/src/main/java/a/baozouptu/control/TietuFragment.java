package a.baozouptu.control;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.dataAndLogic.AllDate;
import a.baozouptu.dataAndLogic.MyDatabase;
import a.baozouptu.tools.BitmapTool;
import a.baozouptu.tools.Util;

/**
 * Created by Administrator on 2016/7/1.
 */
public class TietuFragment extends Fragment {
    Context mContext;
    List<String> tietuPaths = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        loadTietuPath();
        super.onCreate(savedInstanceState);
    }

    private void loadTietuPath() {
        MyDatabase mDB = MyDatabase.getInstance(mContext);
        try {
            mDB.quaryAllUsedPic(tietuPaths);
            mDB.quaryAllUsualyPic(tietuPaths);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mDB.close();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tietu, container, false);

        LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.function_tietu_more);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,ShowTieTuActivity.class);
                startActivityForResult(intent,0);
            }
        });

        RecyclerView.Adapter recyclerAdapter=new RecyclerAdapter();
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycle_list_tietu);
        recyclerView.setBackgroundColor(Color.BLACK);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false));
        recyclerView.setAdapter(recyclerAdapter);
        return view;
    }

    /**
     * Created by Administrator on 2016/6/17.
     */
    public class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout itemView=(LinearLayout)LayoutInflater.from(mContext).inflate(
                    R.layout.list_item_tietu_founction, parent, false);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(AllDate.screenWidth/5, ViewGroup.LayoutParams.MATCH_PARENT));
            ImageView imageView=new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(AllDate.screenWidth/5, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setTag("image");
            itemView.addView(imageView);
            MyViewHolder holder = new MyViewHolder(itemView);
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
