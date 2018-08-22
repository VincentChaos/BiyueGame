package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.example.rungame10.biyue.Util.MResource;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class ProgressDialog extends AlertDialog{

    private Context context;

    public ProgressDialog(Context context) {
        super(context, MResource.getIdByName(context,"style","progressbar"));
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();


    }

    @Override
    public void show() {
        super.show();
        //设置全屏
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高
        lp.width = (int) (d.widthPixels);
        lp.height = (int) (d.heightPixels);
        dialogWindow.setAttributes(lp);
        setTime();
    }

    private void init(){
        View view = LayoutInflater.from(context).inflate(MResource.getIdByName(context,"layout","progressbar"),null);

        ImageView circleView = (ImageView)view.findViewById(MResource.getIdByName(context,"id","progressbar"));
        //加载动画
        Animation animation = AnimationUtils.loadAnimation(context,MResource.getIdByName(context,"anim","anim_load"));
        circleView.startAnimation(animation);
        setContentView(view);
    }

    private void setTime(){
        //设置进度条显示时间
        final Timer time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                dismiss();
                cancel();
                time.cancel();
            }
        },4000);
    }
}
