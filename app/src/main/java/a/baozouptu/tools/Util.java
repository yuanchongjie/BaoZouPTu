package a.baozouptu.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Util {
    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int dp2Px(float dp) {
        final float scale = MyApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int px2Dp(float px) {
        final float scale = MyApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 在mainifest中使用android:name=".MyApplication"，系统将会创建myapplication替代一般的application
     */
    public static class MyApplication extends Application {
        private static MyApplication mcontext;

        @Override
        public void onCreate() {
            // TODO Auto-generated method stub
            super.onCreate();
            mcontext = this;
        }

        public static Context getAppContext() {
            return mcontext;
        }
    }

    /**
     * �򻯴�����࣬
     * ���ǹ��������ӡ����
     *
     * @author acm_lgc
     */
    public static class P {
        public static void le(Object s) {
            Log.e(s.toString(), "------");
        }

        /**
         * @param s1 便于输出产生log的内和位置
         * @param s2
         */
        public static void le(Class s1, Object s2) {
            Log.e(s1.getSimpleName(), s2.toString());
        }

        public static void le(Object s1, Object s2) {
            Log.e(s1.toString(), s2.toString());
        }

        public void lgd(String s) {
            Log.d(s, "------");
        }

        public void lgd(String s1, String s2) {
            Log.d(s1, s2);
        }
    }

    /**
     * 只是测试时方便写代码的，正式的还是正式的书写
     */
    /**
     * 默认长的,系统context不为空
     *
     * @param s
     */
    public static void T(Object s) {
        if (MyApplication.getAppContext() != null)
            T(MyApplication.getAppContext(), s);
        else
            P.le("全局的context不存在");
    }

    public static void T(Context context, Object s) {
        Toast.makeText(context, s.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * Created by Administrator on 2016/5/8.
     */
    public static class DoubleClick {
        public static long lastTime = -1;

        public static boolean isDoubleClick() {
            long curTime = System.currentTimeMillis();
            //貌似系统定义的双击正是300毫秒 ViewConfiguration.getDoubleTapTimeout()
            if (curTime - lastTime < ViewConfiguration.getDoubleTapTimeout()) {
                lastTime = curTime;
                return true;
            } else {
                lastTime = curTime;
                return false;
            }
        }

        public static void cancel() {
            lastTime = -1;
        }
    }
    public static void getMesureWH(View v, int[] WH) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(width, height);
        WH[0] = v.getMeasuredWidth();
        WH[1] = v.getMeasuredHeight();
    }
    /**
     * 获取两点间的位置
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    float getDis(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2, dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
