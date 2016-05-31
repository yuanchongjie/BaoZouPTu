package a.baozouptu.tools;

import android.graphics.Rect;

/**
 * 非水平的矩形,可以为斜的的那种矩形，注意构造时必须按顺时针或逆时针放入四个点
 * Created by Administrator on 2016/5/31.
 */
public class UnlevelRect {
    float x1, y1, x2, y2, x3, y3, x4, y4;
/**
 * 注意构造时必须按顺时针或逆时针放入四个点
 */
    public UnlevelRect(float x1, float y2, float x2, float y1, float x3, float y4, float x4, float y3) {
        this.x1 = x1;
        this.y2 = y2;
        this.x2 = x2;
        this.y1 = y1;
        this.x3 = x3;
        this.y4 = y4;
        this.x4 = x4;
        this.y3 = y3;
    }

    /**
     * 水平的矩形，使用左上角和右下角设置,注意构造时必须按顺时针或逆时针放入四个点
     * @param x1
     * @param y1
     * @param x3
     * @param y3
     */
    public UnlevelRect(float x1,float y1,float x3,float y3){
        this.x1=x1;
        this.y1=y1;
        this.x3=x3;
        this.y3=y3;
        x2=x3;
        y2=y1;
        x4=x1;
        y4=y3;
    }
    public void set(float x1, float y2, float x2, float y1, float x3, float y4, float x4, float y3) {
        this.x1 = x1;
        this.y2 = y2;
        this.x2 = x2;
        this.y1 = y1;
        this.x3 = x3;
        this.y4 = y4;
        this.x4 = x4;
        this.y3 = y3;
    }

    public boolean contain(float x, float y) {
        if(isLeft(x1,y1,x2,y2,x,y)*isLeft(x1,y1,x2,y2,x3,y3)>=0
            &&isLeft(x2,y2,x3,y3,x,y)*isLeft(x2,y2,x3,y3,x4,y4)>=0
            &&isLeft(x3,y3,x4,y4,x,y)*isLeft(x3,y3,x4,y4,x1,y1)>=0
            &&isLeft(x4,y4,x1,y1,x,y)*isLeft(x4,y4,x1,y1,x2,y2)>=0)
            return true;
        return false;
    }
    private float isLeft(float x1,float y1,float x2,float y2,float x3,float y3){
        float x_1=x2-x1,y_1=y2-y1,x_2=x3-x1,y_2=y3-y1;
        return x_1*y_2-x_2*y_1;
    }

    public void translate(float dx, float dy) {
        x1+=dx;y1+=dy;
        x2+=dx;y2+=dy;
        x3+=dx;y3+=dy;
        x4+=dx;y4+=dy;
    }

    /**
     * 平移系统的平行的矩形
     * @param rect
     * @param dx
     * @param dy
     */
    public void translateFormRect(Rect rect,float dx, float dy){
        rect.left+=dx;
        rect.top+=dy;
        rect.right+=dx;
        rect.bottom+=dy;
    }
}
