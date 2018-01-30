package com.lgc.memorynote;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/************************************
 * 数据格式的定义：
 * 1、词义
 * 目前的词义保存在一个String中，词义分为标签，意思两部分
 * 标签以@xxx开头，
 * {@link WordMeaning#MEANING_GUAI}   表怪的词义，
 * {@link WordMeaning#MEANING_SHENG}  表示陌生的词义
 * {@link WordMeaning#MEANING_V} 等表示词性
 * 然后词义的内容用#xxxx#表示，即#xxx# 中间的内容
 * 等表示词性 括号中的为词性
 * 2、相似词，形近词
 * 简单，多个词之间用空格隔开
 *
 * **********************************/
public class Word {

    public String word;
    public int strangeDegree;
    public long lastRememberTime;
    private String mOriginalMeaning;
    private List<String> jsonMeaningList = new ArrayList<>();
    public List<WordMeaning> meaningList = new ArrayList<>();

    public void setAndAnalyzeMeaning(String meaning) {
        meaning = meaning.trim();
        /**
         * 惰性匹配，并且匹配换行
         */
        Matcher matcher = Pattern.compile("(@.*?)(#.*?#)", Pattern.DOTALL).matcher(meaning);
        while(matcher.find()) {
            WordMeaning wordMeaning = new WordMeaning();

            // 处理tag相关的
            Matcher tagMather = Pattern.compile("@.*?").matcher(matcher.group(1));
            while (tagMather.find()) {
                String tag = tagMather.group();
                if(wordMeaning.MEANING_GUAI.equals(tag)) {
                    wordMeaning.setGuai(true);
                } if (wordMeaning.MEANING_SHENG.equals(tag)) {
                    wordMeaning.setSheng(true);
                } else if (!TextUtils.isEmpty(tag)){
                    wordMeaning.setCiXing(tag.substring(1, tag.length() - 1));
                }
            }

            // 处理实际意思
            String realMeaning = matcher.group(2);
            if (realMeaning != null && realMeaning.length() >= 2) {
                realMeaning = realMeaning.substring(1, realMeaning.length() -1);
            }

            wordMeaning.setMeaning(realMeaning);
            meaningList.add(wordMeaning);
        }

        for (WordMeaning oneMeaning : meaningList) {
            Gson gson = new Gson();
            String stringMeaning = gson.toJson(oneMeaning);
            jsonMeaningList.add(stringMeaning);
        }
    }

    public static class WordMeaning {

        public static final String MEANING_GUAI = "@guai"; // 词义比较怪
        public static final String MEANING_SHENG = "@shen"; // 词义比较生
        public static final String MEANING_V = "v";
        public static final String MEANING_ADJ = "v";
        public static final String MEANING_ADV = "v";
        public static final String MEANING_N = "v";
        private boolean isGuai;
        private boolean isSheng;
        private String meaning;
        private String ciXing;


        public void setGuai(boolean guai) {
            isGuai = guai;
        }

        public void setSheng(boolean sheng) {
            isSheng = sheng;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        public boolean isGuai() {
            return isGuai;
        }

        public boolean isSheng() {
            return isSheng;
        }

        public String getMeaning() {
            return meaning;
        }

        public String getCiXing() {
            return ciXing;
        }

        public void setCiXing(String ciXing) {
            this.ciXing = ciXing;
        }
    }
}
