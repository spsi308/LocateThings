package cn.spsilab.locatethings.loginmodule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.spsilab.locatethings.R;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button clearNameBtn,
            clearPasswordBtn,
            loginBtn,
            forgetPasswdBtn,
            registBtn;
    private TextView nameTextView,
            passwdTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        clearNameBtn = (Button) findViewById(R.id.btn_clear_user_name);
        clearPasswordBtn = (Button) findViewById(R.id.btn_clear_user_password);
        loginBtn = (Button) findViewById(R.id.btn_login);
        forgetPasswdBtn = (Button) findViewById(R.id.btn_forget_password);
        registBtn = (Button) findViewById(R.id.btn_register);

        nameTextView = (TextView) findViewById(R.id.edit_login_user_name);
        passwdTextView = (TextView) findViewById(R.id.edit_login_password);

        clearNameBtn.setOnClickListener(this);
        clearPasswordBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        forgetPasswdBtn.setOnClickListener(this);
        registBtn.setOnClickListener(this);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_clear_user_name :
                //清除用户名的同时清除密码
                nameTextView.setText("");
            case R.id.btn_clear_user_password :
                passwdTextView.setText("");
                break;
            case R.id.btn_login :
                //登录
                login();
                break;
            case R.id.btn_register :
                regist();
                break;
            case R.id.btn_forget_password :
                //


        }

    }

    private void login() {

    }

    private void regist(){}

}
