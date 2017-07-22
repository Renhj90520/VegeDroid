package com.xjconvenience.vege.vege.modules.login;

import android.util.Log;

import com.xjconvenience.vege.vege.models.LoginWrapper;
import com.xjconvenience.vege.vege.models.Result;
import com.xjconvenience.vege.vege.webservices.VegeServices;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by Ren Haojie on 2017/7/19.
 */

public class LoginInteractor implements ILoginInteractor {
    VegeServices mService;

    @Inject
    public LoginInteractor(VegeServices service) {
        mService = service;
    }

    @Override
    public void login(LoginWrapper wrapper, final OnLoginListener listener) {
        Observable loginResp = mService.login(wrapper);
        loginResp.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result<String>>() {

                    @Override
                    public void accept(@NonNull Result<String> stringResult) throws Exception {
                        if (stringResult.getState() == 1) {
                            listener.onLoginSuccess(stringResult.getBody());
                        } else {
                            listener.onLoginFailed(stringResult.getMessage());
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        listener.onLoginFailed(throwable.getMessage());
                        Log.e("Login", "---------------------------------", throwable);
                    }
                });
    }
}
