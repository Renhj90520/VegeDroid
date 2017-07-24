package com.xjconvenience.vege.vege.modules.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xjconvenience.vege.vege.Constants;
import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.VegeApplication;
import com.xjconvenience.vege.vege.models.LoginWrapper;
import com.xjconvenience.vege.vege.modules.orderlist.MainActivity;
import com.xjconvenience.vege.vege.utils.TokenUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ren Haojie on 2017/7/18.
 */

public class LoginActivity extends AppCompatActivity implements LoginContract.ILoginView {

    @BindView(R.id.user_name)
    EditText ed_user_name;
    @BindView(R.id.password)
    EditText ed_password;
    @BindView(R.id.user_name_clear)
    ImageButton user_name_clear;
    @BindView(R.id.login_submit)
    Button btn_submit;

    @OnClick(R.id.login_submit)
    public void onLogin() {
        LoginWrapper wrapper = new LoginWrapper();
        wrapper.setUserName(ed_user_name.getText().toString());
        wrapper.setPassword(ed_password.getText().toString());
        mPresenter.login(wrapper);
    }

    @OnClick(R.id.user_name_clear)
    public void onClear() {
        clearUserName();
    }

    @Inject
    LoginPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (TokenUtil.tokenVerify(getSharedPreferences(Constants.PREF_NAME, 0))) {
            navigateToMain();
        }

        ed_user_name.setText(getSharedPreferences(Constants.PREF_NAME, 0).getString("username", ""));
        DaggerLoginComponent.builder().vegeServicesComponent(((VegeApplication) getApplication()).getVegeServicesComponent())
                .loginModule(new LoginModule(this))
                .build()
                .inject(this);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void clearUserName() {
        ed_user_name.setText("");
    }

    @Override
    public void loginSuccess(String token) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.PREF_NAME, 0).edit();
        editor.putString(Constants.TOKEN_KEY, token);
        editor.putString("username", ed_user_name.getText().toString());
        editor.commit();
        navigateToMain();
    }

    @Override
    public void loginFailed(String message) {
        Toast.makeText(this, "登录失败：" + message, Toast.LENGTH_LONG).show();
    }
}
