package com.example.rungame10.biyue.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Model.HttpCallBackListener;
import com.example.rungame10.biyue.Model.HttpUtil;
import com.example.rungame10.biyue.Presenter.FloatActionController;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
    private static final int TIMEOUT_IN_MILLIONS = 5000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Config.wx_api.handleIntent(getIntent(),this);
        Log.i("savedInstanceState"," sacvsa"+Config.wx_api.handleIntent(getIntent(), this));

    }


    @Override
    public void onReq(BaseReq baseReq) {
        Log.d("Entry:","baseReq:"+ baseReq.toString());
    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp baseResp) {
        String result = "";
        SendAuth.Resp re = ((SendAuth.Resp) baseResp);
        String code = re.code;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                Log.i("result:",result+"code: "+code);
                SharedPreferences getData = getSharedPreferences("wechat_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = getData.edit();
                editor.putString("code", ((SendAuth.Resp) baseResp).code);
                editor.apply();
                getAccessToken();
                FloatActionController.isLogined  = true;
                FloatActionController.getInstance().startServer(WXEntryActivity.this);
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                Log.i("result:",result);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                Log.i("result:",result);
                break;
            default:
                result = "发送返回";
                Log.i("result:"+result,baseResp.errCode+""+((SendAuth.Resp) baseResp).code);
                break;
        }
        finish();
    }

    private void getAccessToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("wechat_info",Context.MODE_PRIVATE);
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

}
