package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Intf.ResultCode;
import com.example.rungame10.biyue.Util.MResource;

public class NotifyLoginDialog extends AlertDialog{

    private Context context;
    private TextView contentText,confirmBtn,switchBtn;
    private String content;
    private boolean switchFlag = false;

    public NotifyLoginDialog(Context context, String content){
        super(context, MResource.getIdByName(context, "style", "by_Dialog"));
        this.context = context;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(MResource.getIdByName(context,"layout","by_dialog_login_notify"),null);
        setContentView(view);

        //声明
        contentText = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_text"));
        confirmBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_btn_confirm"));
        switchBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_btn_switch"));

        contentText.setText(content);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });



        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFlag = true;
                SwitchDialog switchDialog = new SwitchDialog(context);
                switchDialog.show();
                cancel();
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(!switchFlag){
                    //登录成功，检查是否绑定手机
                    SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                    String getHavePhone = sharedPreferences.getString("have_phone","");
                    if(getHavePhone.equals("1.0")){
                        //已绑定手机
                        Config.loginCallBack.onResponse(ResultCode.LOGIN_SUCCESS,sharedPreferences.getString("openid",""));
                    }else {
                        //未绑定手机，开启绑定手机界面绑定手机
                        Toast.makeText(context,"请绑定手机",Toast.LENGTH_SHORT).show();
                        BindDialog bindDialog = new BindDialog(context);
                        bindDialog.show();
                    }
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
            lp.height = (int) (d.heightPixels*0.35);
        }

        dialogWindow.setAttributes(lp);
    }
}
