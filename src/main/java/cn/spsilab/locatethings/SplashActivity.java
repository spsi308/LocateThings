package cn.spsilab.locatethings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import cn.spsilab.locatethings.module.ResponseResult;

public class SplashActivity extends Activity implements NetworkService.NetworkCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkService.getInstance().autoLogin(this);
    }

    @Override
    public void onSuccess(ResponseResult result) {
        Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
        startMainActivity();
    }

    @Override
    public void onFailure(ResponseResult result, Throwable t) {
        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
