package com.example.rungame10.biyue.Model;

import com.google.gson.internal.LinkedTreeMap;

public class ResponseMsg {

    private LinkedTreeMap linkedTreeMap;
    private String openid;
    private String register;
    private String username;
    private String password;
    private String havePhone;

    public ResponseMsg(LinkedTreeMap linkedTreeMap){
        this.linkedTreeMap = linkedTreeMap;
        this.openid = (String) linkedTreeMap.get("openid");
        this.register = (String) linkedTreeMap.get("register");
        this.username = (String)linkedTreeMap.get("username");
        this.password = (String)linkedTreeMap.get("password");
        if(linkedTreeMap.containsKey("havePhone")){
            this.havePhone = linkedTreeMap.get("havePhone").toString();
        }
    }

    public String getOpenid() {
        return openid;
    }

    public String getRegister() {
        return register;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHavePhone(){
        return havePhone;
    }
}
