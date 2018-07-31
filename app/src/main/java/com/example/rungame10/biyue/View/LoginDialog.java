package com.example.rungame10.biyue.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Model.Res;
import com.example.rungame10.biyue.R;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

public class LoginDialog extends Dialog {

    private Context context;
    private EditText accountEdit,pwdEdit;       //账号编辑框，密码编辑框
    private TextView loginBtn,registerBtn,esayLogin,forgetPwd;        //登录按钮，注册按钮，一键登录按钮，忘记密码按钮
    private ImageView wechatLogin,qqLogin;      //微信登录按钮，qq登录按钮



    public LoginDialog(@NonNull Context context) {
        super(context, Res.style.dialogStyle());
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
         View view = inflater.inflate(Res.layout.login(),null);
         setContentView(view);

         //声明
         accountEdit = (EditText)view.findViewById(Res.view.accountEdit());
         pwdEdit = (EditText)view.findViewById(Res.view.pwdEdit());
         loginBtn = (TextView)view.findViewById(Res.view.loginBtn());
         registerBtn = (TextView)view.findViewById(Res.view.registerBtn());
         esayLogin = (TextView)view.findViewById(Res.view.loginOneBtn());
         forgetPwd = (TextView)view.findViewById(Res.view.forgetBtn());
         wechatLogin = (ImageView)view.findViewById(Res.view.wechatBtn());
         qqLogin = (ImageView)view.findViewById(Res.view.qqBtn());

         //点击事件
         loginBtn.setOnClickListener(new ClickListener());
         registerBtn.setOnClickListener(new ClickListener());
         esayLogin.setOnClickListener(new ClickListener());
         forgetPwd.setOnClickListener(new ClickListener());
         wechatLogin.setOnClickListener(new ClickListener());
         qqLogin.setOnClickListener(new ClickListener());

         //设置宽高
         Window dialogWindow = getWindow();
         WindowManager.LayoutParams lp = dialogWindow.getAttributes();
         DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高
         lp.width = (int) (d.widthPixels*0.8);
         lp.height = (int) (d.heightPixels*0.5);
         dialogWindow.setAttributes(lp);

     }

     private class ClickListener implements View.OnClickListener{
         @Override
         public void onClick(View view) {
             int id = view.getId();
             switch (id){
                 case R.id.btn_register:
                     RegisterDialog registerDialog = new RegisterDialog(context);
                     registerDialog.show();
                     LoginDialog.this.cancel();
                     break;
                 case R.id.btn_wechat:
                     //发起登录请求
                     if(Config.wx_api.isWXAppInstalled()){
                         SendAuth.Req req = new SendAuth.Req();
                         req.scope = "snsapi_userinfo";
                         req.state = "wechat_sdk_login";
                         Config.wx_api.sendReq(req);
                     }else {
                         Toast.makeText(context,"你还没有安装微信",Toast.LENGTH_SHORT).show();
                     }
                     LoginDialog.this.cancel();
                     break;
             }


         }
     }
}
