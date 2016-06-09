package com.example.administrator.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 用于获取图片的bitmap，并且将其缩放到和合适的大小
 *
 * @author acm_lgc
 *
 */
public class BitmapTool {

	private BitmapFactory.Options optsa = new BitmapFactory.Options();

	/**
	 * 将整个图片获取出来，不损失精度
	 * @return
     */
	public Bitmap getLosslessBitmap(String path) {
		BitmapFactory.Options optsa = new BitmapFactory.Options();
		optsa.inDither=true;
		optsa.inPreferQualityOverSpeed=true;
		optsa.inDensity=0;
		optsa.inTargetDensity=0;
		optsa.inScaled=false;
		return BitmapFactory.decodeFile(path,optsa);
	}
}