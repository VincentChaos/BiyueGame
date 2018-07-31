package com.example.rungame10.biyue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Model.HttpCallBackListener;
import com.example.rungame10.biyue.Model.HttpUtil;
import com.example.rungame10.biyue.View.LoginDialog;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button loginBtn;        //登录按钮
    Button infoBtn;         //获取用户信息按钮
    Button payButton;
    TextView showText;

    private String returnWord;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(!returnWord.equals("")) {
                showText.setText(returnWord);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){

        loginBtn = (Button)this.findViewById(R.id.btn_login);
        infoBtn = (Button)this.findViewById(R.id.btn_test);
        payButton = (Button)this.findViewById(R.id.btn_pay);
        showText = (TextView)this.findViewById(R.id.text_show);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginDialog loginDialog = new LoginDialog(MainActivity.this);
                loginDialog.show();
            }
        });

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAccessToken();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EmptyActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getAccessToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_info",Context.MODE_PRIVATE);
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
                    returnWord = jsonObject.toString();
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, "通过openid获取数据没有成功", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
