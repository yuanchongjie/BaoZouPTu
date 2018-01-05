package a.baozouptu.chosePicture.data;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import a.baozouptu.R;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.FileTool;
import a.baozouptu.common.util.Util;

/**
 * Created by LiuGuicen on 2017/1/18 0018.
 */

public class PicDirInfoManager {
    /**
     * 文件的信息
     */
    private List<PicDirInfo> picDirInfos;

    public PicDirInfoManager() {
        picDirInfos = new ArrayList<>();
        picDirInfos.add(new PicDirInfo("ddd", formatDescribeInfo("0", 0), "sdf"));
    }

    public void updateUsuInfo(List<String> usuPaths) {
        // 处理文件信息,将要显示的文件信息获取出来
        //常用图的信息，
        String representPath = usuPaths.size() == 0 ? null : usuPaths.get(1);
        PicDirInfo picDirInfo = new PicDirInfo("aaaaa",
                formatDescribeInfo("常用图片", usuPaths.size() - 3),
                representPath);
        picDirInfos.set(0, picDirInfo);
    }

    /**
     * 多处使用，便于统一格式，免得更改时到处改
     *
     * @param name   文件名称
     * @param number 文件数量
     */
    private SpannableString formatDescribeInfo(String name, int number) {
        String infos = " " + name + "\n" + " " + number + " 张";
        SpannableString sps = new SpannableString(infos);
        int s = infos.indexOf("\n") + 1, t = infos.length();
        sps.setSpan(new RelativeSizeSpan(0.8f), s, t, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sps.setSpan(new ForegroundColorSpan(Util.getColor(R.color.text_light_black)), s, t, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sps;
    }

    /**
     * 更新图片文件的信息，在drawer中的，包括文件目录信息，文件中图片数目，最新图片的路径
     *
     * @param picFileNumberMap    文件中图片张数信息
     * @param PicFileRepresentMap 文件代表图片信息
     */
    public void updateAllFileInfo(Map<String, Integer> picFileNumberMap, Map<String, String> PicFileRepresentMap) {
        for (Map.Entry<String, Integer> entry : picFileNumberMap.entrySet()) {
            String path = entry.getKey();
            String representPath = PicFileRepresentMap.get(path);
            String name = path.substring(path.lastIndexOf("/") + 1, path.length());
            SpannableString numInfo = formatDescribeInfo(name, entry.getValue());
            picDirInfos.add(new PicDirInfo(path, numInfo, representPath));
        }
    }

    public void clear() {
        picDirInfos.clear();
        picDirInfos.add(new PicDirInfo("asdas", formatDescribeInfo("0", 0), "sdfsd"));
    }

    public String getDirPath(int position) {
        if (position >= picDirInfos.size()) position = picDirInfos.size() - 1;
        return picDirInfos.get(position).getDirPath();
    }

    /**
     * 根据目录路径找到其的位置
     *
     * @param dirPath 目录路径
     */
    private int findDirPathId(String dirPath) {
        for (int i = 0; i < picDirInfos.size(); i++) {
            if (picDirInfos.get(i).getDirPath().equals(dirPath))
                return i;
        }
        return -1;
    }

    /**
     * 新增一张图片时改变相应的目录信息
     *
     * @return 针对添加图片时，图片目录尚不存在的情况，刷新图片列表
     */
    public boolean onAddNewPic(String newPicPath) {
        String parentPath = FileTool.getParentPath(newPicPath);
        int id = findDirPathId(parentPath);
        if (id == -1) {//如果没找到,尚未加入此目录，需要先添加
            addOneDirInfo(newPicPath, parentPath);
            return true;
        }
        String info = picDirInfos.get(id).getPicNumInfo().toString();
        int number = Integer.valueOf(info.substring(info.indexOf('\n') + 2, Util.lastDigit(info) + 1));
        number++;
        SpannableString new_info = formatDescribeInfo(info.substring(0, info.indexOf('\n')), number);
        picDirInfos.set(id, new PicDirInfo(parentPath, new_info, newPicPath));
        return false;
    }

    private void addOneDirInfo(String picPath, String parentPath) {
        picDirInfos.add(new PicDirInfo(parentPath, formatDescribeInfo(FileTool.getFileNameInPath(parentPath), 1), picPath));
    }

    /**
     * 低效方法，有时间改进
     * 删除图片文件，并更新目录列表信息
     * <p>更新文件信息，文件是否还存在，图片张数，最新图片，描述信息的字符串
     * <p>注意发送删除通知
     *
     * @return 是否删除成功
     */
    public boolean onDeleteOnePicInfile(String picPath) {
        File file = new File(picPath);
        String dirPath = file.getParent();
        if (file.exists()) {
            if (!file.delete()) {
                return false;
            }
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(new File(picPath)));
            AllData.appContext.sendBroadcast(scanIntent);
            MediaScannerConnection.scanFile(AllData.appContext, new String[]{dirPath}, null, null);
        }

//更新文件目录信息
        int id = findDirPathId(dirPath);//图片所在目录的位置id
        if (id != -1) {
            List<String> paths = new ArrayList<>();
            FileTool.getOrderedPicListInFile(dirPath, paths);
            if (paths.size() == 0)//如果此目录下面已经没有图片
            {
                picDirInfos.remove(id);
            } else {//还有图片则更新信息
                String representPath = paths.get(0);
                String name = dirPath.substring(dirPath.lastIndexOf("/") + 1, dirPath.length());
                SpannableString info = formatDescribeInfo(name, paths.size());
                picDirInfos.set(id, new PicDirInfo(dirPath, info, representPath));
            }
        }
        return true;
    }

    public List<PicDirInfo> getPicDirInfos() {
        return picDirInfos;
    }
}
