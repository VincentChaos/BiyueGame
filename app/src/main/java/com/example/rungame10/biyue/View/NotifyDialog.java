package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class NotifyDialog {

    private Context context;

    public NotifyDialog(Context context){
        this.context = context;
    }

    public void showNotifyDialog(String content){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("提示");
        alertDialog.setMessage(content);
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //点击退出弹出窗口
            }
        });
        alertDialog.show();
    }
}
