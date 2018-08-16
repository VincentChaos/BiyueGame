package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rungame10.biyue.Intf.JsonResult;
import com.example.rungame10.biyue.Model.RequestCheckVerify;
import com.example.rungame10.biyue.Model.RequestVerify;
import com.example.rungame10.biyue.View.CountdownButton;
import com.example.rungame10.biyue.View.LoginDialog;
import com.example.rungame10.biyue.View.ProgressDialog;
import com.example.rungame10.biyue.View.VerifyDialog;
import com.google.gson.Gson;

public class VerifyPresenter {

    private Context context;
    private VerifyDialog verifyDialog;

    private boolean accountLegal = false;       //用户名是否合法全局变量
    private boolean focusFlag = false;

    public VerifyPresenter(Context context, VerifyDialog verifyDialog){
        this.context = context;
        this.verifyDialog = verifyDialog;
    }

    public void setAccount(EditText accountEdit){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        if(sharedPreferences.contains("telephone")){
            accountEdit.setText(sharedPreferences.getString("telephone",""));
        }
    }

    public void sendVerify(EditText accountEdit, final EditText verifyEdit, CountdownButton countBtn){
        //发送验证码按钮操作

        //关闭软键盘
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&verifyDialog.getCurrentFocus()!=null){
            if (verifyDialog.getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(verifyDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        //绑定手机号后发送验证码
        String account = isAccountLegal(accountEdit);
        if(accountLegal){
            countBtn.start();
            //进度条dialog
            final ProgressDialog alertDialog = new ProgressDialog(context);
            alertDialog.setCancelable(false);
            alertDialog.show();

            //打包请求验证码对象
            final RequestVerify requestVerify = new RequestVerify();
            requestVerify.setCt(2);       //找回密码ct值为2
            requestVerify.setTelephone(account);
            requestVerify.setType("sdkCode");

            //新建线程使用OkHttp访问网络
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PostController postController = new PostController(requestVerify);
                    String result = postController.getResult();
                    if(result.equals("00")){
                        verifyDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                    }else {
                        //用UpLoadResult对象解析获取的json
                        Gson gson = new Gson();
                        JsonResult response = gson.fromJson(result, JsonResult.class);

                        //获取其中code
                        int code = response.getCode();
                        Log.e("code:",code+"");
                        if (code == 10001){
                            focusFlag = true;
                        }
                        verifyDialog.showNotifyDialog(response.getMsg()+"");
                    }
                    //进度条dialog消失
                    alertDialog.dismiss();
                }
            }).start();

            if (focusFlag){
                verifyEdit.setFocusable(true);
                verifyEdit.setFocusableInTouchMode(true);
                verifyEdit.requestFocus();
                focusFlag = false;
            }

            //重置账号合法判别
            accountLegal = false;

        }
    }

    public void doNext(EditText accountEdit, final EditText verifyEdit){
        final String account = isAccountLegal(accountEdit);
        final String codeStr = verifyEdit.getText().toString().trim();

        if(accountLegal){
            if(codeStr.equals("")){
                Toast.makeText(context,"验证码不能为空",Toast.LENGTH_SHORT).show();
            }else {
                //验证码是否正确，正确跳转至找回密码界面

                //进度条dialog
                final ProgressDialog alertDialog = new ProgressDialog(context);
                alertDialog.setCancelable(false);
                alertDialog.show();

                //打包验证验证码对象
                final RequestCheckVerify requestCheckVerify = new RequestCheckVerify();
                requestCheckVerify.setCt(2);
                requestCheckVerify.setTelephone(account);
                requestCheckVerify.setType("sdkCodeVerify");
                requestCheckVerify.setCode(codeStr);

                //新建线程使用OkHttp访问网络
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PostController postController = new PostController(requestCheckVerify);
                        String result = postController.getResult();
                        if(result.equals("00")){
                            verifyDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                        }else {
                            //用UpLoadResult对象解析获取的json
                            Gson gson = new Gson();
                            JsonResult response = gson.fromJson(result, JsonResult.class);

                            //获取其中code
                            int code = response.getCode();
                            if(code == 10001){
                                //验证成功

                                //保存手机号至本地
                                SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("telephone",account);
                                editor.apply();

                                verifyDialog.showNotifyDialog((String)response.getMsg(),account,codeStr);
                            }else {
                                verifyDialog.showNotifyDialog((String)response.getMsg());
                            }
                        }
                        //进度条dialog消失
                        alertDialog.dismiss();
                    }
                }).start();
            }
        }
    }

    public void doReturn(){
        //返回按钮
        LoginDialog loginDialog = new LoginDialog(context);
        loginDialog.show();
        verifyDialog.cancel();
    }

    private String isAccountLegal(EditText editText){
        //判定用户账号编辑框中输入字符是否合法
        String s = editText.getText().toString().trim();
        if (s.equals("")){
            Toast.makeText(context,"手机号码不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }else{
            if(s.length() != 11){
                Toast.makeText(context,"手机号码格式不正确",Toast.LENGTH_SHORT).show();
                return null;
            }else {
                accountLegal = true;
                return s;
            }
        }
    }


}
