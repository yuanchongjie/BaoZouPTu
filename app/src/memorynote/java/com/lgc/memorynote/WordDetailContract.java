package com.lgc.memorynote;

import android.content.Intent;

import a.baozouptu.BasePresenter;
import a.baozouptu.BaseView;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 * 主持者和View契约类
 */

public interface WordDetailContract {
    interface View extends BaseView<Presenter>, android.view.View.OnClickListener {
        void showWord(String word);

        void showWordMeaning(String wordMeaning);

        void showSimilarWords(String similarWords);

        void showStrangeDegree(int strangeDegree);

        void showLastRememberTime(long lastRememberTime);
    }

    interface Presenter extends BasePresenter {

        void initDate(Intent intent);

        void setWordName(String wordName);

        boolean isInEdit();

        void switchEdit();

        /**
         * 返回，增加或者删除是否超限
         */
        boolean addStrangeDegree();

        boolean reduceStrangeDegree();

        void setSimilarFormatWords(String similarFormatWords);

        /**
         * 设置单词的意思
         */
        void setWordMeaning(String wordMeaning);

        /**
         * @return 加入的单词是否是有效单词
         */
        boolean addWord(String word);

        void setLastRememberTime();
    }

}
