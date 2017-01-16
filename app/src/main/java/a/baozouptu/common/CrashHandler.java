package a.baozouptu.common;

/**
 * Created by Administrator on 2016/11/25 0025.
 *
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(Thread.getDefaultUncaughtExceptionHandler()!=null) {
            new CrashLog(thread,ex).commit();
        }
    }
}
