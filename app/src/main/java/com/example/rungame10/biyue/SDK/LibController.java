package com.example.rungame10.biyue.SDK;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Intf.LoginCallBack;
import com.example.rungame10.biyue.Intf.LogoutCallBack;
import com.example.rungame10.biyue.Intf.InitCallBack;
import com.example.rungame10.biyue.Intf.ResultCode;
import com.example.rungame10.biyue.Presenter.DES;
import com.example.rungame10.biyue.Presenter.LoginPresenter;
import com.example.rungame10.biyue.View.LoginDialog;
import com.example.rungame10.biyue.View.NotifyDialog;
import com.example.rungame10.biyue.View.PayDialog;

public class LibController {

    private Context context;

    public LibController(Context context){
        this.context = context;
    }

    public static LibController getInstance(Context context){
        return new LibController(context);
    }

    public void init(String appId,String pId,String key,InitCallBack initCallBack){
        Config.APP_ID = appId;
        Config.P_ID = pId;
        Config.KEY = key;
        if (Config.APP_ID.equals("")||Config.P_ID.equals("")||Config.KEY.equals("")){
            initCallBack.onResponse(ResultCode.INIT_FAIL);
        }else {
            initCallBack.onResponse(ResultCode.INIT_SUCCESS);
        }
    }

    public void doLogin(LoginCallBack loginCallBack){
        if(Config.APP_ID.equals("")||Config.P_ID.equals("")){
            NotifyDialog notifyDialog = new NotifyDialog(context,"未初始化配置");
            notifyDialog.show();
        }else {
            if (checkLogined()){
                NotifyDialog notifyDialog = new NotifyDialog(context,"用户已登录");
                notifyDialog.show();
            }else {
                //登录操作
                Config.loginCallBack = loginCallBack;

                SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
                if (sharedPreferences.contains("account") && sharedPreferences.contains("password")) {
                    LoginDialog loginDialog = new LoginDialog(context);
                    LoginPresenter loginPresenter = new LoginPresenter(context, loginDialog);
                    try {
                        String pwdStr = DES.getDESOri(sharedPreferences.getString("password", ""),DES.KEY);
                        loginPresenter.quickLogin(sharedPreferences.getString("account", ""),pwdStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    LoginDialog loginDialog = new LoginDialog(context);
                    loginDialog.show();
                }
            }
        }
    }

    public String getOpenId(){
        if (Config.isLogined){
            SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
            return sharedPreferences.getString("openid","");
        }else {
            NotifyDialog notifyDialog = new NotifyDialog(context,"用户未登录，请登录");
            notifyDialog.show();
            return "";
        }
    }

    public boolean checkLogined(){
        return Config.isLogined;
    }


    public void doPay (double money, @Nullable String ext){
        if(Config.isLogined){
            if (!Config.KEY.equals("")){
                SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                if(sharedPreferences.contains("openid")){
                    String uid = sharedPreferences.getString("openid","");
                    PayDialog payDialog = new PayDialog(context,uid,money,ext);
                    payDialog.show();
                }
            }else {
                NotifyDialog notifyDialog = new NotifyDialog(context,"变量key未初始化，请设置");
                notifyDialog.show();
            }
        }else {
            NotifyDialog notifyDialog = new NotifyDialog(context,"用户未登录，请登录");
            notifyDialog.show();
        }
    }

    public void logout(LogoutCallBack logoutCallBack){
        Config.logoutCallBack = logoutCallBack;
    }

}
