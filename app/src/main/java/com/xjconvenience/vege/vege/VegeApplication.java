package com.xjconvenience.vege.vege;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xiaomi.mipush.sdk.MiPushClient;
import com.xjconvenience.vege.vege.webservices.DaggerHttpComponent;
import com.xjconvenience.vege.vege.webservices.DaggerVegeServicesComponent;
import com.xjconvenience.vege.vege.webservices.HttpComponent;
import com.xjconvenience.vege.vege.webservices.HttpModule;
import com.xjconvenience.vege.vege.webservices.VegeServices;
import com.xjconvenience.vege.vege.webservices.VegeServicesComponent;

import android.os.Process;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Ren Haojie on 2017/7/19.
 */

public class VegeApplication extends Application {
    VegeServicesComponent mVegeServicesComponent;
    private static final String APP_ID = "1000270";
    private static final String APP_KEY = "670100056270";

    public VegeServicesComponent getVegeServicesComponent() {
        return mVegeServicesComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        resolveDependencies();

        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
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

    private boolean shouldInit() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
