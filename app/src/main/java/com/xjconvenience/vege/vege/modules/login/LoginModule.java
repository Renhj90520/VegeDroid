package com.xjconvenience.vege.vege.modules.login;

import com.xjconvenience.vege.vege.webservices.VegeServices;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ren Haojie on 2017/7/19.
 */
@Module
public class LoginModule {
    private final LoginContract.ILoginView mLoginView;

    public LoginModule(LoginContract.ILoginView loginView) {
        mLoginView = loginView;
    }

    @Provides
    LoginContract.ILoginView provideLoginView() {
        return mLoginView;
    }

    @Provides
    ILoginInteractor provideInteractor(VegeServices services) {
        return new LoginInteractor(services);
    }

    @Provides
    LoginContract.ILoginPresenter provideLoginPresenter(LoginContract.ILoginView view, ILoginInteractor interactor) {
        return new LoginPresenter(interactor, view);
    }

}
