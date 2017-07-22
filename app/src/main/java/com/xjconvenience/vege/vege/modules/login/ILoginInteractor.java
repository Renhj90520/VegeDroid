package com.xjconvenience.vege.vege.modules.login;

import com.xjconvenience.vege.vege.models.LoginWrapper;

/**
 * Created by Ren Haojie on 2017/7/19.
 */

public interface ILoginInteractor {
    interface OnLoginListener {
        void onLoginSuccess(String token);

        void onLoginFailed(String message);
    }

    void login(LoginWrapper wrapper, OnLoginListener listener);
}
