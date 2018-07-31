package com.example.rungame10.biyue.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Model.BaseActivity;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
                SharedPreferences getData = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = getData.edit();
                editor.putString("code", ((SendAuth.Resp) baseResp).code);
                editor.apply();
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
                Log.i("result:",result);
                break;
        }
        finish();
    }

}
