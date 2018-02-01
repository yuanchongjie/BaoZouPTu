package com.lgc.memorynote;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.common.util.Util;

public class WordDetailActivity extends AppCompatActivity implements WordDetailContract.View{

    public static final String INTENT_EXTRA_WORD_NAME = "intent_extra_word_detail_word_name";
    public static final String INTENT_EXTRA_IS_ADD = "intent_extra_word_detail_is_add";

    private WordDetailContract.Presenter mPresenter;
    private EditText mTvWordName;
    private EditText mTvWordMeaning;
    private EditText mTvSimilarWord;
    private TextView mTvStrangeDegree;
    private TextView mTvLastRememberTime;
    private Button mBtnEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        mPresenter = new WordDetailPresenter(
                new WordDetailDataSourceImpl(this.getApplicationContext()), this);
        initView();
        mPresenter.initDate(getIntent()); // 要在View初始化之后调用
    }

    private void initView() {
        mTvWordName          = (EditText) findViewById(R.id.word_detail_word);
        mTvWordMeaning       = (EditText) findViewById(R.id.word_detail_meaning);
        mTvSimilarWord       = (EditText) findViewById(R.id.similar_form_word);
        mTvStrangeDegree     = (TextView) findViewById(R.id.value_strange_degree);
        mTvLastRememberTime  = (TextView) findViewById(R.id.last_remember_time);
        mBtnEdit             = (Button) findViewById(R.id.btn_word_detail_edit);

        mTvWordName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_strange_degree:
                mPresenter.addStrangeDegree();
                break;
            case R.id.reduce_strange_degree:
                mPresenter.reduceStrangeDegree();
                break;
            case R.id.btn_word_detail_edit:
                mPresenter.switchEdit();
        }
    }

    @Override
    public void switchEdit(boolean isInEdit) {
        mTvWordName.setFocusable(isInEdit);
        mTvWordMeaning.setFocusable(isInEdit);
        mTvSimilarWord.setFocusable(isInEdit);
        if (isInEdit) {
            mBtnEdit.setText(getString(R.string.edit));
        } else {
            mBtnEdit.setText(getString(R.string.edit_finish));
        }
    }

    @Override
    public void showWord(String word) {
        mTvWordName.setText(word);
    }

    @Override
    public void showWordMeaning(String wordMeaning) {
        // 解析词义，特殊的词意采用特殊的颜色
        mTvWordMeaning.setText(wordMeaning);
    }

    @Override
    public void showSimilarWords(String similarWords) {
        mTvSimilarWord.setText(similarWords);
    }

    @Override
    public void showStrangeDegree(int strangeDegree) {
        mTvStrangeDegree.setText(strangeDegree + "");
    }

    @Override
    public void showLastRememberTime(long lastRememberTime) {
        String time = Util.long2Date("yyyy年MM月dd HH时MM分", lastRememberTime);
        mTvLastRememberTime.setText(time);
    }

    @Override
    protected void onStop() {
        mPresenter.setLastRememberTime();
        super.onStop();
    }


    @Override
    public void setPresenter(WordDetailContract.Presenter presenter) {

    }
}
