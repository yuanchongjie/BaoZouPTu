package a.baozouptu.ptu.tietu.onlineTietu;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by LiuGuicen on 2017/2/17 0017.
 * 贴图素材
 */

public class tietu_material extends BmobObject {
    public static final String CATEGORY_EXPRESSION = "expression";
    public static final String CATEGORY_PROPERTY = "property";
    private BmobFile url;
    private String category;//
    private Integer heat;//热度

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BmobFile getUrl() {
        return url;
    }

    public void setUrl(BmobFile url) {
        this.url = url;
    }

    public Integer getHeat() {
        return heat;
    }

    public void setHeat(Integer heat) {
        this.heat = heat;
    }

    public String getTheOnlyName() {
        return url.getUrl().substring(url.getUrl().lastIndexOf("/") + 1);
    }
}
