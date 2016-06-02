package a.baozouptu.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import a.baozouptu.dataAndLogic.AsyncImageLoader3;
import a.baozouptu.dataAndLogic.AllDate;
import a.baozouptu.tools.FileTool;
import a.baozouptu.dataAndLogic.GridViewAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import a.baozouptu.R;

/**
 * 显示所选的最近的或某个文件夹下面的所有图片
 * 并且有选择文件夹，相机，空白图画图的功能
 */
public class ShowPictureActivity extends Activity {
    /**
     * 进度条
     */
    private ProgressDialog m_ProgressDialog = null;
    /**
     * 保存最近图片的路径
     */
    public static List<String> recentPicPathList = new ArrayList<>();
    /**
     * 获取和保存某个文件下面所有图片的路径
     */
    private List<String> picPathInFile = new ArrayList<String>();
    /**
     * 当前要现实的所有图片的路径
     */
    private List<String> currentPicFilePathList = new ArrayList<>();


    /**
     * map存下所有相册的名字和张数
     */
    Map<String, Integer> picFileNumberMap = new TreeMap<String, Integer>();
    /**
     * 每个文件夹下的代表图片的路径
     */
    Map<String, String> representPicturePathMap = new TreeMap<String, String>();
    /**
     * 要显示的最近改动过图片的张数
     */
    private static int RECENT_BITMAP_NUMBER = 50;
    /**
     * 最少要显示的图片张数
     */
    private static final int MIN_NUMBER = 120;

    private Button showPictureFileBn;
    private DrawerLayout fileListDrawer;
    private GridViewAdapter showPicAdpter;
    private GridView gridview;

