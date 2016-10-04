package a.baozouptu.ptu.saveAndShare;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 其中的ImageView iv会放入路径
 */
class ShareViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView title;

    public ShareViewHolder(View itemView) {
        super(itemView);
        icon = (ImageView) itemView.findViewWithTag("icon");
        title = (TextView) itemView.findViewWithTag("title");
    }
}
