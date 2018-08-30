package com.example.rungame10.biyue.Common;

import com.example.rungame10.biyue.Intf.LoginCallBack;
import com.example.rungame10.biyue.Intf.LogoutCallBack;

public class Config {
    //切换应用时保存悬浮窗坐标
    public static boolean isLogined;               //是否登录

    public static String APP_ID = "";           //应用ID
    public static String P_ID = "";             //平台ID
    public static String KEY = "";              //应用密钥

    public static LoginCallBack loginCallBack;         //登录回调
    public static LogoutCallBack logoutCallBack;       //退出回调
}
