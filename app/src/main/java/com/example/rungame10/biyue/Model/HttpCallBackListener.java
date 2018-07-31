package com.example.rungame10.biyue.Model;

public interface HttpCallBackListener {


    void onFinish(String response);


    void onError(Exception e);

}