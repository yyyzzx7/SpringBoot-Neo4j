package com.project.springbootneo4j.core;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class SentenceSimilarity {
    Map<String, int[]> vectorMap = new HashMap<String, int[]>();

    int[] tempArray = null;

    public SentenceSimilarity(String sentence1, String sentence2) {
        List<String> words1 = segStr(sentence1);
        List<String> words2 = segStr(sentence2);

        for (String s : words1) {
            if (vectorMap.containsKey(s)) {
                vectorMap.get(s)[0]++;
            } else {
                tempArray = new int[2];
                tempArray[0] = 1;
                tempArray[1] = 0;
                vectorMap.put(s, tempArray);
            }
        }

        for (String s : words2) {
            if (vectorMap.containsKey(s)) {
                vectorMap.get(s)[1]++;
            } else {
                tempArray = new int[2];
                tempArray[0] = 0;
                tempArray[1] = 1;
                vectorMap.put(s, tempArray);
            }
        }

    }

    // 求余弦相似度
    public double value() {
        double result = 0;
        result = pointMulti(vectorMap) / sqrtMulti(vectorMap);
        return result;
    }

    // 点乘（分子）
    private double pointMulti(Map<String, int[]> paramMap) {
        double result = 0;
        for (String key : paramMap.keySet()) {
            int[] temp = paramMap.get(key);
            result += (temp[0] * temp[1]);
        }
        return result;
    }

    // 相乘开根（分母）
    private double sqrtMulti(Map<String, int[]> paramMap) {
        double result = 0;
        result = squares(paramMap);
        result = Math.sqrt(result);
        return result;
    }

    // 求平方和
    private double squares(Map<String, int[]> paramMap) {
        double result1 = 0;
        double result2 = 0;
        Set<String> keySet = paramMap.keySet();
        for (String key : keySet) {
            int[] temp = paramMap.get(key);
            result1 += (temp[0] * temp[0]);
            result2 += (temp[1] * temp[1]);
        }
        return result1 * result2;
    }

    //分词
    public static List<String> segStr(String content) {
        // HanLP分词对象，加载用户词典
        Segment segment = HanLP.newSegment().enableCustomDictionary(true);
        // 分词后的短语list
        List<Term> terms = segment.seg(content);
        // 返回结果字符串List
        List<String> result = new ArrayList<String>();
        for (Term term : terms) {
            result.add(term.word);
        }
        return result;
    }


    public static void main(String[] args) {
        String s1 = "苹果公司是美国的一家高科技公司";
        String s2 = "小米公司是中国的一家高科技公司";
//        SentenceSimilarity similarity = new SentenceSimilarity(s1, s2);
//        double sim_value = similarity.value();
//        System.out.println(sim_value);
        System.out.println(new SentenceSimilarity(s1, s2).value());
    }

}