    /**
     * 获取所有图片的文件信息，最近的图片，
     * <p>并且为图片grid，文件列表加载数据
     */
    private void getValues() {
        /**
         * 内部SD卡路径
         */
        String InnerSDpath = Environment.getExternalStorageDirectory().getPath();
        recentPicPathList.clear();
        /** QQ表情 */
        String QQPictureFace = InnerSDpath + "/tencent/QQ_Favorite/";
        /** 获取几个指定文件夹的图片 */
        String QQCollection = InnerSDpath + "/tencent/QQ_Collection/pic/";
        /** QQ保存的图片 */
        String QQImage = InnerSDpath + "/tencent/QQ_Images/";
        /** 微信保存的 */
        String WeiXinSave = InnerSDpath + "/tencent/MicroMsg/WeiXin/";
        /** 陌陌保存的 */
        String MoMoSave = InnerSDpath + "/immomo/camera/";
        /**排序处理得到的图片的map*/
        final Map<Integer, String> sortPictureMap = new TreeMap<Integer, String>();

        getRecentBitmapPath(sortPictureMap, recentPicPathList, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getRecentBitmapPath(sortPictureMap, recentPicPathList, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        FileTool fileTool = new FileTool();
        fileTool.ListFiles(QQPictureFace, recentPicPathList);
        fileTool.ListFiles(QQCollection, recentPicPathList);
        fileTool.ListFiles(QQImage, recentPicPathList);
        fileTool.ListFiles(WeiXinSave, recentPicPathList);
        fileTool.ListFiles(MoMoSave, recentPicPathList);
        currentPicFilePathList = recentPicPathList;
        // 获取最近修改过的图片
        runOnUiThread(returnRes);// 表示强制这个线程在UI线程之前启动，
    }

    /**
     * 启动一个新线程从图片数据库中获取图片信息
     *
     * @param lstpaths 将最近修改图片的路径放到lstPath之中
     */
    void getRecentBitmapPath(final Map<Integer, String> sortPictureMap, final List<String> lstpaths, final Uri uri) {
        if (uri == null) return;//不为空，放入图片

        Thread thread = new Thread(new Runnable() {
            String[] projection = {MediaStore.Images.Media.DATE_MODIFIED,
                    MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};

            @Override
            public void run() {
                Cursor cursor = getContentResolver().query(uri,
                        projection, null, null, null);
                if (cursor != null) {// 从contentProvider之中取出图片
                    cursor.moveToFirst();
                    while (cursor.moveToNext()) {
                        int size = cursor.getInt(cursor
                                .getColumnIndex(MediaStore.Images.Media.SIZE));
                        String path = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));
                        int modifyTime = cursor.getInt(cursor
                                .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));// 最近修改时间
                        if (5000 < size && size < 16000000) {// 图片符合条件
                            sortPictureMap.put(-modifyTime, path);
                            String parentPath = path.substring(0,
                                    path.lastIndexOf('/'));
                            if (picFileNumberMap.containsKey(parentPath))
                                picFileNumberMap.put(parentPath,
                                        picFileNumberMap.get(parentPath) + 1);
                            else {
                                picFileNumberMap.put(parentPath, 1);
                                representPicturePathMap.put(parentPath, path);
                            }
                        }
                    }
                    cursor.close();
                }
                // 将符合条件的前图片取出
                for (String value : sortPictureMap.values()) {
                    lstpaths.add(value);
                    if (lstpaths.size() > RECENT_BITMAP_NUMBER)
                        break;
                }
            }
        });
        thread.run();
    }

    /**
     * 初始化设置文件list和图片list里面的内容
     */
    private Runnable returnRes = new Runnable() {
        public void run() {
            disposeShowPicture();
            disposeDrawer();
        }
    };

    /**
     * 为显示图片的gridView加载数据
     */
    private void disposeShowPicture() {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ShowPictureActivity.this, PTuActivity.class);
                intent.putExtra("path", currentPicFilePathList.get(position));
                startActivity(intent);
            }
        });
        showPicAdpter = new GridViewAdapter(
                ShowPictureActivity.this, currentPicFilePathList);
        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {
            AsyncImageLoader3 imageLoader = AsyncImageLoader3.getInstatnce();
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        showAdjacentPic();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        imageLoader.cancelLoad();//取消解析，提交的任务还没有执行的就不执行了
                        break;
                }
            }

            AsyncImageLoader3.ImageCallback imageCallback = new AsyncImageLoader3.ImageCallback() {
                public void imageLoaded(Bitmap imageBitmap, ImageView image, int position, String imageUrl) {
                    if (image != null && position == (int) image.getTag()) {
                        image.setImageBitmap(imageBitmap);
                    }
                }
            };

            private void showAdjacentPic() {
                int first = gridview.getFirstVisiblePosition();
                int last = gridview.getLastVisiblePosition();
                for (int position = first; position <= last; position++) {
                    String path = currentPicFilePathList.get(position);
                    final ImageView ivImage = (ImageView) gridview.findViewWithTag(position);
                    imageLoader.loadBitmap(path, ivImage, position, imageCallback);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        gridview.setAdapter(showPicAdpter);
        m_ProgressDialog.dismiss();// 表示此处开始就解除这个进度条Dialog，应该是在相对起始线程的另一个中使用
    }

    /**
     * 获取屏幕的宽度
     */
    void getScreenWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        AllDate.screenWidth = metric.widthPixels; // 屏幕宽度（像素）
    }

    /**
     * Called when the activity is first created.
     * 过程描述：启动一个线程获取所有图片的路径，再启动一个子线程设置好GridView，而且要求这个子线程必须在ui线程之前启动
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        getScreenWidth();
        initView();
        Intent intent = new Intent(ShowPictureActivity.this, PTuActivity.class);
        intent.putExtra("path", "/storage/sdcard1/哈哈.jpg");
        startActivity(intent);
/*
        new Thread(null, new Runnable() {
            public void run() {
                getValues();
            }
        }).start();

        m_ProgressDialog = ProgressDialog.show(ShowPictureActivity.this, "请稍后",
                "数据读取中...", true);

        // 跳转显示文件夹的button
        setClick();*/
    }

    private void initView() {
        fileListDrawer = (DrawerLayout) findViewById(R.id.drawer_layout_show_picture);
        gridview = (GridView) findViewById(R.id.gv_photolist);
    }

    void setClick() {
        showPictureFileBn = (Button) findViewById(R.id.show_picture_file);
        showPictureFileBn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fileListDrawer.openDrawer(GravityCompat.END);
            }
        });
    }

    /**
     * 加载Drawer的ListView数据
     */
    private void disposeDrawer() {

        List<String> picFileInfoList = new ArrayList<>();
        final List<String> representPicPathList = new ArrayList<>();
        picFileInfoList.add("最近图片 (" + recentPicPathList.size() + ")");
        representPicPathList.add(recentPicPathList.get(0));
        final List<String> picFilePathList = new ArrayList<>();
        picFilePathList.add("000");
        for (Map.Entry<String, Integer> entry : picFileNumberMap.entrySet()) {
            String path = entry.getKey();
            picFilePathList.add(path);
            representPicPathList.add(representPicturePathMap.get(path));
            String name = path.substring(path.lastIndexOf("/") + 1, path.length());
            picFileInfoList.add(name + " (" + String.valueOf(entry.getValue()) + ")");
        }
        ListView PictureFileListView = (ListView) findViewById(R.id.drawer_picture_file_list);

        PictureFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentPicFilePathList = recentPicPathList;
                    showPicAdpter.setList(recentPicPathList);
                } else
                    getCurrentPicPathList(picFilePathList.get(position));
                fileListDrawer.closeDrawer(GravityCompat.END);
                gridview.setAdapter(showPicAdpter);
            }

            /**
             * 获取将要显示的图片的列表，并且将当前要显示的列表{@code currentPicFilePathList}和adpter内的数据指向获取的列表
             * @param pictureFilePath
             */
            private void getCurrentPicPathList(String pictureFilePath) {
                picPathInFile.clear();
                FileTool fileTool = new FileTool();
                fileTool.ListFiles(pictureFilePath, picPathInFile);
                currentPicFilePathList = picPathInFile;
                showPicAdpter.setList(picPathInFile);
            }
        });
        PictureFileListView.setAdapter(new MyListAdapter(picFileInfoList, representPicPathList));
    }


    /**
     * 使用继承BaseAdapter处理ListView的图片显示
     *
     * @author acm_lgc
     */
    class MyListAdapter extends BaseAdapter {
        List<String> picFileInfoList;

        List<String> representPicturePathList;

        MyListAdapter(List<String> list, List<String> picPathList) {
            picFileInfoList = list;
            representPicturePathList = picPathList;
        }

        AsyncImageLoader3 asyLoader3 = AsyncImageLoader3.getInstatnce();

        private LayoutInflater layoutInflater = LayoutInflater
                .from(ShowPictureActivity.this);

        @Override
        public int getCount() {
            return picFileInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            public ImageView ivImage;
            public TextView ivText;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(
                        R.layout.list_item_picture_file, null);

                viewHolder.ivImage = (ImageView) convertView
                        .findViewById(R.id.represent_picture);
                viewHolder.ivText = (TextView) convertView
                        .findViewById(R.id.info_of_file);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.ivText.setText(picFileInfoList.get(position));
            // 这个地方主义，imageLoader启动了一个新线程获取图片到cacheImage里面，新线程运行，本线程也会运行，
            // 因为新线程耗时，所以本线程已经执行到后面了，先加载了一张预设的图片，然后这个新线程会使用handler类更新UI线程，
            // 妙啊！
            Bitmap cacheBitmap = asyLoader3.loadBitmap(
                    representPicturePathList.get(position), viewHolder.ivImage, position,
                    new AsyncImageLoader3.ImageCallback() {
                        public void imageLoaded(Bitmap imageDrawable,
                                                ImageView image, int poition, String imageUrl) {
                            image.setImageBitmap(imageDrawable);
                        }
                    });
            if (cacheBitmap == null) {
                viewHolder.ivImage.setImageResource(R.mipmap.icon1);
            } else {
                viewHolder.ivImage.setImageBitmap(cacheBitmap);
            }
            return convertView;
        }
    }

}
