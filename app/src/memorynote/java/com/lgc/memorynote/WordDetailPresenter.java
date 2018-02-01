package com.lgc.memorynote;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import static com.lgc.memorynote.WordDetailActivity.INTENT_EXTRA_IS_ADD;
import static com.lgc.memorynote.WordDetailActivity.INTENT_EXTRA_WORD_NAME;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public class WordDetailPresenter implements WordDetailContract.Presenter {
    private WordDetailDataSource mDataSource;
    private WordDetailContract.View mView;
    private boolean mIsAdd = false;
    private boolean mIsInEdit = false;
    private Context mContext;
    private Word mWord;
    private String mWordName;

    WordDetailPresenter(WordDetailDataSource wordDetailDataSource, WordDetailContract.View wordDetailView) {
        mDataSource = wordDetailDataSource;
        mView = wordDetailView;
        mContext = (Context) mView;
        mWord = new Word();
    }

    @Override
    public void setWordName(String wordName) {
        mWordName = wordName;
    }

    @Override
    public void initDate(Intent intent) {
        if (intent != null) {
            mIsAdd = intent.getBooleanExtra(INTENT_EXTRA_IS_ADD, false);
            if (!mIsAdd) {
                mWordName = intent.getStringExtra(INTENT_EXTRA_WORD_NAME);
            }
        }
    }

    @Override
    public void start() {
        if (!mIsAdd) {
            mWord = mDataSource.getWordDetail(mWordName);
        } else {
            mWord = new Word();
            if (!mIsInEdit) {
                switchEdit();
            }
        }
    }

    public void saveWordDate() {
        if (mIsAdd) {
            mDataSource.addWord(mWord);
        } else {
            mDataSource.updateWord(mWord);
        }
    }

    @Override
    public boolean isInEdit() {
        return mIsInEdit;
    }

    @Override
    public void switchEdit() {
        mIsInEdit = !mIsInEdit;
        mView.switchEdit(mIsInEdit);
        if (!mIsInEdit) { // 编辑完成
            saveWordDate();
        }
    }

    @Override
    public boolean addStrangeDegree() {
        mView.showStrangeDegree(mWord.strangeDegree);
        return false;
    }

    @Override
    public boolean reduceStrangeDegree() {
        mView.showStrangeDegree(mWord.strangeDegree);
        return false;
    }

    @Override
    public void setSimilarFormatWords(String inputSimilarWord) {
        List<String> similarWordList = new ArrayList<>();
        WordAnalyzer.analyzeSimilarWordsFromUser(inputSimilarWord, similarWordList);
    }

    @Override
    public void setWordMeaning(String inputMeaning) {
        List<Word.WordMeaning> meaningList = new ArrayList<>();
        WordAnalyzer.analyzeMeaningFromUser(inputMeaning, meaningList);
        mWord.setMeaningList(meaningList);
        mWord.setInputMeaning(inputMeaning);
    }

    @Override
    public boolean addWord(String word) {
        return false;
    }

    @Override
    public void setLastRememberTime() {
        mWord.lastRememberTime = System.currentTimeMillis();
    }
}
