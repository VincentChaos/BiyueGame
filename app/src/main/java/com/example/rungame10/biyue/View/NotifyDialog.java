package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Presenter.FloatActionController;
import com.example.rungame10.biyue.Util.MResource;

public class NotifyDialog extends AlertDialog{

    private Context context;
    private TextView contentText,confirmBtn;

    private String content;
    private int flag = -1;

    public NotifyDialog(Context context,String content){
        super(context, MResource.getIdByName(context, "style", "by_Dialog"));
        this.context = context;
        this.content = content;
    }

    public NotifyDialog(Context context,String content,int flag){
        super(context, MResource.getIdByName(context, "style", "by_Dialog"));
        this.context = context;
        this.content = content;
        this.flag = flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(MResource.getIdByName(context,"layout","by_dialog_notify"),null);
        setContentView(view);

        //声明
        contentText = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_text"));
        confirmBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_btn_confirm"));

        contentText.setText(content);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(flag != -1){
                    switch (flag){
                        case 1:
                            //登录成功，检查是否绑定手机
                            SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                            String getHavePhone = sharedPreferences.getString("have_phone","");
                            if(getHavePhone.equals("1.0")){
                                //已绑定手机
                                //开启悬浮球
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
            }
        });

        //设置宽高
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高
        lp.width = (int) (d.widthPixels*0.8);
        lp.height = (int) (d.heightPixels*0.3);
        dialogWindow.setAttributes(lp);
    }
}
