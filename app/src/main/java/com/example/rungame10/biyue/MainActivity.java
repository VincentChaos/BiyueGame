package com.example.rungame10.biyue;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.rungame10.biyue.Presenter.LibController;
import com.example.rungame10.biyue.Presenter.PostController;
import com.example.rungame10.biyue.Model.RequestLoginAndRegister;
import com.example.rungame10.biyue.Intf.JsonResult;
import com.example.rungame10.biyue.Util.HttpCallBackListener;
import com.example.rungame10.biyue.Util.HttpUtil;
import com.example.rungame10.biyue.View.LoginDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button loginBtn;        //登录按钮
    Button infoBtn;         //获取用户信息按钮
    Button payButton;
    TextView showText;

    private String returnWord;

    private int code;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
            switch (msg.what){
                case 0:
                    alertDialog.setTitle("提示");
                    alertDialog.setMessage("服务器连接异常，请更换网络环境");
                    alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                    break;
                case 1:
                    showText.setText(returnWord);
                    break;
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
                LibController libController = new LibController(MainActivity.this);
                libController.showLoginDialog();
            }
        });

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPersonMessage();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_info",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
            }
        });

    }



    private void getPersonMessage() {
        SharedPreferences sharedPreferences = getSharedPreferences("wechat_info",Context.MODE_PRIVATE);
        String access_token = sharedPreferences.getString("access_token","");
        String openid = sharedPreferences.getString("open_id","");
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
                    handler.sendEmptyMessage(1);
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

    class PostThread extends Thread{
        private Object object;

        public PostThread(Object o){
            this.object = o;
        }

        public void run(){
            PostController postController = new PostController(object);
            String result = postController.getResult();
            if (result.equals("00")) {
                Message msg = Message.obtain();
                msg.what = 0;
                this.interrupt();
                handler.sendMessage(msg);
            } else {
                  //解析获取的json
                Gson gson = new Gson();
                JsonResult response = gson.fromJson(result, JsonResult.class);
                returnWord = response.getMsg().toString();
                code = response.getCode();
                Log.e("code"+code,result);
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
    }
}
