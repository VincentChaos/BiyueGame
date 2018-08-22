package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rungame10.biyue.Util.CashierInputFilter;
import com.example.rungame10.biyue.Util.MResource;

public class ChargeDialog extends AlertDialog {
    private Context context;
    private EditText moneyEdit;
    private TextView payBtn;

    public ChargeDialog(Context context){
        super(context, MResource.getIdByName(context, "style", "Dialog"));
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(MResource.getIdByName(context,"layout","dialog_charge"),null);
        setContentView(view);

        //声明
        moneyEdit = (EditText)view.findViewById(MResource.getIdByName(context,"id","edit_money"));
        payBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","btn_pay"));

        InputFilter[] filters = {new CashierInputFilter()};
        moneyEdit.setFilters(filters);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String moneyStr = moneyEdit.getText().toString().trim();
                SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                if(sharedPreferences.contains("openid")){
                    String uid = sharedPreferences.getString("openid","");
                    PayDialog payDialog = new PayDialog(context,uid,Double.parseDouble(moneyStr),null);
                    payDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    payDialog.show();
                    ChargeDialog.this.cancel();
                }
            }
        });

        //设置宽高
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高
        lp.width = (int) (d.widthPixels*0.8);
        lp.height = (int) (d.heightPixels*0.25);
        dialogWindow.setAttributes(lp);

        //显示alertDialog的软键盘
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}

