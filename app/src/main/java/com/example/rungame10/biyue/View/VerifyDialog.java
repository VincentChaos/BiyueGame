package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.rungame10.biyue.Presenter.VerifyPresenter;
import com.example.rungame10.biyue.Util.MResource;

import java.lang.ref.WeakReference;

public class VerifyDialog extends AlertDialog{

    private static Context context;
    private EditText accountEdit, verifyEdit;
    private CountdownButton sendVerBtn;
    private TextView nextBtn,returnBtn;

    private static String telephone;
    private static String codeStr;

    private VerifyHandler verifyHandler = new VerifyHandler(this);

    public VerifyDialog(@Nullable Context context) {
        super(context, MResource.getIdByName(context,"style","Dialog"));
        this.context = context;
    }

    private static class VerifyHandler extends Handler{
        private WeakReference<VerifyDialog> mWeakReference;

        public VerifyHandler(VerifyDialog reference){
            mWeakReference = new WeakReference<VerifyDialog>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            VerifyDialog reference = (VerifyDialog)mWeakReference.get();
            VerifyDialog verifyDialog = new VerifyDialog(context);
            if (reference == null) { // the referenced object has been cleared
                return;
            }
            // do something
            switch (msg.what){
                case 0:
                    NotifyDialog notifyDialog = new NotifyDialog(context);
                    notifyDialog.showNotifyDialog((String) msg.obj);
                    break;
                case 1:
                    Toast.makeText(context,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    ConfirmDialog confirmDialog = new ConfirmDialog(context);
                    confirmDialog.setAccountAndCode(telephone,codeStr);
                    confirmDialog.show();
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
        View view = inflater.inflate(MResource.getIdByName(context, "layout", "dialog_verify"),null);
        setContentView(view);

        //声明
        accountEdit = (EditText)view.findViewById(MResource.getIdByName(context, "id", "edit_account"));
        verifyEdit = (EditText)view.findViewById(MResource.getIdByName(context,"id","edit_verification"));
        sendVerBtn = (CountdownButton)view.findViewById(MResource.getIdByName(context, "id", "btn_verification"));
        nextBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","btn_next"));
        returnBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","btn_return"));

        final VerifyPresenter verifyPresenter = new VerifyPresenter(context,VerifyDialog.this);

        verifyPresenter.setAccount(accountEdit);

        //点击事件
        sendVerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPresenter.sendVerify(accountEdit,verifyEdit,sendVerBtn);
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPresenter.doNext(accountEdit,verifyEdit);
            }
        });
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPresenter.doReturn();
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

    public void showNotifyDialog(String returnWord,String telephone,String codeStr){
        //开启提示弹出窗口
        Message msg = verifyHandler.obtainMessage();
        msg.what = 1;
        msg.obj = returnWord;
        this.telephone = telephone;
        this.codeStr = codeStr;
        verifyHandler.sendMessage(msg);
        //验证成功退出窗口
        VerifyDialog.this.cancel();
    }

    @Override
    protected void onStop() {
        sendVerBtn.onDetachedFromWindow();
        super.onStop();
    }
}
