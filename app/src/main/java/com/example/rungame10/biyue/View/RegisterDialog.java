package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.example.rungame10.biyue.Util.MResource;
import com.example.rungame10.biyue.Presenter.RegisterPresenter;

import java.lang.ref.WeakReference;

public class RegisterDialog extends AlertDialog {

    private static Context context;
    private EditText accountEdit,pwdEdit,verificationEdit;       //账号编辑框，密码编辑框,验证码编辑框
    private TextView registerBtn,returnLoginBtn;      //注册按钮，返回登录按钮,发送验证码按钮
    private CountdownButton sendVerBtn;

    private RegisterHandler registerHandler = new RegisterHandler(this);


    public RegisterDialog(@NonNull Context context) {
        super(context,  MResource.getIdByName(context, "style", "by_Dialog"));
        this.context = context;

    }

    private static class RegisterHandler extends Handler {
        private WeakReference<RegisterDialog> mWeakReference;

        public RegisterHandler(RegisterDialog reference) {
            mWeakReference = new WeakReference<RegisterDialog>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            RegisterDialog reference = (RegisterDialog) mWeakReference.get();
            if (reference == null) { // the referenced object has been cleared
                return;
            }
            // do something
            switch (msg.what){
                case 1:
                    if(msg.arg1 == 2){
                        NotifyDialog notifyDialog = new NotifyDialog(context,(String)msg.obj,msg.arg1);
                        notifyDialog.show();
                    }else {
                        NotifyDialog notifyDialog = new NotifyDialog(context,(String) msg.obj);
                        notifyDialog.show();
                    }
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
        View view = inflater.inflate(MResource.getIdByName(context, "layout", "by_dialog_register"),null);
        setContentView(view);

        //声明
        accountEdit = (EditText)view.findViewById(MResource.getIdByName(context, "id", "by_edit_account"));
        pwdEdit = (EditText)view.findViewById(MResource.getIdByName(context, "id", "by_edit_pwd"));
        verificationEdit = (EditText)view.findViewById(MResource.getIdByName(context, "id", "by_edit_verification"));
        registerBtn = (TextView)view.findViewById(MResource.getIdByName(context, "id", "by_btn_register"));
        returnLoginBtn = (TextView)view.findViewById(MResource.getIdByName(context, "id", "by_btn_return"));
        sendVerBtn = (CountdownButton)view.findViewById(MResource.getIdByName(context, "id", "by_btn_verification"));

        final RegisterPresenter registerPresenter = new RegisterPresenter(context,RegisterDialog.this);

        //点击事件
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPresenter.register(accountEdit,verificationEdit,pwdEdit);
            }
        });
        returnLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPresenter.returnLogin();
            }
        });
        sendVerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPresenter.sendVerify(accountEdit,verificationEdit,sendVerBtn);
            }
        });

        //监听事件，手机账号更改时重置验证码倒计时
        accountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!sendVerBtn.isEnabled()){
                    //若发送验证码按钮不能点击
                    sendVerBtn.setEnabled(true);
                    sendVerBtn.setText("发送验证码");
                    sendVerBtn.clearTimer();
                    sendVerBtn.setLength(60 * 1000);
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
            lp.height = (int) (d.heightPixels*0.9);

        }else if(ori == Configuration.ORIENTATION_PORTRAIT){
            //竖屏
            lp.width = (int) (d.widthPixels*0.8);
            lp.height = (int) (d.heightPixels*0.5);
        }

        dialogWindow.setAttributes(lp);

        //显示alertDialog的软键盘
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void showNotifyDialog(String returnWord){
        //开启提示弹出窗口
        Message msg = registerHandler.obtainMessage();
        msg.what = 1;
        msg.obj = returnWord;
        registerHandler.sendMessage(msg);
    }

    public void showNotifyDialog(String returnWord,int flag){
        //开启提示弹出窗口
        Message msg = registerHandler.obtainMessage();
        msg.what = 1;
        msg.arg1 = flag;
        msg.obj = returnWord;
        registerHandler.sendMessage(msg);
        //注册成功退出注册窗口
        RegisterDialog.this.cancel();
    }

    @Override
    protected void onStop() {
        sendVerBtn.onDetachedFromWindow();
        super.onStop();
    }
}
