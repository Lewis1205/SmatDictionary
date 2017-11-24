package com.vany.tulin.dto;

import java.io.InputStream;

/**
 * Created by van元 on 2017/1/26.
 */

public class Sentence {

    private String pic;
    private String date;
    private String pronunce;
    private String sentence;
    private String meaning;

    private InputStream picIS;

    public InputStream getPicIS() {
        return picIS;
    }

    public void setPicIS(InputStream picIS) {
        this.picIS = picIS;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPronunce() {
        return pronunce;
    }

    public void setPronunce(String pronunce) {
        this.pronunce = pronunce;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
