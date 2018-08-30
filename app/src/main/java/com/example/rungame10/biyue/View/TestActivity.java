package com.example.rungame10.biyue.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.rungame10.biyue.Intf.LoginCallBack;
import com.example.rungame10.biyue.Intf.LogoutCallBack;
import com.example.rungame10.biyue.Intf.InitCallBack;
import com.example.rungame10.biyue.SDK.LibController;
import com.example.rungame10.biyue.Util.MResource;

public class TestActivity extends AppCompatActivity {
    private Button test,test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(TestActivity.this,"layout","by_activity_test"));

        test = (Button)this.findViewById(MResource.getIdByName(TestActivity.this,"id","by_test"));
        test = (Button)this.findViewById(MResource.getIdByName(TestActivity.this,"id","by_test2"));


        LibController.getInstance(TestActivity.this).init("by1001", "by_jymfyxy", "rungame", new InitCallBack() {
            @Override
            public void onResponse(int resultCode) {

            }
        });
        LibController.getInstance(TestActivity.this).doLogin(new LoginCallBack() {
            @Override
            public void onResponse(int resultCode) {

            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LibController.getInstance(TestActivity.this).doPay(0.01,null);
            }
        });

        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LibController.getInstance(TestActivity.this).doLogout(new LogoutCallBack() {
                    @Override
                    public void onResponse(int resultCode) {

                    }
                });
            }
        });
    }
}
