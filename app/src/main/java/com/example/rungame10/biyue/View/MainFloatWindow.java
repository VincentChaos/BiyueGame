package com.example.rungame10.biyue.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.rungame10.biyue.Common.Config;
import com.example.rungame10.biyue.Util.MResource;


public class MainFloatWindow extends FrameLayout {
    private final static int MSG_UPDATE_POS = 1;

    private Context context;
    private ImageView ivDefault;                     //显示悬浮窗
    private boolean isClick;
    private float mTouchStartX, mTouchStartY;         //手指按下时坐标
    private long startTime,endTime;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private boolean isOnLeft;                       //控件是否在左边
    private int[] location = new int[2];
    int moveParam;

    public MainFloatWindow(Context context){
        this(context,null);
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_POS:
                    //处理贴边方法传递的xy值刷新悬浮窗
                    mParams.x = msg.arg1;
                    mParams.y = msg.arg2;
                    location[0] = location[0] + moveParam;
                    mWindowManager.updateViewLayout(MainFloatWindow.this,mParams);
                    break;
            }
        }
    };

    public MainFloatWindow(Context context, AttributeSet attrs) {
        super(context,attrs);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        this.context = context.getApplicationContext();
        LayoutInflater.from(context).inflate(MResource.getIdByName(context, "layout", "float_win_layout"),this);

        ivDefault = (ImageView) findViewById(MResource.getIdByName(context, "id", "iv_default"));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指放下
                startTime = System.currentTimeMillis();
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //图标移动的逻辑在这里
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();

                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    // 更新浮动窗口位置参数
                    mParams.x = (int) (x - mTouchStartX);
                    mParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mParams);
                    location[0] = mParams.x;
                    location[1] = mParams.y;
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                //手指放开
                endTime = System.currentTimeMillis();
                //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                if ((endTime - startTime) > 0.1 * 1000L) {
                    isClick = false;
                } else {
                    isClick = true;
                }

                //等待0.5秒后自动贴边
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DisplayMetrics d = context.getResources().getDisplayMetrics();
                isOnLeft = (location[0] + getWidth()/2) <=(d.widthPixels/2);
                moveParam = isOnLeft ? -10 : 10;
                autoMoveToSide();       //自动贴边
                break;
        }//响应点击事件
        if (isClick) {
            Toast.makeText(context, " 点击事件。。", Toast.LENGTH_SHORT).show();
            isClick = false;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }


    private void autoMoveToSide(){
        new Thread(){
            @Override
            public void run(){
                //实现自动检测悬浮窗左右然后实现自动贴边
                DisplayMetrics d = context.getResources().getDisplayMetrics();
                while (true){
                    int newX = location[0];
                    int newY = location[1];
                    Log.e("X:"+location[0],isOnLeft+"");
                    if (isOnLeft && newX<=0) {     //已移至最左侧
                        newX = 0;
                        Message message = new Message();
                        message.what = MSG_UPDATE_POS;
                        message.arg1 = newX;
                        message.arg2 = newY;
                        handler.sendMessage(message);
                        Config.saveX = newX;
                        Config.saveY = newY;
                        //保存当前悬浮窗位置
                        break;
                    }else if(!isOnLeft && newX>=d.widthPixels){     //已移至最右侧
                        newX = d.widthPixels;
                        Message message = new Message();
                        message.what = MSG_UPDATE_POS;
                        message.arg1 = newX;
                        message.arg2 = newY;
                        handler.sendMessage(message);
                        Config.saveX = newX;
                        Config.saveY = newY;
                        //保存当前悬浮窗位置
                        break;
                    } else {
                        Message message = new Message();
                        message.what = MSG_UPDATE_POS;
                        message.arg1 = newX;
                        message.arg2 = newY;
                        handler.sendMessage(message);
                    }

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

}
