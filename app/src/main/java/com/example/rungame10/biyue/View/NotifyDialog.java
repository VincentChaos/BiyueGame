package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
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
                    //注册成功，点击使用注册的账号密码进行登录操作
                    LoginDialog loginDialog = new LoginDialog(context);
                    loginDialog.show();
                }
            }
        });

        //设置宽高
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高

        //判断当前是否横屏
        Configuration configuration = context.getResources().getConfiguration();
        int ori = configuration.orientation;
        if(ori == Configuration.ORIENTATION_LANDSCAPE){
            //横屏
            lp.width = (int) (d.widthPixels*0.5);
            lp.height = (int) (d.heightPixels*0.7);

        }else if(ori == Configuration.ORIENTATION_PORTRAIT){
            //竖屏
            lp.width = (int) (d.widthPixels*0.8);
            lp.height = (int) (d.heightPixels*0.3);
        }

        dialogWindow.setAttributes(lp);
    }
}
