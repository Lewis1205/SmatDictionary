package com.vany.tulin.service;

import android.content.Context;

import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.TuringManager;
import com.vany.tulin.common.Constant;
import com.vany.tulin.interf.TulinReturnResultListener;

/**
 * Created by van元 on 2017/1/22.
 */

public class TulinService implements HttpRequestListener{

    private Context context;
    private TuringManager turingManager;
    private TulinReturnResultListener tulinReturnResultListener;    //图灵返回结果接口

    public TulinService(Context context) {
        this.context = context;
        turingManager = new TuringManager(context, Constant.TLAPI_KEY, Constant.TLSECRET_KEY);  //实例化图灵机器人管理类
        turingManager.setHttpRequestListener(this); //设置发送请求监听
    }

    /**
     * 向图灵API发送请求
     * @param info
     */
    public void sendRequest(String info) {
        turingManager.requestTuring(info);
    }

    /**
     * 设置图灵返回结果监听
     */
    public void setOnTulinReturnResultListener(TulinReturnResultListener tulinReturnResultListener){
        this.tulinReturnResultListener = tulinReturnResultListener;
    }
    @Override
    public void onSuccess(String s) {
        tulinReturnResultListener.onSuccess(s);
    }

    @Override
    public void onFail(int i, String s) {
        tulinReturnResultListener.onFail(i,s);

    }
}
