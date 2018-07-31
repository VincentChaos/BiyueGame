package com.example.rungame10.biyue.Model;

import com.example.rungame10.biyue.R;

public class Res {

    public static final class layout{
        public static int login(){
            return R.layout.dialog_login;
        }
        public static int register(){
            return R.layout.dialog_register;
        }

        public static int floatWin(){
            return R.layout.float_win_layout;
        }
    }

    public static final class style{
        public static int dialogStyle(){
            return R.style.Dialog;
        }
    }

    public static final class view{
        public static int accountEdit(){
            return R.id.edit_account;
        }
        public static int pwdEdit(){
            return R.id.edit_pwd;
        }
        public static int verifiEdit(){
            return R.id.edit_verification;
        }
        public static int loginBtn(){
            return R.id.btn_login;
        }
        public static int registerBtn(){
            return R.id.btn_register;
        }

        public static int loginOneBtn(){
            return R.id.btn_login_one;
        }
        public static int forgetBtn(){
            return R.id.btn_forget;
        }
        public static int wechatBtn(){
            return R.id.btn_wechat;
        }
        public static int qqBtn(){
            return R.id.btn_qq;
        }
        public static int returnBtn(){
            return R.id.btn_return;
        }
        public static int verifiBtn(){
            return R.id.btn_verification;
        }
    }
}
