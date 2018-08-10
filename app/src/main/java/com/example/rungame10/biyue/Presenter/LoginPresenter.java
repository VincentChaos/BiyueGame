package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Model.PostController;
import com.example.rungame10.biyue.Model.RequestLogin;
import com.example.rungame10.biyue.Model.UploadResult;
import com.example.rungame10.biyue.View.LoginDialog;
import com.example.rungame10.biyue.View.ProgressDialog;
import com.example.rungame10.biyue.View.RegisterDialog;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

public class LoginPresenter {

    private Context context;

    private boolean accountLegal = false;       //用户名是否合法全局变量
    private boolean pwdLegal = false;           //密码是否合法全局变量

    private int code;
    private String returnWord;

    private LoginDialog loginDialog;

    public LoginPresenter (Context context,LoginDialog loginDialog){
        this.context = context;
        this.loginDialog = loginDialog;
    }

    public void setAccount(EditText accountEdit, EditText pwdEdit){
        //若SharedPreferences中有保存的账号密码，则自动显示在账号及密码编辑框中
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        if (sharedPreferences.contains("account") && sharedPreferences.contains("password")){
            accountEdit.setText(sharedPreferences.getString("account",""));
            pwdEdit.setText(sharedPreferences.getString("password",""));
        }
    }

    public void startRegister(){
        //注册按钮操作，新建注册窗口，消除登录窗口
        RegisterDialog registerDialog = new RegisterDialog(context);
        registerDialog.show();
        loginDialog.cancel();
    }

    public void wechatLogin(){
        //发起微信登录请求
        if(Config.wx_api.isWXAppInstalled()){
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_login";
            Config.wx_api.sendReq(req);
        }else {
            Toast.makeText(context,"你还没有安装微信",Toast.LENGTH_SHORT).show();
        }
        loginDialog.cancel();
    }

    public void normalLogin(EditText accountEdit, EditText pwdEdit){

        //登录按钮操作
        final String account,pwd;
        account = isAccountLegal(accountEdit);
        pwd = isPwdLegal(pwdEdit);
        if(accountLegal && pwdLegal){
            //账号密码合法，进行登录操作

            //进度条dialog
            final ProgressDialog alertDialog = new ProgressDialog(context);
            alertDialog.setCancelable(false);
            alertDialog.show();

            //打包请求登录对象
            final RequestLogin requestLogin = new RequestLogin();
            requestLogin.setAppid(3);
            requestLogin.setTelephone(account);
            requestLogin.setPassword(pwd);
            requestLogin.setType("appLogin");
            requestLogin.setP("android");

            //新建线程使用OkHttp访问网络
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PostController postController = new PostController(requestLogin);
                    String result = postController.getResult();
                    if (result.equals("00")) {
                        //调用弹出通知窗口方法
                        loginDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                    } else {
                        //用UpLoadResult对象解析获取的json
                        Gson gson = new Gson();
                        UploadResult response = gson.fromJson(result, UploadResult.class);

                        //获取其中code和msg
                        returnWord = response.getMsg().toString();
                        code = response.getCode();
                        Log.e("code"+code,returnWord);
                        if (code == 10001){
                            //code为成功返回时,保存用户名密码至SharedPreferences
                            SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("account",account);
                            editor.putString("password",pwd);
                            editor.apply();
                        }
                        //调用弹出通知窗口方法
                        loginDialog.showNotifyDialog(returnWord);
                    }
                    //进度条dialog消失
                    alertDialog.dismiss();
                }
            }).start();
        }
        //关闭登录窗口
        loginDialog.cancel();
    }

    private String isAccountLegal(EditText editText){
        //判定用户账号编辑框中输入字符是否合法
        String s = editText.getText().toString().trim();
        if (s.equals("")){
            Toast.makeText(context,"用户账号不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }else if(s.length() <= 6){
            Toast.makeText(context,"用户账号长度不能小于6位",Toast.LENGTH_SHORT).show();
            return null;
        }else if(s.length() > 15){
            Toast.makeText(context,"用户账号长度不能大于15位",Toast.LENGTH_SHORT).show();
            return null;
        }else {
            accountLegal = true;
            return s;
        }
    }

    private String isPwdLegal(EditText editText){
        //判定用户密码编辑框中输入字符是否合法
        String s = editText.getText().toString().trim();
        if (s.equals("")){
            Toast.makeText(context,"用户密码不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }else if(s.length() <= 6){
            Toast.makeText(context,"用户密码长度不能小于6位",Toast.LENGTH_SHORT).show();
            return null;
        }else if(s.length() > 15){
            Toast.makeText(context,"用户密码长度不能大于15位",Toast.LENGTH_SHORT).show();
            return null;
        }else {
            pwdLegal = true;
            return s;
        }
    }

}
