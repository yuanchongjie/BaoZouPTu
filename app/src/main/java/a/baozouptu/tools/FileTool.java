package a.baozouptu.tools;

import android.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import a.baozouptu.dataAndLogic.AllDate;

/**
 * 主要用于获取图片文件夹下的所有图片的路径，图片大小支取5k-6000k的见方法注解
 *
 * @author acm_lgc
 * @version jdk 1.8, sdk 21
 */
public class FileTool {

    /**
     * 遍历目录,得到所有图片的路径，图片大小在5-6000k之间，注意判空
     * 获取到的数据会直接往添加到第二个参数末尾添加
     *
     * @param path     String 要显示的图片文件夹的路径，
     * @param lstPaths List《String》括号用不了？？？ , 返回的结构， 文件夹下及子文件夹下所有符合条件的图片的路径,
     *                 因为要得到子文件夹路径下的图片路径，所以使用传参赋值更好
     */
    public void getOrderedPicListInFile(String path, List<String> lstPaths) {
        List<Pair<Long, String>> oderedPaths = new ArrayList<>();

        File file = new File(path);
        if (file == null)
            return;

        File[] fs = file.listFiles();
        if (fs == null)
            return;

        for (File f : fs) {
            if (f == null)
                continue;
            String fName = f.getName(); // 文件名
            String htx = fName.substring(fName.lastIndexOf(".") + 1,
                    fName.length()).toLowerCase(); // 得到扩展名,用求字串的方法

            for (String s : AllDate.normalPictureFormat) {
                if (htx.equals(s)) {
                    if (fileSizeValidity(f.getPath())) {
                        oderedPaths.add(new Pair(-f.lastModified(), f.getPath()));
                    }
                    break;
                }
            }
        }
        Collections.sort(oderedPaths, new Comparator<Pair<Long,String>>() {
            public int compare(Pair<Long,String> o1, Pair<Long,String> o2) {
                //return (o2.getValue() - o1.getValue());
                return o1.first.compareTo(o2.first);
            }
        });
        for (Pair<Long,String> p: oderedPaths)
            lstPaths.add(p.second);
    }

    /**
     * 判断文件大小有效性 如果文件内存在5K-6000K之间则有效，否则返回FALSE
     *
     * @param path 图片的路径
     * @return 图片大小是否符合要求
     */
    private boolean fileSizeValidity(String path) {
        File f = new File(path);
        if (f.exists()) {
            int cur = 0;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);

                cur = fis.available() / 1000;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (cur >= AllDate.PIC_FILE_SIZE_MIN && cur <= AllDate.PIC_FILE_SIZE_MAX)
                return true;
        }

        return false;
    }

    /**
     * 根据原来的路径创建一个新的路径和名称
     *
     * @param oldPath 以前的路径
     * @return
     */
    public static String getNewPictureFile(String oldPath) {
        String prefix = oldPath.substring(0, oldPath.lastIndexOf("."));
        String suffix = oldPath.substring(oldPath.lastIndexOf("."), oldPath.length());
        String newPath=null;
        File newFile;
        for(int i=0;i<10000000;i++){
            newPath=prefix + "baozou" + i + suffix;
            newFile=new File(newPath);
            if(!newFile.exists())
                break;
        }
        return newPath;
    }


}
