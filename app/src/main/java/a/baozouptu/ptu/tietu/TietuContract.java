package a.baozouptu.ptu.tietu;

import a.baozouptu.BasePresenter;
import a.baozouptu.BaseView;

/**
 * Created by LiuGuicen on 2017/2/9 0009.
 */

public class TietuContract {

    /**
     * Created by LiuGuicen on 2017/2/9 0009.
     */

    public interface TietuPresenter extends BasePresenter {
        void prepareTietuByCategory(String TietuType);
    }

    public interface TietuView extends BaseView {
        void showExpressionList();

        void showPropertyList();
    }
}
