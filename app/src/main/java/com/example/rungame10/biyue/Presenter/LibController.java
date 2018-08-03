package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.MainActivity;
import com.example.rungame10.biyue.Model.HttpCallBackListener;
import com.example.rungame10.biyue.Model.HttpUtil;
import com.example.rungame10.biyue.View.LoginDialog;
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
        LoginDialog loginDialog = new LoginDialog(context);
        loginDialog.show();
    }

    private void regToWx(){
        //通过WXAPI工厂，获取api实例
        Config.wx_api = WXAPIFactory.createWXAPI(context, Config.APP_ID,true);
        Config.wx_api.registerApp(Config.APP_ID);
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

    public void getWXInfo(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        String code = sharedPreferences.getString("code","");
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        String WXURL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + Config.APP_ID
                + "&secret="
                + Config.APP_SECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";

        //请求获取微信登录的access_token
        HttpUtil.sendHttpRequest(WXURL, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                //解析以及存储获取到的信息
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String accessToken = jsonObject.getString("access_token");
                    Log.e("access_token:",accessToken);
                    String openId = jsonObject.getString("openid");
                    String refreshToken = jsonObject.getString("refresh_token");
                    Log.e("openid:"+openId,"refreshToken:"+refreshToken);
                    if(!accessToken.equals("")){
                        editor.putString("access_token",accessToken);
                        editor.apply();
                    }
                    if(!refreshToken.equals("")){
                        editor.putString("refresh_token",refreshToken);
                        editor.apply();
                    }
                    if(!openId.equals("")){
                        editor.putString("open_id",openId);
                        editor.apply();
                        getPersonMessage(accessToken,openId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void getPersonMessage(String access_token, String openid) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + access_token
                + "&openid="
                + openid;
        Log.e("url",url);
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
            }
        });
    }

    public String getWXStringInfo(){
        return WXStringInfo;
    }

}
