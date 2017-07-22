package com.xjconvenience.vege.vege.modules.login;

import com.xjconvenience.vege.vege.models.LoginWrapper;

/**
 * Created by Ren Haojie on 2017/7/19.
 */

public class LoginContract {
    public interface ILoginView {
        void clearUserName();

        void loginSuccess(String token);

        void loginFailed(String message);
    }

    public interface ILoginPresenter {
        void login(LoginWrapper wrapper);
    }
}
