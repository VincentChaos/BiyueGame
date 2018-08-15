package com.example.rungame10.biyue.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Presenter.FloatActionController;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class BiYueApplication extends Application {


    //获取到主线程的上下文
    private static BiYueApplication context = null;
    //获取到主线程的handler
    private static Handler mainThreadHandler = null;
    //获取到主线程的looper
    private static Looper mainThreadLooper = null;
    //获取到主线程
    private static Thread mainThread = null;
   //获取到主线程的id
    private static int mainThreadId;

    //生命周期计数器
    private int finalCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = this;
        this.mainThreadHandler = new Handler();
        this.mainThreadLooper = getMainLooper();
        this.mainThread = Thread.currentThread();
        this.mainThreadId = Process.myTid();

        //生命周期监听回调，根据application的生命周期开启和关闭悬浮球服务
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                finalCount ++;
                if(finalCount == 1){
                    //说明后台回到前台
                    FloatActionController.getInstance().startServer(context);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                finalCount --;
                if(finalCount == 0){
                    //说明从前台回到后台
                    FloatActionController.getInstance().stopServer(context);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        regToWx();

    }

    public static Context getInstance() {
        return context;
    }

    private void regToWx(){
        //通过WXAPI工厂，获取api实例
        Config.wx_api = WXAPIFactory.createWXAPI(context, Config.WX_APP_ID,true);
        Config.wx_api.registerApp(Config.WX_APP_ID);
        Log.e("register","wxapi");
    }

    // 获取Application
    public static BiYueApplication getApplication() {
        return context;
    }

    public static Handler getMainThreadHandler() {
        return mainThreadHandler;
    }

   public static Looper getMainThreadLooper() {
        return mainThreadLooper;
     }

     public static Thread getMainThread() {
         return mainThread;
     }

    public static int getMainThreadId() {
         return mainThreadId;
    }

}
