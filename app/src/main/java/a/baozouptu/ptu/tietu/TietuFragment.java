package a.baozouptu.ptu.tietu;

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
import a.baozouptu.chosePicture.ChosePictureActivity;
import a.baozouptu.base.dataAndLogic.AllDate;
import a.baozouptu.base.dataAndLogic.AsyncImageLoader3;
import a.baozouptu.base.dataAndLogic.MyDatabase;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.BaseFunction;
import a.baozouptu.ptu.repealRedo.StepData;

/**
 * Created by Administrator on 2016/7/1.
 */
public class TietuFragment extends Fragment implements BaseFunction {
    private static String TAG="TietuFragment";
    private FloatImageView floatImageView;
    Context mContext;
    List<String> tietuPaths = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerAdapter tietuAdapter;
    private LinearLayout more;

    public void setFloatImageView(FloatImageView floatImageView) {
        this.floatImageView = floatImageView;
    }

    private void loadTietuPath() {
        tietuPaths.clear();
        MyDatabase mDB = MyDatabase.getInstance(mContext);
        try {
            mDB.queryAllUsedPic(tietuPaths);
            mDB.queryAllFrequentlyPic(tietuPaths);
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

        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tietu, container, false);

        tietuAdapter = new RecyclerAdapter(mContext, tietuPaths);
        tietuAdapter.setOnItemClickListener(new RecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                floatImageView.setBitmapAndInit(data);
                int clickPosition=tietuPaths.indexOf(data);
                int lastPosition= layoutManager.findLastVisibleItemPosition();
                if(clickPosition==lastPosition){//将下一个隐藏的item移出来
                    int[] location = new  int[2] ;
                    view.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
                    recyclerView.smoothScrollBy(view.getWidth()+location[0]+view.getWidth()-more.getLeft()+10,0);
                }
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_list_tietu);
        recyclerView.setBackgroundColor(Color.BLACK);
        layoutManager = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tietuAdapter);

        more = (LinearLayout) view.findViewById(R.id.function_tietu_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,ChosePictureActivity.class);
                intent.setAction("tietu");
                startActivityForResult(intent,1);
            }
        });
        setOnclick();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Util.P.le(TAG);
        String path=data.getStringExtra("picPath");
        floatImageView.setBitmapAndInit(path);
    }

    private void setOnclick() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            AsyncImageLoader3 imageLoader = AsyncImageLoader3.getInstatnce();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        tietuAdapter.setScroll(false);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        tietuAdapter.setScroll(true);
                        imageLoader.cancelLoad();//取消解析，提交的任务还没有执行的就不执行了
                        break;
                }
            }

            AsyncImageLoader3.ImageCallback imageCallback = new AsyncImageLoader3.ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, ImageView image, int position, String imageUrl) {
                    if (image != null) {
                        image.setImageBitmap(imageBitmap);
                    }
                }
            };
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
        return floatImageView.getSourceBitmap() ;
    }

    @Override
    public StepData getResultData(float ratio) {
        return floatImageView.getResultData(ratio);
    }

    @Override
    public void releaseResource() {
        floatImageView.releaseResourse();
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
 * Created by liuguicen on 2016/6/17.
 * @description
 */
class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {

    private boolean isScroll = false;

    private final List<String> tietuPaths;
    AsyncImageLoader3 imageLoader = AsyncImageLoader3.getInstatnce();
    AsyncImageLoader3.ImageCallback imageCallback = new AsyncImageLoader3.ImageCallback() {
        @Override
        public void imageLoaded(Bitmap imageDrawable, ImageView image, int position, String imageUrl) {
            //if (image != null) {
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
                R.layout.item_list_tietu_icon, parent, false);
        itemView.setPadding(5, 0, 5, 0);

        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(AllDate.screenWidth / 5, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setTag("image");
        imageView.setOnClickListener(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        itemView.addView(imageView);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.iv.setTag(tietuPaths.get(position));

        Bitmap cachedImage = null;
        cachedImage = imageLoader.getBitmap(tietuPaths.get(position));//从缓存中获取
        if (cachedImage != null) {
            holder.iv.setImageBitmap(cachedImage);
        }else if (cachedImage == null&&!isScroll) {//图片存在，而且处于非滑动状态，从sd卡获取
            imageLoader.loadBitmap(tietuPaths.get(position), holder.iv,
                    position, imageCallback, AllDate.screenWidth / 5);
        }else//获取替代图片
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

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

}
