package a.baozouptu.ptu.saveAndShare;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import a.baozouptu.R;
import a.baozouptu.common.util.Util;

/**
 * Created by liuguicen on 2016/9/8.
 *
 */
class ShareRecyclerAdapter extends RecyclerView.Adapter<ShareViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<ListDrawableItem> shareAcInfo;

    ShareRecyclerAdapter(Context context, List<ListDrawableItem> shareAcInfo) {
        mContext = context;
        this.shareAcInfo = shareAcInfo;
    }

    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, ListDrawableItem data);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.item_list_save_set_share, parent, false);
        itemView.setOnClickListener(this);
        ImageView icon = (ImageView) itemView.findViewById(R.id.save_set_item_share_icon);
        TextView title = (TextView) itemView.findViewById(R.id.save_set_item_share_title);

        icon.setTag("icon");
        title.setTag("title");

        return new ShareViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, int position) {
        holder.icon.setTag(shareAcInfo.get(position));
        holder.icon.setImageDrawable(shareAcInfo.get(position).getIcon());
        holder.title.setText(shareAcInfo.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return shareAcInfo.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            Util.P.le("recyclerView受到点击");
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,
                    (ListDrawableItem) ((LinearLayout)v).getChildAt(0).getTag());
        }
    }

    void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        Util.P.le("item受到点击0");
        this.mOnItemClickListener = listener;
    }
}
