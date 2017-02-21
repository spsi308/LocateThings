package cn.spsilab.locatethings.loginmodule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.spsilab.locatethings.NetworkService;
import cn.spsilab.locatethings.R;
import cn.spsilab.locatethings.module.ResponseResult;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, NetworkService.NetworkCallback{

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
    public void onBackPressed() {
        super.onBackPressed();
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
                RegistFragment registFragment = new RegistFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_login_main_content, registFragment, null)
                        .addToBackStack("regist")
                        .commit();
                break;
            case R.id.btn_forget_password :
                //
                FindPasswordFragment findPasswordFragment = new FindPasswordFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_login_main_content, findPasswordFragment, null)
                        .addToBackStack("find")
                        .commit();
                break;
            default:
                onBackPressed();
        }

    }

    private void login() {
        String name = nameTextView.getText().toString();
        String password = passwdTextView.getText().toString();

        NetworkService.getInstance().login(name, password, this);
    }

    @Override
    public void onSuccess(ResponseResult result) {
        int status = result.getStatus();
        if (status == idToInt(R.integer.LOGIN_SUCCESS)) {
            Toast.makeText(this, "login success", Toast.LENGTH_SHORT).show();
            //login success , user data will be save in application
            setResult(status);
            finish();
            onBackPressed();
        } else if (status == idToInt(R.integer.REGIST_SUCCESS)) {
            Toast.makeText(this, "regist success", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    @Override
    public void onFailure(ResponseResult result, Throwable t) {
        int status = result.getStatus();

        if (status == idToInt(R.integer.LOGIN_FAILED)) {
            Toast.makeText(this, "login failed", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            intent.putExtra("msg", result.getMsg());
            setResult(status, intent);
            finish();
        } else if (status == idToInt(R.integer.NO_CONNECTION)) {
            Toast.makeText(this, "no network", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            intent.putExtra("msg", result.getMsg());
            setResult(idToInt(R.integer.NO_CONNECTION), intent);
            finish();
        } else if (status == idToInt(R.integer.REGIST_FAILED)) {
            Toast.makeText(this, "regist failed", Toast.LENGTH_SHORT).show();
        }


    }

    private int idToInt(int id) {
        return getResources().getInteger(id);
    }

}
