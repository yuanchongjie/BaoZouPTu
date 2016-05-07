package a.baozouptu.tools;

/**
 * Created by Administrator on 2016/5/8.
 */
public class DoubleClick {
    public static long lastTime=-1;
    public static boolean isDoubleClick(){
        long curTime=System.currentTimeMillis();
        if(curTime-lastTime<300) {
            lastTime=curTime;
            return true;
        }
        else {
            lastTime=curTime;
            return false;
        }
    }
}
