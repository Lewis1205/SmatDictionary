package com.vany.tulin.utils;

import com.vany.tulin.dto.CookMenu;
import com.vany.tulin.dto.News;
import com.vany.tulin.dto.TulinTextResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by van元 on 2017/2/10.
 */

public class TulinParserUtil {


    public static TulinTextResult jsonParser(String json) {
        try {
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
            JSONObject jsonObject = new JSONObject(json);
            String code = jsonObject.getString("code");
            switch (code) {
                case "100000":
                    TulinTextResult ttr = new TulinTextResult();
                    ttr.setChatTime(sf.format(new Date()));
                    ttr.setRobotText(jsonObject.getString("text"));
                    return ttr;
                case "200000":
                    TulinTextResult ttr2 = new TulinTextResult();
                    ttr2.setChatTime(sf.format(new Date()));
                    ttr2.setUrl(jsonObject.getString("url"));
                    ttr2.setRobotText(jsonObject.getString("text"));
                    return ttr2;
                case "302000":
                    TulinTextResult ttr3 =new TulinTextResult();
                    ttr3.setChatTime(sf.format(new Date()));
                    ttr3.setRobotText(jsonObject.getString("text"));
                    JSONArray newslist = jsonObject.getJSONArray("list");
                    ArrayList<News> newsArrayList = new ArrayList<News>();
                    String timeformat = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    for (int i=0 ;i<newslist.length();i++) {
                        JSONObject newsjobj = newslist.getJSONObject(i);
                        News news = new News(newsjobj.getString("article"),
                                timeformat, newsjobj.getString("source"),
                                newsjobj.getString("detailurl"));
                        newsArrayList.add(news);
                    }
                    ttr3.setNewsArrayList(newsArrayList);
                    return ttr3;
                case "308000":
                    TulinTextResult ttr4 = new TulinTextResult();
                    ttr4.setChatTime(sf.format(new Date()));
                    ttr4.setRobotText(jsonObject.getString("text"));
                    JSONArray list = jsonObject.getJSONArray("list");
                    ArrayList<CookMenu> cookMenuList = new ArrayList<CookMenu>();
                    for (int i=0;i<list.length();i++) {
                        JSONObject jsonobj = list.getJSONObject(i);
                        CookMenu cookMenu = new CookMenu(jsonobj.getString("name"),
                                jsonobj.getString("info"),jsonobj.getString("detailurl"));
                        cookMenuList.add(cookMenu);
                    }
                    ttr4.setCookMenuList(cookMenuList);
                    return ttr4;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


}
