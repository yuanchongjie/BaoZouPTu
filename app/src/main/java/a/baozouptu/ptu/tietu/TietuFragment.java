package a.baozouptu.ptu.tietu;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.nfc.Tag;
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
import java.util.Iterator;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.dataAndLogic.AsyncImageLoader3;
import a.baozouptu.base.dataAndLogic.MyDatabase;
import a.baozouptu.base.util.Util;
import a.baozouptu.chosePicture.ChosePictureActivity;
import a.baozouptu.ptu.BaseFunction;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TietuStepData;
import a.baozouptu.ptu.view.PtuView;

/**
 * Created by Administrator on 2016/7/1.
 */
public class TietuFragment extends Fragment implements BaseFunction {
    private static String TAG = "TietuFragment";
    private TietuFrameLayout tietuLayout;
    Context mContext;
    List<String> tietuPaths = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerAdapter tietuAdapter;
    private LinearLayout more;

    private PtuView ptuView;
    private RepealRedoManager<StepData> mRpealRedoList;

    public void setTietuLayout(TietuFrameLayout tietuLayout) {
        this.tietuLayout = tietuLayout;
        tietuLayout.setOnTietuRemoveListener(
                new TietuFrameLayout.TietuChangeListener() {
                    @Override
                    public void onTietuRemove(FloatImageView view) {
                        removeFloatImageView(view);
                    }
                });
    }

    private void loadTietuPath() {
        tietuPaths.clear();
        MyDatabase mDB = MyDatabase.getInstance(mContext);
        try {
            mDB.queryAllUsedPic(tietuPaths);
            mDB.queryAllPreferPic(tietuPaths);
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
        mRpealRedoList=new RepealRedoManager<>(20);
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
                /*
//                处理设置贴图失败的情况，暂不实现
                boolean flag=false;
                while(!floatImageView.setBitmapAndInit(data)&&tietuPaths.size()>0)
                {
                    flag=true;
                    int id=tietuPaths.indexOf(data);
                    tietuPaths.remove(data);
                    if(tietuPaths.size()==0){
                        ((ViewGroup)floatImageView.getParent()).removeView(floatImageView);
                        Toast.makeText(mContext,"贴图加载失败了！",Toast.LENGTH_SHORT).show();
                        tietuAdapter.notifyDataSetChanged();
                        return;
                    }
                    else{
                        data=tietuPaths.get(id%tietuPaths.size());
                    }
                }
                if(flag) {
                    Toast.makeText(mContext,"贴图加载失败了！",Toast.LENGTH_SHORT).show();
                    tietuAdapter.notifyDataSetChanged();
                }*/

                addFloatImageView(data);

                int clickPosition = tietuPaths.indexOf(data);
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (clickPosition == lastPosition) {//将下一个隐藏的item移出来
                    int[] location = new int[2];
                    view.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
                    recyclerView.smoothScrollBy(view.getWidth() + location[0] + view.getWidth() - more.getLeft() + 10, 0);
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
                Intent intent = new Intent(mContext, ChosePictureActivity.class);
                intent.setAction("tietu");
                startActivityForResult(intent, 1);
            }
        });
        setOnclick();
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Util.P.le(TAG);
        String path = data.getStringExtra("pic_path");
        addFloatImageView(path);
    }

    /**
     * 添加一个tietu，
     *
     * @param path
     */
    private void addFloatImageView(String path) {
        Bitmap srcBitmap = TietuSizeControler.getSrcBitmap(getActivity(), path);
        FloatImageView floatImageView = new FloatImageView(mContext);
        floatImageView.setAdjustViewBounds(true);
        floatImageView.setImageBitmapAndPath(srcBitmap,path);
        FrameLayout.LayoutParams params = TietuSizeControler.getFeatParams(srcBitmap.getWidth(), srcBitmap.getHeight(),
                ptuView.getPicBound());
        tietuLayout.addView(floatImageView, params);
        Util.P.le(TAG, "添加贴图成功 "+"长宽比 "+params.width*1f/params.height);
    }

    private void removeFloatImageView(FloatImageView view) {
        tietuLayout.removeView(view);
    }

    private void setOnclick() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            AsyncImageLoader3 imageLoader = AsyncImageLoader3.getInstance();

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
        if (tietuLayout.getChildCount() > 0)
            tietuLayout.removeViewAt(tietuLayout.getChildCount() - 1);
    }

    @Override
    public void redo(StepData psd) {
        TietuStepData ttsd=(TietuStepData)psd;
        Iterator<StepData> iterator = ttsd.iterator();
        while (iterator.hasNext()) {
            StepData sd = iterator.next();
            Bitmap souceBitmap= TietuSizeControler.getBitmapInSize(mContext, sd.picPath,
                    (int)sd.boundRectInPic.width(),(int)sd.boundRectInPic.height());
            ptuView.addBitmap(souceBitmap,
                    sd.boundRectInPic, sd.rotateAngle);
        }
    }

    @Override
    public Bitmap getResultBm(float ratio) {
        return null;
    }

    /**
     * 获取结果，因为会有多个贴图，所以返回的 {@link TietuStepData} 里面放的是{@link StepData}的链表
     *
     * @return {@link StepData}
     */
    @Override
    public StepData getResultData(float ratio) {
        TietuStepData tsd = new TietuStepData(PtuActivity.EDIT_TIETU);
        int count = tietuLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            FloatImageView fiv = (FloatImageView) tietuLayout.getChildAt(i);
            StepData sd = new StepData(PtuActivity.EDIT_TIETU);
//获取每个tietu的范围
            RectF boundRectInPic = new RectF();
            String[] temp = PtuUtil.getLocationAtPicture(fiv.getLeft(), fiv.getTop(),
                    ptuView.getSrcRect(), ptuView.getDstRect());
            boundRectInPic.left = Float.valueOf(temp[0]);
            boundRectInPic.top = Float.valueOf(temp[1]);

            temp = PtuUtil.getLocationAtPicture(fiv.getRight(), fiv.getBottom(),
                    ptuView.getSrcRect(), ptuView.getDstRect());
            boundRectInPic.right = Float.valueOf(temp[0]);
            boundRectInPic.bottom = Float.valueOf(temp[1]);

            sd.boundRectInPic = boundRectInPic;
            sd.rotateAngle = fiv.getRotation();
            sd.picPath = fiv.getPicPath();
            tsd.addOneTietu(sd);
        }
        return tsd;

    }

    @Override
    public void releaseResource() {
        int count = tietuLayout.getChildCount();
        for (int i = count-1; i >= 0; i--) {
            ((FloatImageView)tietuLayout.getChildAt(i)).releaseResourse();
            tietuLayout.removeViewAt(i);
        }
    }

    public void setPtuView(PtuView ptuView) {
        this.ptuView = ptuView;
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
 *
 * @description
 */
class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {

    private boolean isScroll = false;

    private final List<String> tietuPaths;
    AsyncImageLoader3 imageLoader = AsyncImageLoader3.getInstance();
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
        imageView.setLayoutParams(new ViewGroup.LayoutParams(AllData.screenWidth / 5, ViewGroup.LayoutParams.MATCH_PARENT));
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
        } else if (cachedImage == null && !isScroll) {//图片存在，而且处于非滑动状态，从sd卡获取
            imageLoader.loadBitmap(tietuPaths.get(position), holder.iv,
                    position, imageCallback, AllData.screenWidth / 5);
        } else//获取替代图片
            holder.iv.setImageResource(R.mipmap.instead_icon);
    }

    public void onBindViewHolder(MyViewHolder holder, int position, List<Long> longs) {
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
