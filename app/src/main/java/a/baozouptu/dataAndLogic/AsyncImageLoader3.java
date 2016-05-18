package a.baozouptu.dataAndLogic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import a.baozouptu.tools.BitmapTool;
import a.baozouptu.tools.Util;

/**
 * 缓存，异步，多线程
 * 为了加快速度，在内存中开启缓存（主要应用于重复图片较多时，或者同一个图片要多次被访问， 比如在ListView时来回滚动） 软引用不可用 public
 * Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String,
 * SoftReference<Bitmap>>();
 */
public class AsyncImageLoader3 {
    /**
     * 使用LRU算法，用key-value形式查找对象；
     */
    public static LruCache<String, Bitmap> imageCache;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    LinkedBlockingQueue<Runnable> lque = new LinkedBlockingQueue<Runnable>();
    /**
     * 线程池，固定五个线程来执行任务，规定最大线程数量的线程池
     */
    private ExecutorService executorService = new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS,
            lque);

    private final Handler handler = new Handler();
    private static AsyncImageLoader3 asyncImageLoader3;

    public static AsyncImageLoader3 getInstatnce() {
        if (asyncImageLoader3 == null)
            asyncImageLoader3 = new AsyncImageLoader3();
        return asyncImageLoader3;
    }

    private AsyncImageLoader3() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Util.P.le("最大可用内存", maxMemory);
        imageCache = new LruCache<String, Bitmap>(maxMemory / 8) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    /**
     * 如果缓存过就从缓存中取出数据
     *
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmap(String imageUrl) {
        if (imageCache.get(imageUrl) != null) {
            return imageCache.get(imageUrl);
        }
        return null;
    }

    /**
     * 使用线程池和handler将需要的图片加载到对应的View上面,如果图片存在LRUcache，则直接返回图片的Bitmap对象，
     * 如果不存在，直接异步获取Bitmap，并进行加载
     *
     * @param imageUrl 图像url地址
     * @param image    要加载图片的那个ImageView
     * @param callback 自己实现一个接口用于回调
     * @return 返回内存中缓存的图像，第一次加载返回null
     */
    public Bitmap loadBitmap(final String imageUrl, final ImageView image, final int position,
                             final ImageCallback callback) {
        // 缓存中没有图像，则从SDcard取出数据，并将取出的数据缓存到内存中
        if (imageCache.get(imageUrl) != null) {
            return imageCache.get(imageUrl);
        }
        executorService.submit(new Runnable() {// 线程池执行取出图片的进程
            public void run() {
                try {
                    final Bitmap bitmap = loadImageFromSD(imageUrl);// 获取图片URL对应的图片Bitmap
                    imageCache.put(imageUrl, bitmap);
                    handler.post(// handler的轻量级方法，利用handler的post方法，在attached的即handler依附的线程中执行下面的代码
                            new Runnable() {
                                public void run() {
                                    callback.imageLoaded(bitmap, image, position,
                                            imageUrl);
                                }
                            });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return null;
    }

    public void cancelLoad() {
        while (!lque.isEmpty())lque.clear();
    }

    /**
     * 从所给路径，返回对应大小的图片Bitmap对象
     *
     * @param path String 路径
     * @return Bitmap对象
     */
    private Bitmap loadImageFromSD(String path) {
        try {
            // 测试时，模拟网络延时，实际时这行代码不能有
            // SystemClock.sleep(2000);
            Bitmap bm = new BitmapTool().charge(path);
            return bm;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 对外界开放的回调接口
    public interface ImageCallback {
        /**
         * 将Bitmap的对象放入到image里面，这个接口已经导入，可以使用
         *
         * @param imageDrawable 将要放入的Bitmap对象，
         * @param image         显示Bitmap的View
         */
        void imageLoaded(Bitmap imageDrawable, ImageView image, int position,
                         String imageUrl);
    }
}
