package com.example.rungame10.biyue.Intf;

public interface HttpCallBackListener {

    void onFinish(String response);

    void onError(Exception e);

}