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

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Intf.ResultCode;
import com.example.rungame10.biyue.Util.MResource;

public class SwitchDialog extends AlertDialog {
    private Context context;
    private TextView nameText,logoutBtn;
    private boolean logoutFlag = false;

    public SwitchDialog(Context context){
        super(context, MResource.getIdByName(context, "style", "by_Dialog"));
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(MResource.getIdByName(context,"layout","by_dialog_switch"),null);
        setContentView(view);

        //声明
        nameText = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_text_name"));
        logoutBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_btn_logout"));

        final SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        String nameStr = "用户名称： " + sharedPreferences.getString("user_name","");
        final String openId = sharedPreferences.getString("openid","");
        nameText.setText(nameStr);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutFlag = true;
                cancel();
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (logoutFlag){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Config.isLogined = false;
                    Config.logoutCallBack.onResponse(ResultCode.LOGOUT_SUCCESS);
                }else {
                    Config.isLogined = true;
                    Config.loginCallBack.onResponse(ResultCode.LOGIN_SUCCESS,openId);
                }
            }
        });

        //设置宽高
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高
        lp.width = (int) (d.widthPixels*0.8);
        lp.height = (int) (d.heightPixels*0.3);

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
