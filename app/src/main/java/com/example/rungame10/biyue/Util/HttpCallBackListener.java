package com.example.rungame10.biyue.Util;

public interface HttpCallBackListener {


    void onFinish(String response);


    void onError(Exception e);

}