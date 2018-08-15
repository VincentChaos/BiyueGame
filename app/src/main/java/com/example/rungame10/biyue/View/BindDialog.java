package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rungame10.biyue.Presenter.BindPresenter;
import com.example.rungame10.biyue.Util.MResource;

import java.lang.ref.WeakReference;

public class BindDialog extends AlertDialog{

    private static Context context;
    private EditText accountEdit, verifyEdit;
    private CountdownButton sendVerBtn;
    private TextView bindBtn,skipBtn;

    private VerifyHandler verifyHandler = new VerifyHandler(this);

    public BindDialog(@Nullable Context context) {
        super(context, MResource.getIdByName(context,"style","Dialog"));
        this.context = context;
    }

    private static class VerifyHandler extends Handler{
        private WeakReference<BindDialog> mWeakReference;

        public VerifyHandler(BindDialog reference){
            mWeakReference = new WeakReference<BindDialog>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            BindDialog reference = (BindDialog)mWeakReference.get();
            BindDialog bindDialog = new BindDialog(context);
            if (reference == null) { // the referenced object has been cleared
                return;
            }
            // do something
            NotifyDialog notifyDialog = new NotifyDialog(context);
            switch (msg.what){
                case 0:
                    notifyDialog.showNotifyDialog((String) msg.obj);
                    break;
                case 1:
                    notifyDialog.showNotifyDialog((String) msg.obj,msg.arg1);
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
        View view = inflater.inflate(MResource.getIdByName(context, "layout", "dialog_bind"),null);
        setContentView(view);

        //声明
        accountEdit = (EditText)view.findViewById(MResource.getIdByName(context, "id", "edit_account"));
        verifyEdit = (EditText)view.findViewById(MResource.getIdByName(context,"id","edit_verification"));
        sendVerBtn = (CountdownButton)view.findViewById(MResource.getIdByName(context, "id", "btn_verification"));
        bindBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","btn_bind"));
        skipBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","btn_return"));

        final BindPresenter bindPresenter = new BindPresenter(context,BindDialog.this);


        //点击事件
        sendVerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindPresenter.sendVerify(accountEdit,sendVerBtn);
            }
        });
        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindPresenter.doBind(accountEdit,verifyEdit);
            }
        });
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindPresenter.doSkip();
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
        lp.width = (int) (d.widthPixels*0.8);
        lp.height = (int) (d.heightPixels*0.4);
        dialogWindow.setAttributes(lp);

        //显示alertDialog的软键盘
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void showNotifyDialog(String returnWord){
        //开启提示弹出窗口
        Message msg = verifyHandler.obtainMessage();
        msg.what = 0;
        msg.obj = returnWord;
        verifyHandler.sendMessage(msg);
    }

    public void showNotifyDialog(String returnWord,int flag){
        //开启提示弹出窗口
        Message msg = verifyHandler.obtainMessage();
        msg.what = 1;
        msg.obj = returnWord;
        msg.arg1 = flag;
        verifyHandler.sendMessage(msg);
        //验证成功退出窗口
        BindDialog.this.cancel();
    }

    @Override
    protected void onStop() {
        sendVerBtn.onDetachedFromWindow();
        super.onStop();
    }
}
