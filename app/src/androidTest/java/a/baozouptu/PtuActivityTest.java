/*
package a.baozouptu;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Environment;
import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import a.baozouptu.ptu.PtuActivity;

*/
/**
 * Created by LiuGuicen on 2017/1/2 0002.
 *//*

public class PtuActivityTest extends Instrumentation {

    private Intent testIntent;

    public PtuActivityTest() {
        super(PtuActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testIntent = new Intent(getInstrumentation().getTargetContext(),PtuActivity.class);
        String picPath= Environment.getExternalStorageState()+"test.jpg";
        testIntent.putExtra("pic_path",picPath);
    }
    @MediumTest
    public void test_getPicPath(){
        startActivity(testIntent,null,null);
        Log.e("图片路径获取到了",getActivity().getIntent().getStringExtra("pic_path"));
        assertNotNull(getActivity().getIntent().getStringExtra("pic_path"));
    }

}
*/
