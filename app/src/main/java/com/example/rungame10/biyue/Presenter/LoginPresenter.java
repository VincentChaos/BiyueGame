package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Model.RequestLoginAndRegister;
import com.example.rungame10.biyue.Intf.JsonResult;
import com.example.rungame10.biyue.Model.RequestOneKey;
import com.example.rungame10.biyue.Model.ResponseMsg;
import com.example.rungame10.biyue.View.LoginDialog;
import com.example.rungame10.biyue.View.ProgressDialog;
import com.example.rungame10.biyue.View.RegisterDialog;
import com.example.rungame10.biyue.View.VerifyDialog;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
            final RequestLoginAndRegister requestLoginAndRegister = new RequestLoginAndRegister();
            requestLoginAndRegister.setAppid(Config.APP_ID);
            requestLoginAndRegister.setTelephone(account);
            requestLoginAndRegister.setPassword(pwd);
            requestLoginAndRegister.setType("appLogin");

            //新建线程使用OkHttp访问网络
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PostController postController = new PostController(requestLoginAndRegister);
                    String result = postController.getResult();
                    if (result.equals("00")) {
                        //调用弹出通知窗口方法
                        loginDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                    } else {
                        //用UpLoadResult对象解析获取的json
                        Gson gson = new Gson();
                        JsonResult response = gson.fromJson(result, JsonResult.class);

                        //获取其中code
                        code = response.getCode();
                        if (code == 10001){
                            //登录成功
                            FloatActionController.isLogined = true;
                            //因Gson解析时将msg类转换成键值对，则用TreeMap获取
                            LinkedTreeMap linkedTreeMap = (LinkedTreeMap)response.getMsg();

                            //新建回复实体类，获取TreeMap中的值
                            ResponseMsg getResponse = new ResponseMsg(linkedTreeMap);
                            returnWord = "登录成功，用户名为："+getResponse.getUsername();
                            //保存用户名密码至SharedPreferences
                            SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("account",account);
                            editor.putString("password",pwd);
                            editor.putString("user_name",getResponse.getUsername());
                            editor.putString("openid",getResponse.getOpenid());
                            editor.putString("have_phone",getResponse.getHavePhone());
                            editor.apply();

                            //调用弹出通知窗口方法
                            loginDialog.showNotifyDialog(returnWord,1);
                        }else {
                            //调用弹出通知登录失败
                            loginDialog.showNotifyDialog((String) response.getMsg());
                        }
                    }
                    //进度条dialog消失
                    alertDialog.dismiss();
                }
            }).start();
        }
    }

    public void OneKeyLogin(){
        //进度条dialog
        final ProgressDialog alertDialog = new ProgressDialog(context);
        alertDialog.setCancelable(false);
        alertDialog.show();
        loginDialog.cancel();

        //打包一键登录请求
        final RequestOneKey requestOneKey = new RequestOneKey();
        requestOneKey.setAppid(Config.APP_ID);
        requestOneKey.setType("appOneKey");

        //新建线程请求网络
        new Thread(new Runnable() {
            @Override
            public void run() {
                PostController postController = new PostController(requestOneKey);
                String result = postController.getResult();
                if(result.equals("00")){
                    loginDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                }else {
                    //用UpLoadResult对象解析获取的json
                    Gson gson = new Gson();
                    JsonResult response = gson.fromJson(result, JsonResult.class);

                    //获取其中code
                    int code = response.getCode();
                    if(code == 10001){
                        //一键注册成功
                        LinkedTreeMap linkedTreeMap = (LinkedTreeMap)response.getMsg();
                        ResponseMsg getResponse = new ResponseMsg(linkedTreeMap);
                        String returnWord = "一键注册成功，\n您的账号是："+getResponse.getUsername()+"\n您的密码是："+getResponse.getPassword();
                        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("account",getResponse.getUsername());
                        editor.putString("password",getResponse.getPassword());
                        editor.putString("openid",getResponse.getOpenid());
                        editor.apply();

                        loginDialog.showNotifyDialog(returnWord,2);
                    }else {
                        loginDialog.showNotifyDialog((String )response.getMsg());
                    }
                }
                //进度条dialog消失
                alertDialog.dismiss();
            }
        }).start();
    }

    public void forgetPwd(){
        //忘记密码操作，新建验证码验证窗口，消除登录窗口
        VerifyDialog verifyDialog = new VerifyDialog(context);
        verifyDialog.show();
        loginDialog.cancel();
    }

    public void quickLogin(final String account, final String password){
        loginDialog.cancel();

        //进度条dialog
        final ProgressDialog alertDialog = new ProgressDialog(context);
        alertDialog.setCancelable(false);
        alertDialog.show();
        //打包请求登录对象
        final RequestLoginAndRegister requestLoginAndRegister = new RequestLoginAndRegister();
        requestLoginAndRegister.setAppid(Config.APP_ID);
        requestLoginAndRegister.setTelephone(account);
        requestLoginAndRegister.setPassword(password);
        requestLoginAndRegister.setType("appLogin");

        //新建线程使用OkHttp访问网络
        new Thread(new Runnable() {
            @Override
            public void run() {
                PostController postController = new PostController(requestLoginAndRegister);
                String result = postController.getResult();
                if (result.equals("00")) {
                    //调用弹出通知窗口方法
                    loginDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                } else {
                    //用UpLoadResult对象解析获取的json
                    Gson gson = new Gson();
                    JsonResult response = gson.fromJson(result, JsonResult.class);

                    //获取其中code
                    code = response.getCode();
                    if (code == 10001){
                        //登录成功
                        FloatActionController.isLogined = true;
                        //因Gson解析时将msg类转换成键值对，则用TreeMap获取
                        LinkedTreeMap linkedTreeMap = (LinkedTreeMap)response.getMsg();

                        //新建回复实体类，获取TreeMap中的值
                        ResponseMsg getResponse = new ResponseMsg(linkedTreeMap);
                        returnWord = "登录成功，用户名为："+getResponse.getUsername();

                        //保存用户名密码至SharedPreferences
                        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("account",account);
                        editor.putString("password",password);
                        editor.putString("user_name",getResponse.getUsername());
                        editor.putString("openid",getResponse.getOpenid());
                        editor.putString("have_phone",getResponse.getHavePhone());
                        editor.apply();

                        //调用弹出通知窗口方法
                        loginDialog.showNotifyDialog(returnWord,1);
                    }else {
                        //调用弹出通知登录失败
                        loginDialog.showNotifyDialog((String) response.getMsg());
                    }
                }
                //进度条dialog消失
                alertDialog.dismiss();
            }
        }).start();
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
