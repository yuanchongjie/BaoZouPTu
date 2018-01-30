package a.baozouptu;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public interface BaseView<T> {
    void switchEdit(boolean mIsInEdit);

    void setPresenter(T presenter);
}
