package com.example.rungame10.biyue.Model;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Pay {

    private final static String HOST = "https://pay.biyue8.com/";
    private String uid;                              //用户id唯一值
    private String pid = Config.P_ID;                //平台id
    private String appid = Config.APP_ID ;           //游戏唯一标识
    private String ext;                              //其他参数
    private double money;                            //充值金额
    private String sign;                             //MD5签名
    private String key;

    public Pay(String uid, double money, @Nullable String ext){
        this.uid = uid;
        this.money = money;
        this.ext = ext;
        key = Config.KEY;
    }

    private String setSign(){
        //md5签名，
        String str = "";
        if(TextUtils.isEmpty(ext)){
            str = str + "appid=" + appid + "&money=" + money +"&pid=" + pid + "&uid=" + uid + "&key=" + key;
        }else {
            str = str + "appid=" + appid + "&ext=" + ext + "&money=" + money + "&pid=" + pid + "&uid=" + uid + "&key=" + key ;
        }

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            String result = "";
            for (byte b: bytes){
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1){
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getUrl(){

        sign = setSign();
        String url;
        if (TextUtils.isEmpty(ext)){
            url = HOST + "?uid=" + uid + "&pid=" + pid + "&appid=" + appid + "&money=" + money + "&sign="+sign + "&mobile=yes";
        }else {
            url = HOST + "?uid=" + uid + "&pid=" + pid + "&appid=" + appid + "&ext=" + ext +"&money=" + money + "&sign="+sign + "&mobile=yes";
        }

        return url;
    }

}
