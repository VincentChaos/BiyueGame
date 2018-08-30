package com.example.rungame10.biyue.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Intf.ResultCode;
import com.example.rungame10.biyue.Util.MResource;
import com.example.rungame10.biyue.Presenter.LoginPresenter;

import java.lang.ref.WeakReference;

public class LoginDialog extends AlertDialog {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private EditText accountEdit,pwdEdit;       //账号编辑框，密码编辑框
    private TextView loginBtn,registerBtn,esayLogin,forgetPwd;        //登录按钮，注册按钮，一键登录按钮，忘记密码按钮
    private ImageView wechatLogin,qqLogin;      //微信登录按钮，qq登录按钮
    private LinearLayout ll;                    //含微信、QQ登录layout
    private LoginHandler loginHandler = new LoginHandler(this);

    public LoginPresenter loginPresenter;

    private boolean otherFlag = false;

    public LoginDialog(@NonNull Context context) {
        super(context, MResource.getIdByName(context, "style", "by_Dialog"));
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    private class LoginHandler extends Handler{
        private WeakReference<LoginDialog> mWeakReference;

        public LoginHandler(LoginDialog reference) {
            mWeakReference = new WeakReference<LoginDialog>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginDialog reference = (LoginDialog) mWeakReference.get();
            if (reference == null) { // the referenced object has been cleared
                return;
            }
            // do something
            switch (msg.what){
                case 0:
                    Config.loginCallBack.onResponse(ResultCode.LOGIN_FAIL);
                    NotifyDialog notifyDialog = new NotifyDialog(context,(String) msg.obj);
                    notifyDialog.show();
                    break;
                case 1:
                    NotifyLoginDialog notifyLoginDialog = new NotifyLoginDialog(context,(String)msg.obj);
                    notifyLoginDialog.show();
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
         View view = inflater.inflate(MResource.getIdByName(context, "layout", "by_dialog_login"),null);
         setContentView(view);

         //声明
         accountEdit = (EditText)view.findViewById(MResource.getIdByName(context, "id", "by_edit_account"));
         pwdEdit = (EditText)view.findViewById(MResource.getIdByName(context, "id", "by_edit_pwd"));
         loginBtn = (TextView)view.findViewById(MResource.getIdByName(context, "id", "by_btn_login"));
         registerBtn = (TextView)view.findViewById(MResource.getIdByName(context, "id", "by_btn_register"));
         esayLogin = (TextView)view.findViewById(MResource.getIdByName(context, "id", "by_btn_login_one"));
         forgetPwd = (TextView)view.findViewById(MResource.getIdByName(context, "id", "by_btn_forget"));
         wechatLogin = (ImageView)view.findViewById(MResource.getIdByName(context, "id", "by_btn_wechat"));
         qqLogin = (ImageView)view.findViewById(MResource.getIdByName(context, "id", "by_btn_qq"));
         ll = (LinearLayout)view.findViewById(MResource.getIdByName(context,"id","by_ll"));
         loginPresenter = new LoginPresenter(context,LoginDialog.this);
         loginPresenter.setAccount(accountEdit,pwdEdit);

         if(otherFlag){
             ll.setVisibility(View.VISIBLE);
         }else {
             ll.setVisibility(View.GONE);
         }

         //点击事件
         loginBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //登录按钮点击事件
                 loginPresenter.normalLogin(accountEdit,pwdEdit);
             }
         });

         registerBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //注册按钮点击事件
                 loginPresenter.startRegister();
             }
         });

         esayLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //一键登录操作
                loginPresenter.OneKeyLogin();
             }
         });

         forgetPwd.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //忘记密码操作
                 loginPresenter.forgetPwd();
             }
         });

         wechatLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //微信登录按钮操作
//                 loginPresenter.wechatLogin();
             }
         });
         qqLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                //QQ登录操作
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
             if(otherFlag){
                 lp.height = (int) (d.heightPixels*0.9);
             }else {
                 lp.height = (int) (d.heightPixels*0.85);
             }

         }else if(ori == Configuration.ORIENTATION_PORTRAIT){
             //竖屏
             lp.width = (int) (d.widthPixels*0.8);
             if(otherFlag){
                 lp.height = (int) (d.heightPixels*0.5);
             }else {
                 lp.height = (int) (d.heightPixels*0.45);
             }
         }
         dialogWindow.setAttributes(lp);

         //显示alertDialog的软键盘
         dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
         dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                 WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

     }

     public void showNotifyDialog(String returnWord){
        //开启提示弹出窗口
         Message msg = loginHandler.obtainMessage();
         msg.what = 0;
         msg.obj = returnWord;
         loginHandler.sendMessage(msg);
     }

     public void showNotifyDialog(String returnWord,int flag){
        //开启提示弹出窗口
        Message msg = loginHandler.obtainMessage();
        msg.what = 1;
        msg.obj = returnWord;
        loginHandler.sendMessage(msg);
        //登录成功退出登录窗口
        LoginDialog.this.cancel();
    }


}
