package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.rungame10.biyue.Model.JsonResult;
import com.example.rungame10.biyue.Model.Pay;
import com.example.rungame10.biyue.Util.MResource;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PayDialog extends AlertDialog{
    private Context context;
    private WebView webView;
    private TextView closeBtn;
    private WebSettings webSettings;
    private String uid;
    private double money;
    private String ext;

    public PayDialog(@Nullable Context context,String uid,double money,@Nullable String ext){
        super(context, MResource.getIdByName(context,"style","by_Dialog"));
        this.context = context;
        this.uid = uid;
        this.money = money;
        this.ext = ext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(MResource.getIdByName(context,"layout","by_dialog_pay"),null);
        setContentView(view);

        //声明
        webView = (WebView)view.findViewById(MResource.getIdByName(context,"id","by_web_view"));
        closeBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_btn_close"));

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        webView.setWebChromeClient( new WebChromeClient(){
            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress < 100) {

                } else if (newProgress == 100) {
                    progressDialog.dismiss();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("weixin://wap/pay?")){
                    //识别是否是微信支付url地址
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);
                    cancel();
                    return true;
                }

                if (url.contains("platformapi/startApp")) {
                    //识别支付宝支付
                    startAlipayActivity(url);
                } else if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                        && (url.contains("platformapi") && url.contains("startApp"))) {
                    startAlipayActivity(url);
                }

                if(url.contains("code")){

                }

                return super.shouldOverrideUrlLoading(view,url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {//网页页面开始加载的时候
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                //检测网页状态码，若为400即返回参数错误
                Log.e("code:",errorResponse.getStatusCode()+"");
                if (errorResponse.getStatusCode() == 400){
                    NotifyDialog notifyDialog = new NotifyDialog(context,"初始化参数错误");
                    notifyDialog.show();
                    cancel();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
                NotifyDialog notifyDialog = new NotifyDialog(context,description);
                notifyDialog.show();
                cancel();
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                progressDialog.dismiss();
            }
        });

        Pay pay = new Pay(uid,money,ext);
        webView.loadUrl(pay.getUrl());

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayDialog.this.cancel();
            }
        });

        //设置宽高
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高

        //判断当前是否横屏
        Configuration configuration = context.getResources().getConfiguration();
        int ori = configuration.orientation;
        if(ori == Configuration.ORIENTATION_LANDSCAPE){
            //横屏
            lp.width = (int) (d.widthPixels*0.5);
            lp.height = (int) (d.heightPixels*0.9);

        }else if(ori == Configuration.ORIENTATION_PORTRAIT){
            //竖屏
            lp.width = (int) (d.widthPixels*0.8);
            lp.height = (int) (d.heightPixels*0.5);
        }

        dialogWindow.setAttributes(lp);

        //显示alertDialog的软键盘
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    // 调起支付宝并跳转到指定页面
    private void startAlipayActivity(String url) {
        Intent intent;
        try {
            intent = Intent.parseUri(url,
                    Intent.URI_INTENT_SCHEME);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setComponent(null);
            context.startActivity(intent);
            cancel();
        } catch (Exception e) {

        }
    }


    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁WebView
    @Override
    public void cancel() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.cancel();
    }
}
