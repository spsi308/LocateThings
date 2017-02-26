package cn.spsilab.locatethings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cn.spsilab.locatethings.loginmodule.LoginService;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginService.checkIsLogin(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
