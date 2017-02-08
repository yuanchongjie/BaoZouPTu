package a.baozouptu.ptu.tietu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

import a.baozouptu.R;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.dataAndLogic.AsyncImageLoader3;


/**
 * Created by liuguicen on 2016/6/17.
 *
 * @description
 */
class TietuRecyclerAdapter extends RecyclerView.Adapter<TietuRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    /**
     * 其中的ImageView iv会放入路径
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewWithTag("image");
        }
    }

    private boolean isScroll = false;

    private final List<Integer> tietuIds;
    private Context mContext;

    TietuRecyclerAdapter(Context context, List<Integer> tietuIds) {
        mContext = context;
        this.tietuIds = tietuIds;
    }

    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Integer data);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout itemView = (FrameLayout) LayoutInflater.from(mContext).inflate(
                R.layout.item_list_tietu_icon, parent, false);
        itemView.setPadding(5, 0, 5, 0);

        ImageView imageView = new ImageView(mContext);
        imageView.setBackgroundColor(0xffffffff);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(AllData.screenWidth / 5, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setTag("image");
        imageView.setOnClickListener(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        itemView.addView(imageView);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.iv.setTag(tietuIds.get(position));
       // AsyncImageLoader3 imageLoader = AsyncImageLoader3.getInstance();
       // Bitmap bitmap = imageLoader.getBitmap(tietuIds.get(position));
        Bitmap bitmap=BitmapFactory.decodeResource(mContext.getResources(),tietuIds.get(position));
        if (bitmap != null)
            holder.iv.setImageBitmap(bitmap);
        else {
            holder.iv.setImageResource(R.mipmap.instead_icon);
           // imageLoader.loadBitmap(tietuIds.get(position), holder.iv, position, imageCallback, AllData.screenWidth / 3);
        }
    }

    private AsyncImageLoader3.ImageCallback imageCallback = new AsyncImageLoader3.ImageCallback() {
        @Override
        public void imageLoaded(Bitmap imageDrawable, ImageView image, int position, String imageUrl) {
            if (image != null && position == (int) image.getTag())
                if (imageDrawable != null)
                    image.setImageBitmap(imageDrawable);
        }
    };

    @Override
    public int getItemCount() {
        return tietuIds.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    void setScroll(boolean scroll) {
        isScroll = scroll;
    }

}
