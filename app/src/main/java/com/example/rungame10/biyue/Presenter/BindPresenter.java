package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rungame10.biyue.Intf.JsonResult;
import com.example.rungame10.biyue.Model.RequestLoginAndRegister;
import com.example.rungame10.biyue.Model.RequestVerify;
import com.example.rungame10.biyue.View.BindDialog;
import com.example.rungame10.biyue.View.CountdownButton;
import com.example.rungame10.biyue.View.ProgressDialog;
import com.google.gson.Gson;

public class BindPresenter {

    private Context context;
    private BindDialog bindDialog;

    private boolean accountLegal = false;       //用户名是否合法全局变量
    private boolean focusFlag = false;

    public BindPresenter(Context context, BindDialog bindDialog){
        this.context = context;
        this.bindDialog = bindDialog;
    }

    public void sendVerify(EditText editText, CountdownButton countBtn){
        //发送验证码按钮操作

        //关闭软键盘
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&bindDialog.getCurrentFocus()!=null){
            if (bindDialog.getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(bindDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        //绑定手机号后发送验证码
        String account = isAccountLegal(editText);
        if(accountLegal){
            countBtn.start();
            //进度条dialog
            final ProgressDialog alertDialog = new ProgressDialog(context);
            alertDialog.setCancelable(false);
            alertDialog.show();

            //打包请求验证码对象
            final RequestVerify requestVerify = new RequestVerify();
            requestVerify.setCt(3);       //绑定手机ct值为3
            requestVerify.setTelephone(account);
            requestVerify.setType("sdkCode");

            //新建线程使用OkHttp访问网络
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PostController postController = new PostController(requestVerify);
                    String result = postController.getResult();
                    if(result.equals("00")){
                        bindDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
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
                        bindDialog.showNotifyDialog(response.getMsg()+"");
                    }
                    //进度条dialog消失
                    alertDialog.dismiss();
                }
            }).start();

            //重置账号合法判别
            accountLegal = false;
        }
    }

    public void doBind(EditText accountEdit, final EditText verifyEdit){
        final String telephone = isAccountLegal(accountEdit);
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

                SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("account","");
                String password = sharedPreferences.getString("password","");

                //打包验证验证码对象
                final RequestLoginAndRegister request = new RequestLoginAndRegister();
                request.setType("appBind");
                request.setTelephone(telephone);
                request.setUsername(username);
                request.setPassword(password);
                request.setCode(codeStr);

                //新建线程使用OkHttp访问网络
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PostController postController = new PostController(request);
                        String result = postController.getResult();
                        if(result.equals("00")){
                            bindDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                        }else {
                            //用UpLoadResult对象解析获取的json
                            Gson gson = new Gson();
                            JsonResult response = gson.fromJson(result, JsonResult.class);

                            //获取其中code
                            int code = response.getCode();
                            if(code == 10001){
                                //验证成功

                                //绑定手机号成功，实现登录操作
                                SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("telephone",telephone);
                                editor.apply();

                                bindDialog.showNotifyDialog((String)response.getMsg(),1);
                            }else {
                                bindDialog.showNotifyDialog((String)response.getMsg());
                            }
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
                accountLegal = false;
            }
        }
    }

    public void doSkip(){
        //开启悬浮球,关闭界面
        FloatActionController.isLogined = true;
        FloatActionController.getInstance().startServer(context);
        bindDialog.cancel();
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
