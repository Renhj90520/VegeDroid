package com.xjconvenience.vege.vege.receivers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.xjconvenience.vege.vege.Constants;
import com.xjconvenience.vege.vege.modules.login.LoginActivity;
import com.xjconvenience.vege.vege.modules.orderlist.MainActivity;
import com.xjconvenience.vege.vege.utils.TokenUtil;

/**
 * Created by Ren Haojie on 2017/8/4.
 */

public class MiPushReceiver extends PushMessageReceiver {
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, 0);
        if (TokenUtil.tokenVerify(prefs)) {
            //打开自定义的Activity
            Intent i = new Intent(context, MainActivity.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else {
            Intent intentLogin = new Intent(context, LoginActivity.class);
            context.startActivity(intentLogin);
        }
    }
}
