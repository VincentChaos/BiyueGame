package com.example.rungame10.biyue.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rungame10.biyue.Presenter.FloatActionController;
import com.example.rungame10.biyue.Util.MResource;

public class SwitchDialog extends AlertDialog {
    private Context context;
    private PopupWindow popupWindow;
    private TextView nameText,logoutBtn;

    public SwitchDialog(Context context,PopupWindow popupWindow){
        super(context, MResource.getIdByName(context, "style", "Dialog"));
        this.context = context;
        this.popupWindow = popupWindow;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(MResource.getIdByName(context,"layout","dialog_switch"),null);
        setContentView(view);

        //声明
        nameText = (TextView)view.findViewById(MResource.getIdByName(context,"id","text_name"));
        logoutBtn = (TextView)view.findViewById(MResource.getIdByName(context,"id","btn_logout"));

        final SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        String nameStr = "用户名称： " + sharedPreferences.getString("user_name","");
        nameText.setText(nameStr);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                popupWindow.dismiss();
                FloatActionController.isLogined = false;
                FloatActionController.getInstance().stopServer(context);
                SwitchDialog.this.cancel();
                Toast.makeText(context,"用户退出登录成功，请重新登录",Toast.LENGTH_SHORT).show();
            }
        });

        //设置宽高
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高
        lp.width = (int) (d.widthPixels*0.8);
        lp.height = (int) (d.heightPixels*0.3);
        dialogWindow.setAttributes(lp);
    }
}
