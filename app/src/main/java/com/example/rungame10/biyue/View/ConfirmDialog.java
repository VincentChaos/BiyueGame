package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rungame10.biyue.Presenter.ConfirmPresenter;
import com.example.rungame10.biyue.Util.MResource;

import java.lang.ref.WeakReference;

public class ConfirmDialog extends AlertDialog{

    private static Context context;
    private EditText pwdEdit,confirmEdit;
    private TextView finishBtn,returnBtn;

    private ConfirmHandler confirmHandler = new ConfirmHandler(this);

    private ConfirmPresenter confirmPresenter;

    public ConfirmDialog(@Nullable Context context) {
        super(context, MResource.getIdByName(context,"style","by_Dialog"));
        this.context = context;

    }

    private static class ConfirmHandler extends Handler {
        private WeakReference<ConfirmDialog> mWeakReference;

        public ConfirmHandler(ConfirmDialog reference){
            mWeakReference = new WeakReference<ConfirmDialog>(reference);
        }

        public void handleMessage(Message msg) {
            ConfirmDialog reference = (ConfirmDialog)mWeakReference.get();
            if (reference == null) { // the referenced object has been cleared
                return;
            }

            // do something
            switch (msg.what){
                case 0:
                    NotifyDialog notifyDialog = new NotifyDialog(context,(String) msg.obj);
                    notifyDialog.show();
                    break;
                case 1:
                    NotifyDialog notifyDialog2 = new NotifyDialog(context,(String) msg.obj,2);
                    notifyDialog2.show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(MResource.getIdByName(context, "layout", "by_dialog_confirm"),null);
        setContentView(view);

        //声明
        pwdEdit = (EditText)view.findViewById(MResource.getIdByName(context,"id","by_edit_pwd"));
        confirmEdit = (EditText)view.findViewById(MResource.getIdByName(context,"id","by_edit_confirm"));
        finishBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_btn_finish"));
        returnBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","by_btn_return"));

        //点击事件
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmPresenter.confirmPwd(pwdEdit,confirmEdit);
            }
        });
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmPresenter.doReturn();
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
            lp.height = (int) (d.heightPixels*0.8);

        }else if(ori == Configuration.ORIENTATION_PORTRAIT){
            //竖屏
            lp.width = (int) (d.widthPixels*0.8);
            lp.height = (int) (d.heightPixels*0.4);
        }

        dialogWindow.setAttributes(lp);

        //显示alertDialog的软键盘
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void setAccountAndCode(String account,String codeStr){
        confirmPresenter = new ConfirmPresenter(context,ConfirmDialog.this);
        confirmPresenter.setAccountAndCode(account,codeStr);

    }

    public void showNotifyDialog(String returnWord){
        //开启提示弹出窗口
        Message msg = confirmHandler.obtainMessage();
        msg.what = 0;
        msg.obj = returnWord;
        confirmHandler.sendMessage(msg);
    }

    public void showNotifyDialog(String returnWord,int flag){
        //开启提示弹出窗口
        Message msg = confirmHandler.obtainMessage();
        msg.what = 1;
        msg.arg1 = flag;
        msg.obj = returnWord;
        confirmHandler.sendMessage(msg);
        //重置密码成功退出窗口
        cancel();
    }
}
