package com.xjconvenience.vege.vege;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xjconvenience.vege.vege.webservices.DaggerHttpComponent;
import com.xjconvenience.vege.vege.webservices.DaggerVegeServicesComponent;
import com.xjconvenience.vege.vege.webservices.HttpComponent;
import com.xjconvenience.vege.vege.webservices.HttpModule;
import com.xjconvenience.vege.vege.webservices.VegeServices;
import com.xjconvenience.vege.vege.webservices.VegeServicesComponent;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Ren Haojie on 2017/7/19.
 */

public class VegeApplication extends Application {
    VegeServicesComponent mVegeServicesComponent;

    public VegeServicesComponent getVegeServicesComponent() {
        return mVegeServicesComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        resolveDependencies();
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
    }

    private void resolveDependencies() {
        mVegeServicesComponent = DaggerVegeServicesComponent.builder()
                .httpComponent(getHttpComponent())
                .build();
    }

    private HttpComponent getHttpComponent() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, 0);
        return DaggerHttpComponent.builder()
                .httpModule(new HttpModule(prefs))
                .build();
    }
}
