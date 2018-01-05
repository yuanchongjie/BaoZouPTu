package a.baozouptu.ptu.tietu.onlineTietu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import a.baozouptu.R;


/**
 * Created by liuguicen on 2016/6/17.
 */
public class TietuRecyclerAdapter extends RecyclerView.Adapter<TietuRecyclerAdapter.MyViewHolder>
        implements View.OnClickListener {

    private static final String TAG = "TietuRecyclerAdapter";

    TietuListProxy listProxy = new TietuListProxy();

    private Context mContext;

    public TietuRecyclerAdapter(Context context) {
        mContext = context;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Object data);
    }

    public void setTietuIds(List<Integer> tietuIds) {
        this.listProxy.setTietuIds(tietuIds);
    }

    public void setTietuMaterials(List<tietu_material> tietu_materials) {
        this.listProxy.setTietuMaterials(tietu_materials);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout itemView = (FrameLayout) LayoutInflater.from(mContext).inflate(
                R.layout.item_list_tietu_icon, parent, false);
        itemView.setPadding(5, 0, 5, 0);

        ImageView imageView = new ImageView(mContext);
        imageView.setBackgroundColor(0xffffffff);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(parent.getHeight(), ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setTag("image");
        imageView.setOnClickListener(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        itemView.addView(imageView);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.iv.setTag(listProxy.get(position));
        Object data = listProxy.get(position);
        Bitmap bitmap;
        if (data instanceof Integer) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), (int) data);
            if (bitmap != null)
                holder.iv.setImageBitmap(bitmap);
            else {
                holder.iv.setImageResource(R.mipmap.instead_icon);
            }
        } else {
            File tietuFile = PriorTietuManager.getLocalTietuFile((tietu_material) data);
            if (tietuFile != null) {//本地存在，村本地文件加载
                Picasso.with(mContext)
                        .load(tietuFile)
                        .into(holder.iv);
            } else {//本地不存在，从网络上下载
                Log.e(TAG, "picasso: 从网络下载了贴图");
                Picasso.with(mContext)
                        .load(((tietu_material) data).getUrl().getUrl())
                        .into(holder.iv);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listProxy.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


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
}

/**
 * 两个列表数据的访问控制，代理模式？
 */
class TietuListProxy {
    private List<Integer> tietuIds;
    private List<tietu_material> tietuMaterials;

    public List<Integer> getTietuIds() {
        return tietuIds;
    }

    public void setTietuIds(List<Integer> tietuIds) {
        this.tietuIds = tietuIds;
    }

    public List<tietu_material> getTietuMaterials() {
        return tietuMaterials;
    }

    public void setTietuMaterials(List<tietu_material> tietu_materials) {
        this.tietuMaterials = tietu_materials;
    }

    public Object get(int position) {
        if (position < tietuIds.size())
            return tietuIds.get(position);
        else return tietuMaterials.get(position - tietuIds.size());
    }

    public int size() {
        return tietuIds.size() + tietuMaterials.size();
    }
}
