package com.example.rungame10.biyue.Model;

public class UploadResult {
    private int code;       //返回状态码
    private Object msg;     //返回信息

    public UploadResult(){}

    public UploadResult(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
