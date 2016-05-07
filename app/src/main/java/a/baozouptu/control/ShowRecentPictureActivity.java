package a.baozouptu.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import a.baozouptu.tools.Date;
import a.baozouptu.tools.FileTool;
import a.baozouptu.dataAndLogic.GridViewAdapter;
import a.baozouptu.tools.P;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import a.baozouptu.R;

public class ShowRecentPictureActivity extends Activity {
    /**
     * 内部SD卡路径
     */
    private String InnerSDpath;
    /**
     * 进度条
     */
    private ProgressDialog m_ProgressDialog = null;
    /**
     * 存放所有图片的路径
     */
    public static List<String> lstFilePath = null;
    /**
     * 要显示的最近改动过图片的张数
     */
    private static int RECENT_BITMAP_NUMBER = 20;
    /**
     * 最少要显示的图片张数
     */
    private static final int MIN_NUMBER = 120;
    /**
     * map存下所有相册的名字和张数
     */
    Map<String, Integer> fileInfo = new HashMap<String, Integer>();
    /**
     * 每个文件夹下的代表图片的路径
     */
    Map<String, String> picturePath = new HashMap<String, String>();
    /**
     * 排序处理得到的图片的map
     */
    final Map<Integer, String> sortPictureMap = new TreeMap<Integer, String>();

    /**
     * @category 获取所有图片的路径
     */
    private void getValues() {
        P.le("getValue");

        lstFilePath = new ArrayList<String>();
        lstFilePath.clear();
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
        getRecentBitmapPath(RECENT_BITMAP_NUMBER, lstFilePath);
        FileTool fileTool = new FileTool();
        fileTool.ListFiles(QQPictureFace, lstFilePath);
        fileTool.ListFiles(QQCollection, lstFilePath);
        fileTool.ListFiles(QQImage, lstFilePath);
        fileTool.ListFiles(WeiXinSave, lstFilePath);
        fileTool.ListFiles(MoMoSave, lstFilePath);
        // 获取最近修改过的图片
        runOnUiThread(returnRes);// 表示强制这个线程在UI线程之前启动，
    }

    /**
     * 启动一个新线程从图片数据库中获取图片信息
     *
     * @param number   要获取的最近修改图片的张数
     * @param lstpaths 将最近修改图片的路径放到lstPath之中
     */
    void getRecentBitmapPath(final int number, final List<String> lstpaths) {

        P.le("GRB");

        Thread thread = new Thread(new Runnable() {
            String[] projection = {MediaStore.Images.Media.DATE_MODIFIED,
                    MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};

            @Override
            public void run() {
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
                        if (5000 < size && size < 6000000) {// 图片符合条件
                            sortPictureMap.put(-modifyTime, path);
                            String parentPath = path.substring(0,
                                    path.lastIndexOf('/') - 1);
                            if (fileInfo.containsKey(parentPath))
                                fileInfo.put(parentPath,
                                        fileInfo.get(parentPath) + 1);
                            else {
                                fileInfo.put(parentPath, 1);
                                picturePath.put(parentPath, path);
                            }
                        }
                    }
                    cursor.close();
                }
                // 将符合条件的前几张图片取出
                int num = 0;
                for (Map.Entry<Integer, String> entry : sortPictureMap
                        .entrySet()) {
                    String value = (String) entry.getValue();
                    lstpaths.add(value);
                    if (++num > number)
                        break;
                }
            }
        });
        thread.run();
    }

    /**
     * Called when the activity is first created.
     * 过程描述：启动一个线程获取所有图片的路径，再启动一个子线程设置好GridView，而且要求这个子线程必须在ui线程之前启动
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        InnerSDpath = Environment.getExternalStorageDirectory().getPath();

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        Date.screenWidth = metric.widthPixels; // 屏幕宽度（像素）
        P.le("ShowRecentPictureActivity,returnres", "屏幕宽度" + Date.screenWidth);

        Thread thread = new Thread(null, new Runnable() {
            public void run() {
                getValues();
            }
        }, "MagentoBackground");
        thread.start();

        m_ProgressDialog = ProgressDialog.show(ShowRecentPictureActivity.this, "请稍后",
                "数据读取中...", true);

        // 跳转显示文件夹的button
        Button showPictureFileBn = (Button) findViewById(R.id.show_picture_file);
        showPictureFileBn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                P.le(12, System.currentTimeMillis());
                Intent intent = new Intent(ShowRecentPictureActivity.this,
                        ShowFileActivity.class);
                int[] pictureNumber = new int[fileInfo.size()];
                int i = 0;
                for (Map.Entry<String, Integer> entry : fileInfo.entrySet()) {
                    pictureNumber[i] = (int) entry.getValue();
                    intent.putExtra(String.valueOf(i),
                            picturePath.get(entry.getKey()));// 根据编号放入代表图片的路径
                    i++;
                }
                intent.putExtra("pictureNumber", pictureNumber);
                P.le(fileInfo.size());
                P.le(12, System.currentTimeMillis());
                startActivity(intent);
            }
        });
    }

    /**
     * @category 获取屏幕的宽，并利用lstFilePath设置gridview内容了
     */
    private Runnable returnRes = new Runnable() {
        public void run() {
            GridView gridview = (GridView) findViewById(R.id.gv_photolist);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(ShowRecentPictureActivity.this, PTuActivity.class);
                    intent.putExtra("path", lstFilePath.get(position));
                    startActivity(intent);
                }
            });
            GridViewAdapter iadapter = new GridViewAdapter(
                    ShowRecentPictureActivity.this, lstFilePath);
            gridview.setAdapter(iadapter);
            m_ProgressDialog.dismiss();// 表示此处开始就解除这个进度条Dialog，应该是在相对起始线程的另一个中使用
        }
    };
}
