package com.example.rungame10.biyue.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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


        final LibController libController = new LibController(TestActivity.this);
        libController.init("by1001","by_jymfyxy","rungame");
        libController.doLogin();

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                libController.doPay(0.01,null);
            }
        });

        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                libController.doLogout();
            }
        });
    }
}
