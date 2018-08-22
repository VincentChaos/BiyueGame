package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Util.HttpCallBackListener;
import com.example.rungame10.biyue.Util.HttpUtil;
import com.example.rungame10.biyue.View.LoginDialog;
import com.example.rungame10.biyue.View.PayDialog;
import com.example.rungame10.biyue.View.ProgressDialog;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class LibController {

    private Context context;
    private String WXStringInfo;

    public LibController(Context context){
        this.context = context;
    }

    public void showLoginDialog(){
        //登录操作
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        if(sharedPreferences.contains("account")&&sharedPreferences.contains("password")){
            LoginDialog loginDialog = new LoginDialog(context);
            LoginPresenter loginPresenter = new LoginPresenter(context,loginDialog);
            loginPresenter.quickLogin(sharedPreferences.getString("account",""),sharedPreferences.getString("password",""));
        }else {
            LoginDialog loginDialog = new LoginDialog(context);
            loginDialog.show();
        }
    }

    public void doPay (double money, @Nullable String ext){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        if(sharedPreferences.contains("openid")){
            String uid = sharedPreferences.getString("openid","");
            PayDialog payDialog = new PayDialog(context,uid,money,ext);
            payDialog.show();
        }
    }

    private void regToWx(){
        //通过WXAPI工厂，获取api实例
        Config.wx_api = WXAPIFactory.createWXAPI(context, Config.WX_APP_ID,true);
        Config.wx_api.registerApp(Config.WX_APP_ID);
    }


    public void WXLogin(){
        regToWx();
        if(Config.wx_api.isWXAppInstalled()){
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_login";
            Config.wx_api.sendReq(req);
        }else {
            Toast.makeText(context,"你还没有安装微信",Toast.LENGTH_SHORT).show();
        }
    }

    public void startedFloatWin(){
        FloatActionController.getInstance().startServer(context);
    }

    public void getPersonMessage() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wechat_info",Context.MODE_PRIVATE);
        String access_token = sharedPreferences.getString("access_token","");
        String openid = sharedPreferences.getString("open_id","");
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + access_token
                + "&openid="
                + openid;
        Log.e("url:",url);
        HttpUtil.sendHttpRequest(url, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("略略略", "------获取到的个人信息------" + jsonObject.toString());
                    WXStringInfo = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "通过openid获取数据没有成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getWXStringInfo(){
        return WXStringInfo;
    }

}
