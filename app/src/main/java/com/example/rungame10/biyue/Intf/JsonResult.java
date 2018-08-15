package com.example.rungame10.biyue.Intf;

public class JsonResult {
    private int code;       //返回状态码
    private Object msg;     //返回信息

    public JsonResult(){}

    public JsonResult(int code, Object msg){
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
