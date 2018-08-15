package com.example.rungame10.biyue.Model;

public class RequestVerify {

    private int ct;                     //短信验证码类型
    private String telephone;           //用户手机号
    private String type;                //操作类型

    public int getCt() {
        return ct;
    }

    public void setCt(int ct) {
        this.ct = ct;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
