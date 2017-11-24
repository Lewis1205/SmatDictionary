package com.vany.tulin.utils;

import android.util.Xml;

import com.vany.tulin.dto.Sentence;
import com.vany.tulin.dto.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;

/**
 * Created by van元 on 2017/1/25.
 */

public class XmlParserUtil {

    private static XmlPullParser parser = Xml.newPullParser();

    /**
     * 解析中文json数据
     * @param json
     * @return
     */
    public static Word chineseWordParser(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String word_name = jsonObject.getString("word_name");
            JSONArray jarray = jsonObject.getJSONArray("symbols");
            JSONArray jsonArray = jarray.getJSONObject(0).getJSONArray("parts")
                                        .getJSONObject(0).getJSONArray("means");
            String ret = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                ret += jsonArray.getJSONObject(i).getString("word_mean") + "\n";
            }
            Word word = new Word();
            word.setWord(word_name);
            word.setMeaning(ret);
            System.out.println(ret);
            return word;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;  //返回空
    }

    /**
     * 解析单词xml字符串
     * @param word
     * @return
     */
    public static Word wordParser(String word) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(word.getBytes());
        try {
            parser.setInput(byteArrayInputStream, "utf-8");
            int eventType = parser.getEventType();
            Word w = null;
            boolean psflag = true; //用于区分英美式音标
            boolean proflag = true; //用于区分英美式发音
            String s = "";      //拼接单词的意义

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        w = new Word();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("dict".equals(parser.getName())) {

                        } else if ("key".equals(parser.getName())) {
                            w.setWord(parser.nextText());   //设置单词
                        } else if ("ps".equals(parser.getName())) {
                            if (psflag) {
                                w.setEnps(parser.nextText());   //第一次设置英式音标
                                psflag = false;
                            } else {
                                w.setUsps(parser.nextText());   //第二次设置美式音标
                            }
                        } else if ("pron".equals(parser.getName())) {
                            if (proflag) {
                                w.setEnpronunce(parser.nextText());     //第一次设置英式发音
                                proflag = false;
                            } else {
                                w.setUspronunce(parser.nextText());     //第二次设置美式发音
                            }
                        } else if ("pos".equals(parser.getName())) {
                            s += parser.nextText();
                        } else if ("acceptation".equals(parser.getName())) {
                            s += parser.nextText();
                            s += "\n";
                        } else if ("sent".equals(parser.getName())) {
                            //当解析到sent例子的时候，就返回就可以了
                            w.setMeaning(s);
                            return w;
                        }
                       break;
                    case XmlPullParser.END_TAG: //这个情况是：当不存在这个单词的时候返回
                        if ("dict".equals(parser.getName())) {
                            w.setMeaning(s);
                            return w;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析每日一句xml字符串
     * @param sentence
     */
    public static Sentence sentenceParser(String sentence) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sentence.getBytes());
        try {
            parser.setInput(byteArrayInputStream, "utf-8");
            int eventType = parser.getEventType();
            Sentence sent = null;
            String s = "";      //拼接单词的意义

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        sent = new Sentence();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("tts".equals(parser.getName())) {
                            sent.setPronunce(parser.nextText());    //设置发音
                        } else if ("content".equals(parser.getName())) {
                            sent.setSentence(parser.nextText());    //设置句子
                        } else if ("note".equals(parser.getName())) {
                            sent.setMeaning(parser.nextText());     //设置意义
                        } else if ("picture".equals(parser.getName())) {
                            sent.setPic(parser.nextText());     //设置图片
                        } else if ("dateline".equals(parser.getName())) {
                            sent.setDate(parser.nextText());    //设置日期
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("Document".equals(parser.getName())) {
                            return sent;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
