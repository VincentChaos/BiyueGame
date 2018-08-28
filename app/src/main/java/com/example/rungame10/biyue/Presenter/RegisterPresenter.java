package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Model.RequestLoginAndRegister;
import com.example.rungame10.biyue.Intf.JsonResult;
import com.example.rungame10.biyue.Model.RequestVerify;
import com.example.rungame10.biyue.Model.ResponseMsg;
import com.example.rungame10.biyue.View.CountdownButton;
import com.example.rungame10.biyue.View.LoginDialog;
import com.example.rungame10.biyue.View.ProgressDialog;
import com.example.rungame10.biyue.View.RegisterDialog;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class RegisterPresenter {
    private Context context;
    private RegisterDialog registerDialog;

    private boolean accountLegal = false;       //用户名是否合法全局变量
    private boolean pwdLegal = false;           //密码是否合法全局变量
    private boolean focusFlag;
    public RegisterPresenter(Context context,RegisterDialog registerDialog){
        this.context = context;
        this.registerDialog = registerDialog;

    }

    public void sendVerify(EditText accountEdit, final EditText verifyEdit, CountdownButton countBtn){
        //发送验证码按钮操作

        //关闭软键盘
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&registerDialog.getCurrentFocus()!=null){
            if (registerDialog.getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(registerDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
            requestVerify.setCt(1);       //注册ct值为1
            requestVerify.setTelephone(account);
            requestVerify.setType("sdkCode");

            //新建线程使用OkHttp访问网络
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PostController postController = new PostController(requestVerify);
                    String result = postController.getResult();
                    if(result.equals("00")){
                        registerDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                    }else {
                        //用UpLoadResult对象解析获取的json
                        Gson gson = new Gson();
                        JsonResult response = gson.fromJson(result, JsonResult.class);

                        //获取其中code
                        int code = response.getCode();
                        if (code == 10001){
                            focusFlag = true;
                        }
                        registerDialog.showNotifyDialog(response.getMsg()+"");
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

    public void register(EditText accountEdit, EditText verifyEdit, EditText pwdEdit){
        //注册按钮操作
        final String account = isAccountLegal(accountEdit);
        final String pwd = isPwdLegal(pwdEdit);
        String codeStr = verifyEdit.getText().toString().trim();
        if (accountLegal && pwdLegal){
            if(codeStr.equals("")){
                Toast.makeText(context,"验证码不能为空",Toast.LENGTH_SHORT).show();
            }else {
                //注册操作

                //进度条dialog
                final ProgressDialog alertDialog = new ProgressDialog(context);
                alertDialog.setCancelable(false);
                alertDialog.show();

                //打包注册对象
                final RequestLoginAndRegister requestLoginAndRegister = new RequestLoginAndRegister();
                requestLoginAndRegister.setAppid(Config.APP_ID);
                requestLoginAndRegister.setType("appRegister");
                requestLoginAndRegister.setTelephone(account);
                requestLoginAndRegister.setPassword(pwd);
                requestLoginAndRegister.setCode(codeStr);

                //新建线程使用OkHttp访问网络
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PostController postController = new PostController(requestLoginAndRegister);
                        String result = postController.getResult();
                        if (result.equals("00")) {
                            //调用弹出通知窗口方法
                            registerDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                        } else {
                            //用UpLoadResult对象解析获取的json
                            Gson gson = new Gson();
                            JsonResult response = gson.fromJson(result, JsonResult.class);

                            //获取其中code
                            int code = response.getCode();

                            String returnWord;
                            if (code == 10001){
                                //注册成功
                                LinkedTreeMap linkedTreeMap = (LinkedTreeMap)response.getMsg();
                                ResponseMsg getResponse = new ResponseMsg(linkedTreeMap);
                                returnWord = "注册成功，点击确定进行登录操作";

                                //code为成功返回时,保存用户名密码至SharedPreferences
                                SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("account",account);
                                editor.putString("password",pwd);
                                editor.apply();

                                //调用弹出通知窗口方法
                                registerDialog.showNotifyDialog(returnWord,2);
                            }else {
                                //调用弹出通知注册失败
                                returnWord = (String)response.getMsg();
                                registerDialog.showNotifyDialog(returnWord);
                            }
                        }
                        //进度条dialog消失
                        alertDialog.dismiss();
                    }
                }).start();
            }
        }
    }

    public void returnLogin(){
        //返回按钮
        LoginDialog loginDialog = new LoginDialog(context);
        loginDialog.show();
        registerDialog.cancel();
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

    private String isPwdLegal(EditText editText){
        //判定用户密码编辑框中输入字符是否合法
        String s = editText.getText().toString().trim();
        if (s.equals("")){
            Toast.makeText(context,"用户密码不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }else if(s.length() <= 6){
            Toast.makeText(context,"用户密码长度不能小于6位",Toast.LENGTH_SHORT).show();
            return null;
        }else if(s.length() > 15){
            Toast.makeText(context,"用户密码长度不能大于15位",Toast.LENGTH_SHORT).show();
            return null;
        }else {
            pwdLegal = true;
            return s;
        }
    }

}
