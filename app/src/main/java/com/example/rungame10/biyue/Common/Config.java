package com.example.rungame10.biyue.Common;

import com.tencent.mm.opensdk.openapi.IWXAPI;

public class Config {
    public static final String APP_ID = "wxc985beae1a4e1d4b";
    public static final String APP_SECRET = "69f4822b0c285920007537c96d24968c";
    public static IWXAPI wx_api;

    //切换应用时保存悬浮窗坐标
    public static int saveX = -100,saveY = -100;
}
