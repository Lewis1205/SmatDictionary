package com.vany.tulin.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vany.tulin.common.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by van元 on 2017/1/25.
 */

public class NetUtil {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * get请求
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 通过url解析网络上图片
     * @param url
     * @return
     * @throws IOException
     */
    public static void getPic(String url, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * post请求
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }
    /**
     * 向易源在线词典API发送请求
     * @return
     */
    public static String postYiYuan(String sendText) throws IOException {
//        String json = "{'showapi_appid':'30866'," +
//                "'showapi_sign':'c12f7fa1ab964a33842da04f68b96a22'," +
//                "'q':'" + sendText + "'}";
//        RequestBody body = RequestBody.create(JSON, json);
//        Request request = new Request.Builder()
//                .url(Constant.YIYUANURL)
//                .post(body)
//                .build();
//        Response response = client.newCall(request).execute();
        Request req =new Request.Builder().url(Constant.GET_YIYUANURL+sendText).build();//用get请求
        Response response =okHttpClient.newCall(req).execute();
        return response.body().string();
    }
    public static String getDataFromYiYuanAPI(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        Integer code = (Integer) jsonObject.get("showapi_res_code");
        if (code==0){
            JSONObject body = (JSONObject) jsonObject.get("showapi_res_body");
            JSONObject s = (JSONObject) body.get("basic");
            JSONArray jsonArray = (JSONArray) s.get("explains");
            String ret ="";
            for (int i=0;i<jsonArray.length();i++){
                ret += jsonArray.get(i).toString() + "\n";
            }
            if (ret.contains("[")) {
                ret = ret.replaceAll("[\\[\\]\"]", "");
            }
            System.out.println(ret);
            return ret;
        }else {
            return "出错啦，请重试！";
        }
    }

    /**
     * 判断网络是否可用
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm!=null){
            NetworkInfo ani = cm.getActiveNetworkInfo();
            if (ani!=null){
                if (ani.isConnected())
                    return true;
            }
        }
        return false;
    }

    /**
     * 判断是否连接wifi
     * @param context
     * @return
     */
    public static boolean isWifi(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm!=null){
            NetworkInfo ani = cm.getActiveNetworkInfo();
            if (ani!=null){
                if (ani.getType() == ConnectivityManager.TYPE_WIFI)
                    return true;
            }
        }
        return false;
    }

}
