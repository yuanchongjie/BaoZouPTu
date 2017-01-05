package a.baozouptu.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.Util;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;


/**
 * Created by Administrator on 2016/11/21 0021.
 *
 */

public class FileDownloader {
    public final static ArrayList<String> typefaceNames =
            new ArrayList<>(Arrays.asList("default", "kaiti.TTF", "banner.ttf", "xingshu.ttf", "xiaowanzi.ttf"));
    public final static ArrayList<String> typefaceChinese = new ArrayList<>(Arrays.asList("默认", "楷体", "横幅", "行书", "小丸子"));
    private final static ArrayList<String> typefaceUrl = new ArrayList<>(Arrays.asList(
            "default",
            "http://bmob-cdn-6999.b0.upaiyun.com/2016/11/21/f9aa491840ce450080854e20c025651e.TTF",
            "http://bmob-cdn-6999.b0.upaiyun.com/2016/11/21/ab449a534091f7b680a0718b75737168.ttf",
            "http://bmob-cdn-6999.b0.upaiyun.com/2016/11/21/8a5b35b5404164ea8052040e210c09a4.ttf",
            "http://bmob-cdn-6999.b0.upaiyun.com/2016/11/21/04f24d4240709536807c7c538ea310d8.ttf"));

    String zitiDir = AllData.appFilePathDefault + "字体/";
    final String[] typefaceLoacalPath = new String[]{
            "default",
            zitiDir + "kaiti.ttf",
            zitiDir + "banner.ttf",
            zitiDir + "xingshu.ttf",
            zitiDir + "xiaowanzi.ttf"};

    private final static float[] typefaceSizes = new float[]{0f, 5.33f, 1.80f, 8.61f, 1.84f};

    private static FileDownloader instance;

    /**
     * 最好要传入应用的context
     */
    private FileDownloader() {

    }

    public static FileDownloader getInstance() {
        if (instance == null)
            instance = new FileDownloader();
        return instance;
    }

    /**
     * 下载字体，下载完成后更新一些数据
     *
     * @param context      要显示对话框，必须传入Activity的context
     * @param name         一下几种之一 kaiti.TTF
     * @param typefaceList 要更新的列表
     * @param textView     要更新的view
     */
    public void downloadZiti(Context context, final String name, ArrayList<Typeface> typefaceList, TextView textView) {
        int id = typefaceNames.indexOf(name);
        adjustNetwork(context, id, typefaceList, textView);
    }

    private void realDownloadZiti(final Context context, final int id, final ArrayList<Typeface> typefaceList, final TextView textView) {
        final String name = typefaceNames.get(id);
        String url = typefaceUrl.get(id);
        BmobFile bmobfile = new BmobFile(name, "", url);
        final String savePath = AllData.appFilePathDefault + "字体/" + name;
        final File saveFile = new File(savePath);
        Toast.makeText(context, "开始下载" + typefaceChinese.get(id) + "字体，请稍后！", Toast.LENGTH_SHORT).show();
        bmobfile.download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Typeface typeface = Typeface.createFromFile(savePath);
                    typefaceList.remove(id);
                    typefaceList.add(id, typeface);
                    textView.setTypeface(typeface);
                    Toast.makeText(context, typefaceChinese.get(id) + "下载成功了,点击即可使用！", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "下载失败：" + e.getErrorCode() + "," + e.getMessage(), Toast.LENGTH_LONG).show();
                    if (saveFile.exists()) saveFile.delete();//删除没下完整的文件
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }

    private void adjustNetwork(final Context context, final int id, final ArrayList<Typeface> typefaceList, final TextView textView) {
        float size = typefaceSizes[id];
        int state = NetworkState.detecteNetworkType(context);
        String msg;
        if (state == -1) {//没有网络
            Toast.makeText(context, "网络未连接，不能下载字体，请稍后再试！", Toast.LENGTH_LONG).show();
            return;
        }
        if (state == 1)//是wifi
        {
            msg = "已连接到WiFi，字体大小:" + size + "MB," + "确认下载吗？";
        } else {
            msg = "WiFi未连接，字体大小：" + size + "MB," + "会产生流量消耗，确定下载吗?";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("字体下载")
                .setMessage(msg)
                .setNegativeButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realDownloadZiti(context, id, typefaceList, textView);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
