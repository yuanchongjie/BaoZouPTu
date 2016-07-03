package a.baozouptu.control;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.dataAndLogic.AllDate;
import a.baozouptu.dataAndLogic.AsyncImageLoader3;
import a.baozouptu.dataAndLogic.MyDatabase;
import a.baozouptu.view.FloatImageView;

/**
 * Created by Administrator on 2016/7/1.
 */
public class TietuFragment extends Fragment {
    private FloatImageView floatImageView;
    Context mContext;
    List<String> tietuPaths = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    public void setFloatImageView(FloatImageView floatImageView) {
        this.floatImageView = floatImageView;
    }
    private void loadTietuPath() {
        MyDatabase mDB = MyDatabase.getInstance(mContext);
        try {
            mDB.quaryAllUsedPic(tietuPaths);
            mDB.quaryAllUsualyPic(tietuPaths);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDB.close();
        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        loadTietuPath();
        floatImageView.setBitmapAndInit(tietuPaths.get(0));
        super.onCreate(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tietu, container, false);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.function_tietu_more);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowTieTuActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(mContext, tietuPaths);
        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                floatImageView.setBitmapAndInit(data);
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_list_tietu);
        recyclerView.setBackgroundColor(Color.BLACK);
        layoutManager = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        setOnclick();
        return view;
    }

    private void setOnclick() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            AsyncImageLoader3 imageLoader = AsyncImageLoader3.getInstatnce();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        showAdjacentPic();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        imageLoader.cancelLoad();//取消解析，提交的任务还没有执行的就不执行了
                        break;
                }
            }

            AsyncImageLoader3.ImageCallback imageCallback = new AsyncImageLoader3.ImageCallback() {
                public void imageLoaded(Bitmap imageBitmap, ImageView image, int position, String imageUrl) {
                    if (image != null && position == (int) image.getTag()) {
                        image.setImageBitmap(imageBitmap);
                    }
                }
            };

            private void showAdjacentPic() {
                int first = layoutManager.findFirstVisibleItemPosition();
                int last = layoutManager.findLastVisibleItemPosition();
                for (int position = first; position <= last; position++) {
                    String path = tietuPaths.get(position);
                    final ImageView ivImage = (ImageView) recyclerView.findViewWithTag(position);
                    imageLoader.loadBitmap(path, ivImage, position, imageCallback);
                }
            }
        });
    }
}

/**
 * 其中的ImageView iv会放入路径
 */
class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView iv;

    public MyViewHolder(View itemView) {
        super(itemView);
        iv = (ImageView) itemView.findViewWithTag("image");
    }
}

/**
 * Created by Administrator on 2016/6/17.
 */
class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
    private final List<String> tietuPaths;
    AsyncImageLoader3 imageLoader = AsyncImageLoader3.getInstatnce();
    AsyncImageLoader3.ImageCallback imageCallback = new AsyncImageLoader3.ImageCallback() {
        @Override
        public void imageLoaded(Bitmap imageDrawable, ImageView image, int position, String imageUrl) {
            //if (image != null && position == (int) image.getTag()) {
            image.setImageBitmap(imageDrawable);
            //}
        }
    };
    private Context mContext;

    public RecyclerAdapter(Context context, List<String> tietuPaths) {
        mContext = context;
        this.tietuPaths = tietuPaths;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout itemView = (FrameLayout) LayoutInflater.from(mContext).inflate(
                R.layout.list_item_tietu_founction, parent, false);

        itemView.setOnClickListener(this);

        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(AllDate.screenWidth / 5, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(100,100));
        imageView.setTag("image");
        itemView.addView(imageView);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.iv.setTag(tietuPaths.get(position));

        Bitmap cachedImage = null;
        cachedImage = imageLoader.getBitmap(tietuPaths.get(position));//从缓存中获取
        if (cachedImage == null && position <= 8) {//否则从内存中获取
            imageLoader.loadBitmap(tietuPaths.get(position), holder.iv, position, imageCallback);
        }
        if (cachedImage != null && holder.iv != null) {
            holder.iv.setImageBitmap(cachedImage);
        }else
            holder.iv.setImageResource(R.mipmap.instead_icon);
    }

    @Override
    public int getItemCount() {
        return tietuPaths.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
