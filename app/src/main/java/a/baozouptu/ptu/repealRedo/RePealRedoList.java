package a.baozouptu.ptu.repealRedo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Administrator on 2016/6/13.
 */

public class RePealRedoList<T> extends LinkedList<T> {
    private ListIterator<T> listIt = listIterator();
    //撤销重做的最大步数
    private static int maxStep = 5;
    private boolean hasChangePic;
    private int pointer;
    public boolean canRepeal() {
        return listIt.hasPrevious();
    }

    public int getCurrentPoint() {
        return listIt.previousIndex();
    }

    public boolean canRedo() {
        return listIt.hasNext();
    }

    public T redo() {
        return listIt.next();
    }

    /**
     * 添加一步，如果以前撤销过，这一步后面的数据都会删除
     * 如果数据大于限制，会删除最前面的
     * @param t
     */
    public void addStep(T t){
        while(listIt.hasNext()){
            listIt.remove();
        }
        listIt.add(t);
        if(size()>maxStep) {
            hasChangePic=true;
            remove(0);
        }
    }

    /**
     * 开始前移,将当前指针前移，后面撤销时就处理到指针位置时
     */
    public void startRepeal() {
        listIt.previous();
    }

    /**
     * repealRedo还有的功能就是检测本次编辑是够修改了图片
     * 根据当前指针pointer等
     * @return
     */
    public boolean hasChangePicture() {
        //有的步骤已经不能返回，有修改
        //当前指针不在最前面，有修改，
        if(hasChangePic||pointer>0)
            return true;
        return false;
    }
}
