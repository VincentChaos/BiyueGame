package com.example.rungame10.biyue.SDK;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Presenter.FloatActionController;
import com.example.rungame10.biyue.Presenter.FloatPermissionManager;
import com.example.rungame10.biyue.Presenter.LoginPresenter;
import com.example.rungame10.biyue.View.LoginDialog;
import com.example.rungame10.biyue.View.NotifyDialog;
import com.example.rungame10.biyue.View.PayDialog;
import com.example.rungame10.biyue.View.SwitchDialog;

public class LibController {

    private Context context;

    public LibController(Context context){
        this.context = context;
    }

    public void doLogin(){
        if(Config.APP_ID.equals("")||Config.P_ID.equals("")){
            NotifyDialog notifyDialog = new NotifyDialog(context,"未初始化配置");
            notifyDialog.show();
        }else {
            if (checkLogined()){
                NotifyDialog notifyDialog = new NotifyDialog(context,"用户已登录");
                notifyDialog.show();
            }else {
                //登录操作
                SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
                if (sharedPreferences.contains("account") && sharedPreferences.contains("password")) {
                    LoginDialog loginDialog = new LoginDialog(context);
                    LoginPresenter loginPresenter = new LoginPresenter(context, loginDialog);
                    loginPresenter.quickLogin(sharedPreferences.getString("account", ""), sharedPreferences.getString("password", ""));
                } else {
                    LoginDialog loginDialog = new LoginDialog(context);
                    loginDialog.show();
                }
            }
        }

    }

    public boolean checkLogined(){
        return FloatActionController.isLogined;
    }

    public void init(String appId,String pId,String key){
        Config.APP_ID = appId;
        Config.P_ID = pId;
        Config.KEY = key;
        FloatPermissionManager.getInstance().applyFloatWindow(context);
    }

    public void doPay (double money, @Nullable String ext){
        if(FloatActionController.isLogined){
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

    public void doLogout(){
        if(FloatActionController.isLogined){
            SwitchDialog switchDialog = new SwitchDialog(context);
            switchDialog.show();
        }else {
            NotifyDialog notifyDialog = new NotifyDialog(context,"用户未登录，请登录");
            notifyDialog.show();
        }
    }

}
