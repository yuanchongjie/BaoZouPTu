package a.baozouptu.control;

import java.util.ArrayList;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.tools.FileTool;
import a.baozouptu.dataAndLogic.GridViewAdapter;

import a.baozouptu.tools.P;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class FilePictureActivity extends Activity {
	/** 进度条 */
	private ProgressDialog m_ProgressDialog = null;
	/**存放所有要显示图片的路径 List */
	public static List<String> lstFilePath = new ArrayList<String>();
	/**要显示图片的文件夹的路径*/
	String pictureFilePath;

	/**
	 * 获取所有需要显示的图片
	 * @param
	 */
	private void getValues() {
		lstFilePath.clear();
		FileTool fileTool = new FileTool();
		fileTool.ListFiles(pictureFilePath, lstFilePath);
		P.le("FilePictureActivity.java 显示图片的总数",lstFilePath.size());
		// 获取最近修改过的图片
		runOnUiThread(returnRes);// 表示强制这个线程在UI线程之前启动，
	}

	/**
	 * Called when the activity is first created.
	 * 过程描述：启动一个线程获取所有图片的路径，再启动一个子线程设置好GridView，而且要求这个子线程必须在ui线程之前启动
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_drawer_file_list);
		pictureFilePath =getIntent().getStringExtra("path");
		P.le(1.3,pictureFilePath);
		Thread thread = new Thread(null, new Runnable() {
			public void run() {
				getValues();
			}
		}, "MagentoBackground");
		thread.start();

		m_ProgressDialog = ProgressDialog.show(FilePictureActivity.this, "请稍后",
				"数据读取中...", true);
	}

	/**
	 * @category 并利用lstFilePath设置gridview内容了
	 *
	 */
	private Runnable returnRes = new Runnable() {
		public void run() {

			GridView gridview = (GridView) findViewById(R.id.gv_photolist);
			GridViewAdapter iadapter = new GridViewAdapter(
					FilePictureActivity.this, lstFilePath);
			gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					Intent intent=new Intent(FilePictureActivity.this,PTuActivity.class);
					intent.putExtra("path", lstFilePath.get(position));
					startActivity(intent);
				}
			});
			gridview.setAdapter(iadapter);
			m_ProgressDialog.dismiss();// 表示此处开始就解除这个进度条Dialog，应该是在相对起始线程的另一个中使用
		}
	};
}
