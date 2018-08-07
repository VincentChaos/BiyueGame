package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.example.rungame10.biyue.Model.MResource;
import com.example.rungame10.biyue.Presenter.RegisterPresenter;

public class RegisterDialog extends AlertDialog {

    private Context context;
    private EditText accountEdit,pwdEdit,verificationEdit;       //账号编辑框，密码编辑框,验证码编辑框
    private TextView registerBtn,returnLoginBtn,sendVerBtn;      //注册按钮，返回登录按钮,发送验证码按钮

    public RegisterDialog(@NonNull Context context) {
        super(context,  MResource.getIdByName(context, "style", "Dialog"));
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
        View view = inflater.inflate(MResource.getIdByName(context, "layout", "dialog_register"),null);
        setContentView(view);

        //声明
        accountEdit = (EditText)view.findViewById(MResource.getIdByName(this.context, "id", "edit_account"));
        pwdEdit = (EditText)view.findViewById(MResource.getIdByName(this.context, "id", "edit_pwd"));
        verificationEdit = (EditText)view.findViewById(MResource.getIdByName(this.context, "id", "edit_verification"));
        registerBtn = (TextView)view.findViewById(MResource.getIdByName(this.context, "id", "btn_register"));
        returnLoginBtn = (TextView)view.findViewById(MResource.getIdByName(this.context, "id", "btn_return"));
        sendVerBtn = (TextView)view.findViewById(MResource.getIdByName(this.context, "id", "btn_verification"));

        final RegisterPresenter registerPresenter = new RegisterPresenter(context);

        //点击事件
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        returnLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterDialog.this.cancel();
                registerPresenter.returnLogin();
            }
        });
        sendVerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //设置宽高
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高
        lp.width = (int) (d.widthPixels*0.8);
        lp.height = (int) (d.heightPixels*0.5);
        dialogWindow.setAttributes(lp);

        //显示alertdialog的软键盘
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }
}
