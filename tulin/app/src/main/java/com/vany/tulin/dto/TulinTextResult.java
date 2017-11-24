package com.vany.tulin.dto;

import java.util.ArrayList;

/**
 * Created by van元 on 2017/2/10.
 */

public class TulinTextResult {
    private String chatTime;
    private String robotText;
    private String url;
    private ArrayList<CookMenu> cookMenuList;   //菜谱信息
    private ArrayList<News> newsArrayList;  //新闻信息

    public ArrayList<News> getNewsArrayList() {
        return newsArrayList;
    }

    public void setNewsArrayList(ArrayList<News> newsArrayList) {
        this.newsArrayList = newsArrayList;
    }

    public ArrayList<CookMenu> getCookMenuList() {
        return cookMenuList;
    }

    public void setCookMenuList(ArrayList<CookMenu> cookMenuList) {
        this.cookMenuList = cookMenuList;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = "☆ "+chatTime+" ☆";
    }


    public String getRobotText() {
        return robotText;
    }

    public void setRobotText(String robotText) {
        this.robotText = robotText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
