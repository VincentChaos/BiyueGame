package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.View.RegisterDialog;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

public class LoginPresenter {

    private Context context;

    private boolean accountLegal = false;       //用户名是否合法全局变量
    private boolean pwdLegal = false;           //密码是否合法全局变量

    public LoginPresenter (Context context){
        this.context = context;
    }

    public void setAccount(EditText accountEdit, EditText pwdEdit){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        if (sharedPreferences.contains("account") && sharedPreferences.contains("password")){
            accountEdit.setText(sharedPreferences.getString("account",""));
            pwdEdit.setText(sharedPreferences.getString("password",""));
        }
    }

    public void startRegister(){
        RegisterDialog registerDialog = new RegisterDialog(context);
        registerDialog.show();
        FloatActionController.getInstance().stopServer(context);
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
    }

    public void normalLogin(EditText accountEdit, EditText pwdEdit){
        String account,pwd;
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        account = isAccountLegal(accountEdit);
        pwd = isPwdLegal(pwdEdit);
        if(accountLegal && pwdLegal){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("account",account);
            editor.putString("password",pwd);
            editor.apply();
            Toast.makeText(context,"略略略",Toast.LENGTH_SHORT).show();

        }
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
