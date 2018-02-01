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
 * 示例:
 * @v @sheng #相信，信任#
 * @v @guai @sheng #赞颂，把...归功于#
 * @n #信任，学分，声望#
 *
 * @adj #国会的，议会的#
 * **********************************/
public class Word {

    public String word;
    public int strangeDegree;
    public long lastRememberTime;
    public String jsonMeaning;
    public List<WordMeaning> meaningList = new ArrayList<>();


    public static class WordMeaning {

        public static final String MEANING_GUAI = "@guai"; // 词义比较怪
        public static final String MEANING_SHENG = "@sheng"; // 词义比较生
        public static final String MEANING_N = "n";
        public static final String MEANING_V = "v";
        public static final String MEANING_ADJ = "adj";
        public static final String MEANING_ADV = "adv";
        private boolean isGuai = false;
        private boolean isSheng = false;
        private String meaning;
        private String ciXing = "null";


        public void setGuai(boolean guai) {
            isGuai = guai;
        }

        public void setSheng(boolean sheng) {
            isSheng = sheng;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        public boolean setCiXing(String ciXing) {
            if (WordMeaning.MEANING_N.equals(ciXing)
                    |WordMeaning.MEANING_V.equals(ciXing)
                    |WordMeaning.MEANING_ADJ.equals(ciXing)
                    |WordMeaning.MEANING_ADV.equals(ciXing)) {
                this.ciXing =ciXing;
                return true;
            }
            return false;
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
    }

    /**
     * 解析用户输入的数据，并放到Word中，返回解析结果
     * @param originalMeaning
     * @return 解析结果
     */
    public int setMeaningFromUser(String originalMeaning) {
        if (originalMeaning == null) return WordAnalyzer.IS_NULL;
        List<WordMeaning> meaningList = new ArrayList<>();
        int resultCode = WordAnalyzer.SUCCESS;
        originalMeaning = originalMeaning.trim();
        /**
         * 惰性匹配，并且匹配换行
         */
        Matcher matcher = Pattern.compile("(@.*?|.*?)(#.*?#)", Pattern.DOTALL).matcher(originalMeaning);
        while(matcher.find()) {
            WordMeaning oneMeaning = new WordMeaning();

            // 处理tag相关的
            Matcher tagMather = Pattern.compile("@[^@#]*", Pattern.DOTALL).matcher(matcher.group(1));
            while (tagMather.find()) {
                String tag = tagMather.group(0).trim();
                if(WordMeaning.MEANING_GUAI.equals(tag)) {
                    oneMeaning.setGuai(true);
                } if (WordMeaning.MEANING_SHENG.equals(tag)) {
                    oneMeaning.setSheng(true);
                } else if (tag.length() > 1){
                    boolean isValid = oneMeaning.setCiXing(tag.substring(1, tag.length()).trim());
                    if (!isValid) {
                        resultCode = WordAnalyzer.TAG_FORMAT_ERROR;
                    }
                }
            }

            // 处理实际意思
            String realMeaning = matcher.group(2);
            if (realMeaning != null && realMeaning.length() >= 2) {
                realMeaning = realMeaning.substring(1, realMeaning.length() -1).trim();
            }
            if (realMeaning == null || realMeaning.trim().isEmpty()) { // 意思为空
                continue;
            }
            oneMeaning.setMeaning(realMeaning);
            meaningList.add(oneMeaning);
        }
        // 对于没加任何标签的，直接解析，简化用户输入
        if (!originalMeaning.isEmpty() && !originalMeaning.contains("#") && !originalMeaning.contains("@")) {
            WordMeaning oneMeaning = new WordMeaning();
            oneMeaning.setMeaning(originalMeaning);
            meaningList.add(oneMeaning);
        }
        if (meaningList.size() == 0) { // 没有获取到有效的词义，不设置数据
            resultCode = WordAnalyzer.NO_VALID_MEANING;
        } else {
            setMeaningList(meaningList);
            jsonMeaning = new Gson().toJson(meaningList);
        }
        return resultCode;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) return false;
        return this == obj || TextUtils.equals(this.word, ((Word) obj).word);
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setStrangeDegree(int strangeDegree) {
        this.strangeDegree = strangeDegree;
    }

    public void setLastRememberTime(long lastRememberTime) {
        this.lastRememberTime = lastRememberTime;
    }

    public void setJsonMeaning(String jsonMeaning) {
        this.jsonMeaning = jsonMeaning;
    }

    public void setMeaningList(List<WordMeaning> meaningList) {
        this.meaningList = meaningList;
    }

    public String getWord() {
        return word;
    }

    public int getStrangeDegree() {
        return strangeDegree;
    }

    public long getLastRememberTime() {
        return lastRememberTime;
    }

    public String getJsonMeaning() {
        return jsonMeaning;
    }

    public List<WordMeaning> getMeaningList() {
        return meaningList;
    }
}
