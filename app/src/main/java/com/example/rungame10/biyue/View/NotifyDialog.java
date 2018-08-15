package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.rungame10.biyue.Presenter.FloatActionController;

public class NotifyDialog {

    private Context context;
    private boolean isBind = false;           //全局变量，是否绑定手机

    public NotifyDialog(Context context){
        this.context = context;
    }

    public void setBind(){
        isBind = true;
    }

    public void showNotifyDialog(String content){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("提示");
        alertDialog.setMessage(content);
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //点击退出弹出窗口

            }
        });
        alertDialog.show();
    }

    public void showNotifyDialog(String content, final int flag){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("提示");
        alertDialog.setMessage(content);
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (flag){
                    case 1:
                        //登录成功，检查是否绑定手机
                        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                        String getHavePhone = sharedPreferences.getString("have_phone","");
                        if(getHavePhone.equals("1.0")){
                            //已绑定手机
                            //开启悬浮球
                            FloatActionController.isLogined = true;
                            FloatActionController.getInstance().startServer(context);
                        }else {
                            //未绑定手机，开启绑定手机界面绑定手机
                            Toast.makeText(context,"请绑定手机",Toast.LENGTH_SHORT).show();
                            BindDialog bindDialog = new BindDialog(context);
                            bindDialog.show();
                        }
                        break;
                    case 2:
                        //注册成功，点击使用注册的账号密码进行登录操作
                        LoginDialog loginDialog = new LoginDialog(context);
                        loginDialog.show();
                        break;
                }
            }
        });
        alertDialog.show();
    }
}
