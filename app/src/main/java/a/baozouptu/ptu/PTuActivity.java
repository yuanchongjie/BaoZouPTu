package a.baozouptu.ptu;

import a.baozouptu.myCodeTools.P;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import a.baozouptu.R;
public class PTuActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ptu);
		Intent intent = getIntent();
		P.le("PTuActivity.onCreat()","到达");
		if(intent==null)P.le("PTuActivity.onCreat()","intent出现空指针");
		String path=intent.getStringExtra("path");
		PTuView pTuView=(PTuView)findViewById(R.id.ptu_view);
		pTuView.setBitmap(path);
	}
}
