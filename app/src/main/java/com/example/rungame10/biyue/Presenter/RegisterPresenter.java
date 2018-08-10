package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rungame10.biyue.Model.PostController;
import com.example.rungame10.biyue.Model.RequestLogin;
import com.example.rungame10.biyue.Model.UploadResult;
import com.example.rungame10.biyue.View.LoginDialog;
import com.google.gson.Gson;

public class RegisterPresenter {
    private Context context;

    private boolean accountLegal = false;       //用户名是否合法全局变量
    private boolean pwdLegal = false;           //密码是否合法全局变量

    private int code;
    private String returnWord;

    public RegisterPresenter(Context context){
        this.context = context;
    }

    public void sendVeri(EditText editText){
        //绑定手机号后发送验证码
        String account = isAccountLegal(editText);
        if(accountLegal){
            //发送验证码操作

            //重置账号合法判别
            accountLegal = false;
        }
    }

    public void register(EditText accountEdit,EditText veriEdit,EditText pwdEdit){
        //注册按钮操作
        String account = isAccountLegal(accountEdit);
        String pwd = isPwdLegal(pwdEdit);
        String code = veriEdit.getText().toString().trim();
        if (accountLegal && pwdLegal){
            if(code.equals("")){
                Toast.makeText(context,"验证码不能为空",Toast.LENGTH_SHORT).show();
            }else {
                Log.e("doRegister",".........");
                RequestLogin requestLogin = new RequestLogin();
                requestLogin.setAppid(3);
                requestLogin.setP("android");
                requestLogin.setType("appRegister");
                requestLogin.setTelephone(account);
                requestLogin.setPassword(pwd);
                requestLogin.setCode(code);
                new PostLogin(requestLogin).start();
            }
        }
    }

    public void returnLogin(){
        //返回按钮
        LoginDialog loginDialog = new LoginDialog(context);
        loginDialog.show();
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

    private class PostLogin extends Thread{
        private Object object;

        public PostLogin(Object object){
            this.object = object;
        }

        public void run(){
            PostController postController = new PostController(object);
            String result = postController.getResult();
            if (result.equals("00")) {
                this.interrupt();
            } else {
                //解析获取的json
                Gson gson = new Gson();
                UploadResult response = gson.fromJson(result, UploadResult.class);
                returnWord = response.getMsg().toString();
                code = response.getCode();
                Log.e("code"+code,result);
            }
        }
    }
}
