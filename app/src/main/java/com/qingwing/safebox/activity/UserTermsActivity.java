package com.qingwing.safebox.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.qingwing.safebox.R;
import com.qingwing.safebox.utils.AcitivityCollector;


public class UserTermsActivity extends Activity {
    private final String USER_AGREEMENT = "http://www.keenzy.cn/mobilePages/agree.jsp";
    private WebView webView;
    private ImageView image_callback;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_terms);
        AcitivityCollector.addActivity(this);
        webView = (WebView) findViewById(R.id.wv_userterms);
        image_callback = (ImageView) findViewById(R.id.image_callback);
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    private void initData() {
        webView.loadUrl(USER_AGREEMENT);
        //覆盖WebView默认通过第三方或者系统浏览器打开网页的行为，使得网页可以在WebView中打开。
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是TRUE的时候控制在网页在WebView中打开，如果是FALSE调用系统浏览器打开。
                view.loadUrl(url);
                return true;
                //WebView帮助WebView去处理一些页面控制，和请求通知。
            }
        });
        //启用支持javaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //加载页面页面优先使用缓存
        //				webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 加载完成
                    closeDialog();
                } else {
                    openDialog(newProgress);
                }
            }

            private void openDialog(int newProgress) {
                if (mDialog == null) {
                    mDialog = new ProgressDialog(UserTermsActivity.this);
                    mDialog.setTitle("正在加载");
                    mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mDialog.setProgress(newProgress);
                    mDialog.show();
                } else {
                    mDialog.setProgress(newProgress);
                }
            }

            private void closeDialog() {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        });

    }

    private void initEvent() {
        image_callback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    Intent resultIntent = new Intent();
                    UserTermsActivity.this.setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }
}
