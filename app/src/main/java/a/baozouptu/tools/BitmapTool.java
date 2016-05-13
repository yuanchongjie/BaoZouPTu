package a.baozouptu.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;

import a.baozouptu.dataAndLogic.AllDate;

/**
 * 用于获取图片的bitmap，并且将其缩放到和合适的大小
 *
 * @author acm_lgc
 *
 */
public class BitmapTool {

	private BitmapFactory.Options optsa = new BitmapFactory.Options();

	/**
	 * 缩放路径下的图片 ，返回其Bitmap对象
	 *
	 * @param path
	 *            String 图片，
	 * @return Bitmap 路径下适应大小的图片
	 */
	public Bitmap charge(String path) {
		Bitmap bm = null;

		optsa.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(path, optsa);
		float width = optsa.outWidth, height = optsa.outHeight;

		optsa.inJustDecodeBounds=false;
		/** 不同尺寸图片的缩放比例 */
		optsa.inSampleSize =(int)(Math.min(height,width)/(AllDate.screenWidth/3));
		bm = BitmapFactory.decodeFile(path, optsa);
		return bm;
	}
}