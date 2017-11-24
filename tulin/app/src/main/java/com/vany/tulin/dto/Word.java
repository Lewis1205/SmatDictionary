package com.vany.tulin.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by van元 on 2017/1/25.
 */
@Entity
public class Word implements Parcelable{
    @Id(autoincrement = true)
    private Long id;

    private String word;    //单词
    private String enps;    //英式音标
    private String enpronunce;  //英式发音
    private String usps;    //美式音标
    private String uspronunce;  //美式发音
    private String meaning; //意义，解释

    public Word() {
    }
    public Word(String word) {
        this.word = word;
    }

    public Word(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public Word(String word, String enps, String enpronunce, String usps, String uspronunce, String meaning) {
        this.word = word;
        this.enps = enps;
        this.enpronunce = enpronunce;
        this.usps = usps;
        this.uspronunce = uspronunce;
        this.meaning = meaning;
    }

    protected Word(Parcel in) {
        id = in.readLong();
        word = in.readString();
        enps = in.readString();
        enpronunce = in.readString();
        usps = in.readString();
        uspronunce = in.readString();
        meaning = in.readString();
    }
    @Generated(hash = 834888572)
    public Word(Long id, String word, String enps, String enpronunce, String usps, String uspronunce,
            String meaning) {
        this.id = id;
        this.word = word;
        this.enps = enps;
        this.enpronunce = enpronunce;
        this.usps = usps;
        this.uspronunce = uspronunce;
        this.meaning = meaning;
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getEnps() {
        return enps;
    }

    public void setEnps(String enps) {
        this.enps = "【英】"+enps;
    }

    public String getEnpronunce() {
        return enpronunce;
    }

    public void setEnpronunce(String enpronunce) {
        this.enpronunce = enpronunce;
    }

    public String getUsps() {
        return usps;
    }

    public void setUsps(String usps) {
        this.usps = "【美】"+usps;
    }

    public String getUspronunce() {
        return uspronunce;
    }

    public void setUspronunce(String uspronunce) {
        this.uspronunce = uspronunce;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(word);
        dest.writeString(enps);
        dest.writeString(enpronunce);
        dest.writeString(usps);
        dest.writeString(uspronunce);
        dest.writeString(meaning);
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
