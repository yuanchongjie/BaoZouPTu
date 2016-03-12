package a.baozouptu.ptu;

import a.baozouptu.myCodeTools.P;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PTuView extends View {

	/**
	 * 表示当前处理状态：缩放，移动等等
	 */
	static int CURRENT_STATUS = 0;
	/**
	 * 初始化状态
	 */
	static final int STATUS_INIT = 0;
	/**
	 * 移动状态
	 */
	static final int STATUS_MOVE = 1;
	/**
	 * 缩放状态
	 */
	static final int STATUS_ZOOM = 2;
	static final int STATUS_DRAW_PATH = 3;
	/**
	 * 最近的x的位置
	 */
	float lastX = -1;
	/**
	 * 最近的y的位置
	 */
	float lastY = -1;
	/**
	 * x的移动距离
	 */
	float totalTranX;
	/**
	 * y的移动距离
	 */
	float totalTranY = 0;
	/**
	 * 当前用于缩放两个手指的距离
	 */

	double currentDis;
	/**
	 * 最近一次用于缩放两手指间的距离
	 */
	double lastDis;
	/**
	 * 原图片的宽度
	 */
	int sourceWidth;
	/**
	 * 原图片的高度
	 */
	int sourceHeight;
	/**
	 * 中的缩放比例
	 */
	float totalRatio = 1f;
	/**
	 * 当前的缩放比例
	 */
	float currentRatio = 1f;
	/**
	 * context
	 */
	Context context;
	/**
	 * 原图片
	 */
	Bitmap sourceBitmap;
	/**
	 * 用于处理图片的矩阵
	 */
	Matrix matrix = new Matrix();
	/**
	 * 整个View的宽
	 */
	int width;
	/**
	 * 整个View的高
	 */
	int height;
	Paint paint= new Paint();
	Bitmap bitmapToDraw;
	Canvas drawCanvas;

	public PTuView(Context context, AttributeSet set) {
		super(context, set);
		this.context = context;
		CURRENT_STATUS=STATUS_INIT;
		paint.setColor(Color.RED);
		paint.setStrokeWidth(25);
		paint.setTextSize(25);
	}
	
/*
	public void saveNewBitmap(String path) {
		File file = new File(path);
		if (!file.exists())
			try { 
				FileOutputStream fileOutputStream = new FileOutputStream(
						file.getPath());
				bitmapToDraw.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream);
				fileOutputStream.close();
				Log.e("saveBmp is succseed", "yes!");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
*/

	class Point
	{
		public float x,y;
		Point(){
		}
	}
	Point startPoint=new Point();
	Point endPoint=new Point();
	/**
	 * 设置原图片
	 *
	 * @param Bitmap
	 *            从调用的ACtivity中传过来的bitmap
	 */
	Path path = new Path();
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				startPoint.x=event.getX();startPoint.y=event.getY();
				//myPath.startPath(path, event.getX(), event.getY());
				break;
			case MotionEvent.ACTION_MOVE:
				endPoint.x=event.getX();
				endPoint.y=event.getY();
				//myPath.addPoint(path,event.getX(), event.getY());
				CURRENT_STATUS=STATUS_DRAW_PATH;
				invalidate();
				break;
			default:	break;
		}
		return true;
	}

	public void setBitmap(String path2) {
		P.le("PTuActivity.setBitmap",path2+"到达");
		if(path2== null)P.le("PTuActivity.setBitmap","path2出现空指针");
		sourceBitmap= BitmapFactory.decodeFile(path2);

		if(sourceBitmap == null)P.le("PTuActivity.setBitmap","sourceBitmap出现空指针");
		sourceWidth = sourceBitmap.getWidth();
		sourceHeight = sourceBitmap.getHeight();
		totalRatio=1;
		if(sourceHeight<100||sourceWidth<60)
		{
			totalRatio=500/sourceHeight;
			sourceHeight=500;
			sourceWidth=300;
		}
		if(sourceWidth>1000||sourceWidth>600)
		{
			totalRatio=sourceHeight/1000;
			sourceHeight=1000;
			sourceHeight=600;
		}
	}

	/**
	 * 绘制，这里根据不同的当前状态来绘制图片CURRENT_STATUS
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		switch (CURRENT_STATUS) {
			case STATUS_INIT:
				init(canvas);
				break;
			case STATUS_DRAW_PATH:
				drawCanvas.drawLine(startPoint.x, startPoint.y,
						endPoint.x,endPoint.y, paint);
				startPoint.x=endPoint.x;
				startPoint.y=endPoint.y;
				break;
			default:
				break;
		}
		matrix.reset();
		canvas.drawBitmap(bitmapToDraw, matrix, null);//将底图绘制到View上面到
		P.le("PTuActivity.onDraw","到达");
	}
	public void init(Canvas canvas)
	{
		bitmapToDraw = Bitmap.createBitmap(sourceWidth, sourceHeight,
				Config.ARGB_8888);//创建一个空图做底图
		drawCanvas = new Canvas(bitmapToDraw);//设置drawCanvas为底图
		matrix.postScale(totalRatio,totalRatio);
		drawCanvas.drawBitmap(sourceBitmap, matrix, null);//将原图填充到底图上
		P.le("PTuActivity.init()","绘制了底图");
	}
}
