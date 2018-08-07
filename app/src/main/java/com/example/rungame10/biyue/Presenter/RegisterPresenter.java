package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rungame10.biyue.View.LoginDialog;

public class RegisterPresenter {
    private Context context;

    private boolean accountLegal = false;       //用户名是否合法全局变量
    private boolean pwdLegal = false;           //密码是否合法全局变量

    public RegisterPresenter(Context context){
        this.context = context;
    }

    public void sendVeri(EditText editText){
        //绑定手机号后发送验证码
        String account = isAccountLegal(editText);

    }

    public void register(){
        //注册按钮操作
    }

    public void returnLogin(){
        //返回按钮
        LoginDialog loginDialog = new LoginDialog(context);
        loginDialog.show();
        FloatActionController.getInstance().stopServer(context);
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
