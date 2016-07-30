package a.baozouptu.ptu.view;

/**
 * Created by Administrator on 2016/7/28.
 */
public interface GestureImageView {
    void scale(float centerX,float centerY,float ratio);
    void move(float dx,float dy);
    void rotate(float angle);
}
