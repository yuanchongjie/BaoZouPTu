package a.baozouptu.chosePicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import a.baozouptu.R;
import a.baozouptu.chosePicture.data.PicDirInfo;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.dataAndLogic.AsyncImageLoader;

/**
 * 使用继承BaseAdapter处理ListView的图片显示
 *
 * @author acm_lgc
 */
class MyFileListAdapter extends BaseAdapter {
    private final List<PicDirInfo> picDirInfos;
    private Context mContext;

    private AsyncImageLoader asyLoader3 = AsyncImageLoader.getInstance();

    private LayoutInflater layoutInflater;

    MyFileListAdapter(Context context, List<PicDirInfo> picDirInfos) {
        mContext = context;
        this.picDirInfos = picDirInfos;
        layoutInflater = LayoutInflater
                .from(mContext);
    }


    @Override
    public int getCount() {
        return picDirInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView ivImage;
        TextView ivText;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final int realPosition;
        if(position>=picDirInfos.size())
            realPosition=picDirInfos.size()-1;// TODO: 2017/3/10 0010 多线程+设计导致出问题，强行抑制，后期想办法改
        else
            realPosition=position;

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.item_list_picfile, null);

            viewHolder.ivImage = (ImageView) convertView
                    .findViewById(R.id.represent_picture);
            viewHolder.ivText = (TextView) convertView
                    .findViewById(R.id.info_of_file);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivText.setText(picDirInfos.get(realPosition).getPicNumInfo());
        // 这个地方主义，imageLoader启动了一个新线程获取图片到cacheImage里面，新线程运行，本线程也会运行，
        // 因为新线程耗时，所以本线程已经执行到后面了，先加载了一张预设的图片，然后这个新线程会使用handler类更新UI线程，
        // 妙啊！
        Bitmap cacheBitmap = asyLoader3.loadBitmap(
                picDirInfos.get(realPosition).getRepresentPicPath(), viewHolder.ivImage, realPosition,
                new AsyncImageLoader.ImageCallback() {
                    public void imageLoaded(Bitmap imageDrawable,
                                            ImageView image, int position, String imageUrl) {
                        image.setImageBitmap(imageDrawable);
                    }
                },
                AllData.screenWidth / 3);
        if (cacheBitmap == null) {
            viewHolder.ivImage.setImageResource(R.mipmap.icon);
        } else {
            viewHolder.ivImage.setImageBitmap(cacheBitmap);
        }

        return convertView;
    }

}
