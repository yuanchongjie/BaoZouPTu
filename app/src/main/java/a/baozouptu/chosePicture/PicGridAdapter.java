package a.baozouptu.chosePicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import a.baozouptu.R;
import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.dataAndLogic.AsyncImageLoader3;
import a.baozouptu.base.util.Util;

/**
 * Created by liuguicen on 2016/8/31.
 *
 * @description
 */
public class PicGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int STATIC_SHOW_NUMBER = 25;

    private final Context mContext;
    private final LayoutInflater layoutInflater;
    private final AsyncImageLoader3 imageLoader;
    public boolean isScrollWidthoutTouch;

    public List<String> getImagUrls() {
        return imagUrls;
    }

    private List<String> imagUrls;
    private final ProcessUsuallyPicPath usuallyProcessor;

    public static int ITEM = 1;
    public static int GROUP_HEADER = 2;


    AsyncImageLoader3.ImageCallback imageCallback = new AsyncImageLoader3.ImageCallback() {
        @Override
        public void imageLoaded(Bitmap imageDrawable, ImageView image, int position, String imageUrl) {

            if (image != null && position == (int) image.getTag()) {
                if (imageDrawable == null) {
                    image.setImageResource(R.mipmap.decode_failed_icon);
                } else
                    image.setImageBitmap(imageDrawable);
            }

        }
    };

    public void setList(List<String> usualyPicPathList) {
        imagUrls = usualyPicPathList;
    }

    public interface ItemClickListener {
        void onItemClick(ItemHolder itemHolder);
    }

    public interface LongClickListener {
        boolean onItemLongClick(ItemHolder itemHolder);
    }

    ItemClickListener clickListener;
    LongClickListener longClickListener;

    public void setClickListener(ItemClickListener clickListenner) {
        this.clickListener = clickListenner;
    }

    public void setLongClickListener(LongClickListener longClickListenner) {
        this.longClickListener = longClickListenner;
    }

    /**
     * @param context
     * @param imagUrls 要显示在GridView上的所有 图片的路径
     */
    public PicGridAdapter(Context context, List<String> imagUrls, ProcessUsuallyPicPath usuallyProcessor) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        this.imagUrls = imagUrls;
        imageLoader = AllData.imageLoader3;
        this.usuallyProcessor = usuallyProcessor;
        isScrollWidthoutTouch=false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM) {
            LinearLayout layout = creatItemLayout(parent);
            final ItemHolder itemHolder = new ItemHolder(layout);
            itemHolder.iv = creatItemImage(layout);
            itemHolder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(itemHolder);
                }
            });
            itemHolder.iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return longClickListener.onItemLongClick(itemHolder);
                }
            });
            return itemHolder;
        }
        if (viewType == GROUP_HEADER) {
            View view = layoutInflater.inflate(R.layout.pic_gird_group_header, parent, false);
            return new HeaderHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(imagUrls.get(position));
        Util.P.le("系统调用加载图片","position =  "+position+"isscrpll= "+isScrollWidthoutTouch);
        myBindViewHolder(holder, position);
    }

    public void myBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //如果是分组标题
        ItemHolder itemHolder = null;
        if (usuallyProcessor.isUsuPic(imagUrls)) {
            int headerValue = getHeaderValue(position);
            if (headerValue == 0) {
                ((HeaderHolder) holder).tv.setText(R.string.latest_use);
                return;
            } else if (headerValue == 1) {
                ((HeaderHolder) holder).tv.setText(R.string.recent_pic);
                return;
            } else if (headerValue == 2) {
                ((HeaderHolder) holder).tv.setText(R.string.prefer_pic);
                return;
            } else {
                itemHolder = (ItemHolder) holder;
            }
        } else {
            itemHolder = (ItemHolder) holder;
        }
        // 这个地方主义，imageLoader启动了一个新线程获取图片到cacheImage里面，新线程运行，本线程也会运行，
        // 因为新线程耗时，所以本线程已经执行到后面了，先加载了一张预设的图片，然后这个新线程会使用handler类更新UI线程， 妙啊！
        itemHolder.iv.setTag(position);
        String path = imagUrls.get(position);
        Bitmap cachedImage = null;
        cachedImage = imageLoader.getBitmap(path);
        if (cachedImage == null && (!isScrollWidthoutTouch||position <= STATIC_SHOW_NUMBER)) {
            imageLoader.loadBitmap(path, itemHolder.iv, position, imageCallback, AllData.screenWidth / 3);
        }
        if (cachedImage != null && itemHolder.iv != null) {
            itemHolder.iv.setImageBitmap(cachedImage);
        } else if (itemHolder.iv != null) {
            itemHolder.iv.setImageResource(R.mipmap.instead_icon);
        }
    }

    @Override
    public int getItemCount() {
        if (usuallyProcessor.isUsuPic(imagUrls))
            return imagUrls.size();
        return imagUrls.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (usuallyProcessor.isUsuPic(imagUrls) && getHeaderValue(position) >= 0) {
            return GROUP_HEADER;
        }
        return ITEM;
    }

    /**
     * 获取分组的值
     *
     */
    private int getHeaderValue(int position) {
        String path=imagUrls.get(position);
        if (path.equals(ProcessUsuallyPicPath.USED_FLAG))//存在使用过的图片
            return 0;
        if (path.equals(ProcessUsuallyPicPath.RECENT_FLAG))
            return 1;
        if (path.equals(ProcessUsuallyPicPath.PREFER_FLAG))
            return 2;
        return -1;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public ItemHolder(View itemView) {
            super(itemView);
        }
    }

    LinearLayout creatItemLayout(ViewGroup parent) {
        // 创建LinearLayout对象
        LinearLayout mLinearLayout = new LinearLayout(mContext);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, AllData.screenWidth / 3 - Util.dp2Px(12));
        layoutParams.setMargins(Util.dp2Px(1.5f), Util.dp2Px(1.5f), Util.dp2Px(1.5f),
                Util.dp2Px(1.5f));
        mLinearLayout.setLayoutParams(layoutParams);

        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setGravity(Gravity.CENTER);
        return mLinearLayout;
    }

    ImageView creatItemImage(LinearLayout linearLayout) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, AllData.screenWidth / 3 - 4);
        linearLayout.addView(imageView, mLayoutParams);
        return imageView;
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public HeaderHolder(View itemView) {
            super(itemView);
            itemView.setClickable(false);
            itemView.setLongClickable(false);
            tv = (TextView) itemView.findViewById(R.id.tv_pic_header_name);
        }
    }
}
