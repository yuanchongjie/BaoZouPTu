package a.baozouptu.chosePicture;

import java.util.ArrayList;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.myCodeTools.P;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.chosePicture.AsyncImageLoader3.ImageCallback;

public class ShowFileActivity extends Activity {

	/** 每个文件夹代表图片的路径*/
	List<String> representPicturePath = new ArrayList<String>();
	/** 每个文件夹下图片的数量 */
	List<Integer> pictureNumberInFile = new ArrayList<Integer>();
	/** 每个文件的信息*/
	List<String> pictureFileInfo = new ArrayList<String>();
	/** 每个文件路径 */
	List<String> pictureFilePath = new ArrayList<String>();
	ListView listView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		P.le(12,System.currentTimeMillis());
		P.le("start secondActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_file);
		listView= (ListView) findViewById(R.id.picture_file_list);
		//点击文件夹，Activity跳转显示里面的所有图片
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent= new Intent(ShowFileActivity.this,FilePictureActivity.class);
				intent.putExtra("path", pictureFilePath.get(position));
				startActivity(intent);
			}
		});
		
		
		
		P.le("ShowFileActivity.java 系统时间",System.currentTimeMillis());
		//根据inten获取几个东西的信息
		Intent intent = getIntent();
		getInfo(intent);
		//启动一个UI线程前面的线程加载ListView
		runOnUiThread(returnRes);
		//new Thread(returnRes).run();
	}
	/**
 * 将Intent之中包含的文件路径和张数取出，并得出相应的信息
 * @param intent
 */
	private void getInfo(Intent intent)
	{
		int[] tempNumber = intent.getIntArrayExtra("pictureNumber");

		P.le("文件数目 ",tempNumber.length);
		for (int i = 0; i < tempNumber.length; i++)
			pictureNumberInFile.add(tempNumber[i]);
		for (int i = 0; i < pictureNumberInFile.size(); i++) {
			representPicturePath.add(intent.getStringExtra(String.valueOf(i)));
		}
		for (int i = 0; i < pictureNumberInFile.size(); i++) {
			String picturePath=representPicturePath.get(i);
			String parentPath=picturePath.substring(0,picturePath.lastIndexOf('/'));
			pictureFilePath.add(parentPath);
			pictureFileInfo.add("    "
					+parentPath.substring(parentPath.lastIndexOf('/')+1,parentPath.length())+" ("
					+String.valueOf(pictureNumberInFile.get(i))
					+")");
		}
	}

	private Runnable returnRes = new Runnable() {
		public void run() {
			MyListAdapter myListAdapter = new MyListAdapter();
			P.le(12,System.currentTimeMillis());
			listView.setAdapter(myListAdapter);
			P.le(12,System.currentTimeMillis());
		}
	};
/**
 * 使用继承BaseAdapter处理ListView的图片显示
 * @author acm_lgc
 *
 */
	class MyListAdapter extends BaseAdapter {
		Context context;
		AsyncImageLoader3 asyLoader3 = new AsyncImageLoader3();

		private LayoutInflater layoutInflater = LayoutInflater
				.from(ShowFileActivity.this);

		@Override
		public int getCount() {
			P.le("------", representPicturePath.size());
			return representPicturePath.size();
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

/**
 * 根据屏幕大小选取对应的布局文件
 * @param viewHolder 装载布局逐渐的容器类
 * @return
 */
		View setView(ViewHolder viewHolder) {
			View convertView;
			if (Date.screenWidth < 660) {
				P.le("加载了Item", 660);
				convertView = layoutInflater.inflate(
						R.layout.picture_file_400to660, null);
				viewHolder.ivImage = (ImageView) convertView
						.findViewById(R.id.represent_picture1);
				viewHolder.ivText = (TextView) convertView
						.findViewById(R.id.info_of_file1);
			} else if (Date.screenWidth <= 840) {
				P.le("加载了Item", 720);
				convertView = layoutInflater.inflate(
						R.layout.picture_file_660to840, null);
				viewHolder.ivImage = (ImageView) convertView
						.findViewById(R.id.represent_picture2);
				viewHolder.ivText = (TextView) convertView
						.findViewById(R.id.info_of_file2);
			} else if (Date.screenWidth <= 1020) {
				P.le("加载了Item", 1020);
				convertView = layoutInflater.inflate(
						R.layout.activity_picture_file, null);
				viewHolder.ivImage = (ImageView) convertView
						.findViewById(R.id.represent_picture3);
				viewHolder.ivText = (TextView) convertView
						.findViewById(R.id.info_of_file3);
			} else {
				P.le("加载了Item", 1080);
				convertView = layoutInflater.inflate(
						R.layout.picture_file_1020to1200, null);
				viewHolder.ivImage = (ImageView) convertView
						.findViewById(R.id.represent_picture4);
				viewHolder.ivText = (TextView) convertView
						.findViewById(R.id.info_of_file4);
			}
			return convertView;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			final ViewHolder viewHolder;

			if (convertView == null) {// 如果gridView的子项目为空，那么建立这个子项目
				viewHolder = new ViewHolder();
				convertView=setView(viewHolder);
				convertView.setTag(viewHolder);// setTeg是往view组件中添加一个任意的数据，以后可以随时取出
			} else {// 先前已将这个convertView的tag设置为ViewHolder，现在直接取出即可
				viewHolder = (ViewHolder) convertView.getTag();
			}
			P.le("装载了ViewHolder");
			viewHolder.ivText.setText(pictureFileInfo.get(position));
			P.le("MylistAdapter 设置了文字");
			// 这个地方主义，imageLoader启动了一个新线程获取图片到cacheImage里面，新线程运行，本线程也会运行，
			// 因为新线程耗时，所以本线程已经执行到后面了，先加载了一张预设的图片，然后这个新线程会使用handler类更新UI线程，
			// 妙啊！
			P.le("bitmap的路径: ",representPicturePath.get(position));
			Bitmap cacheBitmap = asyLoader3.loadBitmap(
					representPicturePath.get(position), viewHolder.ivImage,
					new ImageCallback() {
						public void imageLoaded(Bitmap imageDrawable,
								ImageView image, String imageUrl) {
							image.setImageBitmap(imageDrawable);
						}
					});
			if (cacheBitmap == null) {
				viewHolder.ivImage.setImageResource(R.mipmap.icon1);
			} else {
				viewHolder.ivImage.setImageBitmap(cacheBitmap);
			}
			return convertView;
		}
	};
}
