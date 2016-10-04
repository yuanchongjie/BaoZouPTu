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
import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.dataAndLogic.AsyncImageLoader3;

/**
 * 使用继承BaseAdapter处理ListView的图片显示
 *
 * @author acm_lgc
 */
public class MyFileListAdapter extends BaseAdapter {
    Context mContext;
    List<String> picFileInfoList;

    List<String> representPicturePathList;
    AsyncImageLoader3 asyLoader3 = AsyncImageLoader3.getInstatnce();

    private LayoutInflater  layoutInflater;

    MyFileListAdapter(Context context,List<String> list, List<String> picPathList) {
        mContext=context;
        picFileInfoList = list;
        representPicturePathList = picPathList;
        layoutInflater = LayoutInflater
                .from(mContext);
    }



    @Override
    public int getCount() {
        return picFileInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public ImageView ivImage;
        public TextView ivText;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
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
        viewHolder.ivText.setText(picFileInfoList.get(position));
        // 这个地方主义，imageLoader启动了一个新线程获取图片到cacheImage里面，新线程运行，本线程也会运行，
        // 因为新线程耗时，所以本线程已经执行到后面了，先加载了一张预设的图片，然后这个新线程会使用handler类更新UI线程，
        // 妙啊！
        Bitmap cacheBitmap = asyLoader3.loadBitmap(
                representPicturePathList.get(position), viewHolder.ivImage, position,
                new AsyncImageLoader3.ImageCallback() {
                    public void imageLoaded(Bitmap imageDrawable,
                                            ImageView image, int poition, String imageUrl) {
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
