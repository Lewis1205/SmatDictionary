package com.vany.tulin.controller;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.vany.tulin.R;

public class TBSActivity extends AppCompatActivity {
    private WebView tbsContent; //该webview是用腾讯公司封装的，不是安卓系统自带的
    private boolean isShow =false; //是否显示标题栏,默认不显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tbs);

        getWindow().setFormat(PixelFormat.TRANSLUCENT); //网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //避免输入法界面弹出后遮挡输入光标的问题
        initView();
    }

    private void initView() {
        tbsContent = (WebView) findViewById(R.id.tbsContent);
        String url = getIntent().getStringExtra("url");
        tbsContent.loadUrl(url);   //百度搜索地址："https://www.baidu.com/s?wd=刘亦菲"
        WebSettings webSettings = tbsContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        tbsContent.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return false;
            }
        });

        final RelativeLayout mTitleBar = (RelativeLayout) findViewById(R.id.mTitleBar);   //退出标题栏
        TextView switchshoworhideTV = (TextView) findViewById(R.id.switchshoworhideTV);
        switchshoworhideTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShow) {
                    mTitleBar.setVisibility(View.VISIBLE);
                    isShow = true;
                } else {
                    mTitleBar.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });

        Button exitBtn = (Button) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && tbsContent.canGoBack()) {
            tbsContent.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
