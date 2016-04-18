package a.baozouptu.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import a.baozouptu.tools.Date;

/**
 * 用于获取图片的bitmap，并且将其缩放到和合适的大小
 *
 * @author acm_lgc
 *
 */
public class BitmapTool {
	/**
	 * 图片适配大小，150*100的大小
	 *
	 */
	private Bitmap change(String path) {

		Bitmap bm = null;

		BitmapFactory.Options optsa = new BitmapFactory.Options();

		optsa.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(path, optsa);
		float width = optsa.outWidth, height = optsa.outHeight;

		optsa.inJustDecodeBounds=false;
		/** 不同尺寸图片的缩放比例 */
		if (height  <= 150 || width  <= 100) {
			optsa.inSampleSize = 1;
		}
		else  {
			optsa.inSampleSize =(int)(Math.sqrt((height*width)/ Date.thumbnailSize));
		}
		bm = BitmapFactory.decodeFile(path, optsa);
		return bm;
	}

	/**
	 * 缩放路径下的图片 ，返回其Bitmap对象
	 *
	 * @param path
	 *            String 图片，
	 * @return Bitmap 路径下适应大小的图片
	 */
	public Bitmap charge(String path) {
		return change(path);
	}
}