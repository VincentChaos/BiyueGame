package com.example.rungame10.biyue.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rungame10.biyue.Model.JsonResult;
import com.example.rungame10.biyue.Model.RequestReset;
import com.example.rungame10.biyue.View.ConfirmDialog;
import com.example.rungame10.biyue.View.ProgressDialog;
import com.example.rungame10.biyue.View.VerifyDialog;
import com.google.gson.Gson;

public class ConfirmPresenter {

    private Context context;
    private ConfirmDialog confirmDialog;
    private String telephone;
    private String codeStr;

    private boolean pwdLegal = false;           //密码是否合法全局变量


    public ConfirmPresenter(Context context,ConfirmDialog confirmDialog){
        this.context = context;
        this.confirmDialog = confirmDialog;
    }

    public void setAccountAndCode(String telephone,String codeStr){
        this.telephone = telephone;
        this.codeStr = codeStr;
    }

    public void confirmPwd(EditText pwdEdit, EditText confirmEdit){
        final String pwdStr = isPwdLegal(pwdEdit);
        String confirmStr = confirmEdit.getText().toString().trim();
        if (pwdLegal){
            if(pwdStr.equals(confirmStr)){
                //进度条dialog
                final ProgressDialog alertDialog = new ProgressDialog(context);
                alertDialog.setCancelable(false);
                alertDialog.show();

                //打包
                final RequestReset requestReset = new RequestReset();
                requestReset.setType("appModify");
                requestReset.setCode(codeStr);
                requestReset.setTelephone(telephone);
                requestReset.setPassword(pwdStr);
                requestReset.setNewpassword(confirmStr);

                //新建线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PostController postController = new PostController(requestReset);
                        String result = postController.getResult();
                        if(result.equals("00")){
                            confirmDialog.showNotifyDialog("服务器连接异常，请更换网络环境");
                        }else {
                            //用UpLoadResult对象解析获取的json
                            Gson gson = new Gson();
                            JsonResult response = gson.fromJson(result, JsonResult.class);

                            //获取其中code
                            int code = response.getCode();
                            if(code == 10001){
                                //修改密码成功
                                //保存账号密码
                                SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("account",telephone);
                                editor.putString("password",pwdStr);
                                editor.apply();

                                confirmDialog.showNotifyDialog((String) response.getMsg(),1);
                            }else {
                                confirmDialog.showNotifyDialog((String )response.getMsg());
                            }
                        }
                        //进度条消失
                        alertDialog.dismiss();
                    }
                }).start();

            }else {
                Toast.makeText(context,"重复密码不一致",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void doReturn(){
        //返回按钮
        VerifyDialog verifyDialog = new VerifyDialog(context);
        verifyDialog.show();
        confirmDialog.cancel();
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
