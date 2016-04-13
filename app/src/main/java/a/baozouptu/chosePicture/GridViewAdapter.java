package a.baozouptu.chosePicture;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import a.baozouptu.R;
import a.baozouptu.chosePicture.AsyncImageLoader3.ImageCallback;

/**
 * 自定义GridView，用于显示所有图片，采用相应的处理，防止内存溢出和显示错乱
 *
 * @author acm_lgc
 *
 */
public class GridViewAdapter extends BaseAdapter {
	/**
	 * 布局形成器
	 */
	private LayoutInflater layoutInflater;
	/**
	 * 图片的路径
	 */
	private List<String> imgUrls;
	/**
	 * 加载图片的类
	 */
	private AsyncImageLoader3 imageLoader;
/**
 *
 */
	GridView mGridView;/**
	 *
	 * @param context
	 * @param imgUrls   要显示在GridView上的所有 图片的路径
	 */
	public GridViewAdapter(Context context, List<String> imgUrls) {
		layoutInflater = LayoutInflater.from(context);
		this.imgUrls = imgUrls;
		imageLoader = new AsyncImageLoader3();
	}

	@Override
	public int getCount() {
		return imgUrls.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 注意这里的关系，convertView是GridView实际要显示的子布局View，View它的里面又有一个tag可以存放任意类型的数据，
	 * 然后又利用了一个ViewHolder类，这个类里面持有了一个ImageView，而convertView布局，将它的
	 * ImageView的引用的给了ViewHolder，然后又把Viewholder放到了自己的tag里面。
	 *
	 * @category
	 *           过程描述：在GridView进行滚动时，会检测convertView，如果不为空，那么按上述步骤建立它以及viewHolder，
	 *           否则从convertView里面取出ViewHolder
	 *           ，然后从ViewHolder里面取出它持有的View，再往里面加入相应的图片，
	 *           然后利用含有LRUcache的容器imageLoader
	 *           .loadBitmap，加速，并且避免内存溢出，将图片加载到VeiwHolder里的
	 *           View上面，应为View本来是convertView里面View的引用
	 *           ，所以Bitmap实际上加载的了ConvertView上面，convertView已经加载了相应的信息，
	 *           最后返回convertView即可。
	 */
	/* （非 Javadoc）
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View holder, ViewGroup parent) {
		if(mGridView==null) mGridView=(GridView)parent;

		final ViewHolder setter;
		String path = imgUrls.get(position);
		if (holder == null) {// 如果gridView的子项目为空，那么建立这个子项目
			setter = new ViewHolder();
			holder = layoutInflater.inflate(
						R.layout.photolist_item, null);
			setter.ivImage = (ImageView) holder
						.findViewById(R.id.iv_photolist_image);
			setter.ivImage.setTag(position);
			holder.setTag(setter);
			// setTeg是往view组件中添加一个任意的数据，以后可以随时取出
		} else {// 先前已将这个convertView的tag设置为ViewHolder，现在直接取出即可
			setter=(ViewHolder)holder.getTag();
			setter.ivImage.setTag(position);
		}
		// 这个地方主义，imageLoader启动了一个新线程获取图片到cacheImage里面，新线程运行，本线程也会运行，
		// 因为新线程耗时，所以本线程已经执行到后面了，先加载了一张预设的图片，然后这个新线程会使用handler类更新UI线程， 妙啊！
		Bitmap cachedImage = imageLoader.loadBitmap(path, setter.ivImage,position,
				new ImageCallback() {
					public void imageLoaded(Bitmap imageBitmap,
											ImageView image, int position,String imageUrl) {
						Log.e("old position"+position,"new position"+(int)image.getTag());
						if(setter.ivImage!=null&&position==(int)image.getTag()) {
							setter.ivImage.setImageBitmap(imageBitmap);
						}
						//Log.e("position: " + pi, "bitmapId:" + imageBitmap.getGenerationId()
						//+"\n"+"imageView"+setter.ivImage.hashCode());
					}
				});
		if (cachedImage != null&&setter.ivImage!=null) {
			Log.e("position: "+position,"bitmapId:"+cachedImage.getGenerationId()+"\nimageView" + setter.ivImage.hashCode());
			setter.ivImage.setImageBitmap(cachedImage);
		} else if(setter.ivImage!=null) {
			setter.ivImage.setImageResource(R.mipmap.icon1);
		}
		return holder;// 返回最终ListView的子项目View
	}

	class ViewHolder {
		/**
		 * 图片
		 */
		public volatile ImageView ivImage;
	}
}