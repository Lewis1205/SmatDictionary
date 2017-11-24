package com.vany.tulin.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by van元 on 2017/2/15.
 */

public class News implements Parcelable{
    private String newsArticle;
    private String newsTime;
    private String newsSource;
    private String newsUrl;

    public News() {
    }

    public News(String newsArticle, String newsTime, String newsSource, String newsUrl) {
        this.newsArticle = newsArticle;
        this.newsTime = newsTime;
        this.newsSource = newsSource;
        this.newsUrl = newsUrl;
    }

    protected News(Parcel in) {
        newsArticle = in.readString();
        newsTime = in.readString();
        newsSource = in.readString();
        newsUrl = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public String getNewsArticle() {
        return newsArticle;
    }

    public void setNewsArticle(String newsArticle) {
        this.newsArticle = newsArticle;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(newsArticle);
        dest.writeString(newsTime);
        dest.writeString(newsSource);
        dest.writeString(newsUrl);
    }
}
