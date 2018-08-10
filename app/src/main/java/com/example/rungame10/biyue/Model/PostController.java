package com.example.rungame10.biyue.Model;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018-4-13 0013.
 */

public class PostController {
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    private static final String HOST = "https://www.biyue8.com/micro/appSdk.php";
    private Object object;
    private String result = "00" ;

    public PostController(Object o){
        this.object = o;
    }

    public String getResult() {

        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .build();

        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        Gson g = new Gson();
        String json = g.toJson(object);
        Log.e("createJson",json);
        //json为String类型的json数据
        RequestBody requestBody = RequestBody.create(JSON, json);
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(HOST)
                .post(requestBody)
                .build();
        //发送请求获取响应
        try {
            okhttp3.Response response = okHttpClient.newCall(request).execute();
            //判断请求是否成功
            if (response.isSuccessful()) {
                //打印服务端返回结果
                result = response.body().string();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
