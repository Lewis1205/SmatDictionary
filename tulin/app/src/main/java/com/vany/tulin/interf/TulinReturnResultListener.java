package com.vany.tulin.interf;

/**
 * Created by van元 on 2017/1/22.
 */

public interface TulinReturnResultListener {
    void onSuccess(String json);

    void onFail(int i, String s);
}
