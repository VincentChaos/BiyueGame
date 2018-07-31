package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rungame10.biyue.Model.Res;
import com.example.rungame10.biyue.R;

public class RegisterDialog extends AlertDialog {

    private Context context;
    private EditText accountEdit,pwdEdit,verificationEdit;       //账号编辑框，密码编辑框,验证码编辑框
    private TextView registerBtn,returnLoginBtn,sendVerBtn;      //注册按钮，返回登录按钮,发送验证码按钮

    public RegisterDialog(@NonNull Context context) {
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
        View view = inflater.inflate(Res.layout.register(),null);
        setContentView(view);

        //声明
        accountEdit = (EditText)view.findViewById(Res.view.accountEdit());
        pwdEdit = (EditText)view.findViewById(Res.view.pwdEdit());
        verificationEdit = (EditText)view.findViewById(Res.view.verifiEdit());
        registerBtn = (TextView)view.findViewById(Res.view.registerBtn());
        returnLoginBtn = (TextView)view.findViewById(Res.view.returnBtn());
        sendVerBtn = (TextView)view.findViewById(Res.view.verifiBtn());

        //点击事件
        registerBtn.setOnClickListener(new ClickListener());
        returnLoginBtn.setOnClickListener(new ClickListener());
        sendVerBtn.setOnClickListener(new ClickListener());


        //设置宽高
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高
        lp.width = (int) (d.widthPixels*0.8);
        lp.height = (int) (d.heightPixels*0.5);
        dialogWindow.setAttributes(lp);

        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.btn_return:
                    LoginDialog loginDialog = new LoginDialog(context);
                    loginDialog.show();
                    RegisterDialog.this.cancel();
                    break;
            }
        }
    }


}
