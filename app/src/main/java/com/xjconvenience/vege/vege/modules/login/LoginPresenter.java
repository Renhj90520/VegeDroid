package com.xjconvenience.vege.vege.modules.login;

import com.xjconvenience.vege.vege.models.LoginWrapper;

import javax.inject.Inject;

/**
 * Created by Ren Haojie on 2017/7/19.
 */

public class LoginPresenter implements LoginContract.ILoginPresenter {

    private ILoginInteractor mLoginInteractor;
    private LoginContract.ILoginView mLoginView;

    @Inject
    public LoginPresenter(ILoginInteractor loginInteractor, LoginContract.ILoginView loginView) {
        mLoginInteractor = loginInteractor;
        mLoginView = loginView;
    }

    @Override
    public void login(LoginWrapper wrapper) {
        mLoginInteractor.login(wrapper, new ILoginInteractor.OnLoginListener() {
            @Override
            public void onLoginSuccess(String token) {
                mLoginView.loginSuccess(token);
            }

            @Override
            public void onLoginFailed(String message) {
                mLoginView.loginFailed(message);
            }
        });
    }
}
