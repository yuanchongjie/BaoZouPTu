package com.example.administrator.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/9.
 */
public class Activity2 extends AppCompatActivity {

    private List<String> mDatas;
    private RecyclerView recyclerView;
    private HomeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("启动成功","Activity2");
        recyclerView = (RecyclerView) findViewById(R.id.first_recycler_view);

        initData();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(mAdapter = new HomeAdapter());
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
    }
//从github上添加的内容
    private void initData() {
        mDatas = new ArrayList<>();
        for (int i = 'A'; i < 'Z'+50; i++) {
            mDatas.add("" + (char) i);
        }

    }
    //从github上面添加了一行

    /**
     * Created by Administrator on 2016/6/17.
     */
    public class HomeAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(Activity2.this).inflate(
                    R.layout.recycler_view_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }
    //从github的devolop添加了一行

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.id_num);
        }
    }
}
