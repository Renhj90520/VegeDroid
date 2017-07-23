package com.xjconvenience.vege.vege.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by Ren Haojie on 2017/7/23.
 */

public class PackUtil {
    public static boolean isInstalled(Context context, String packageName) {
        final PackageManager pkm = context.getPackageManager();
        List<PackageInfo> packageInfos = pkm.getInstalledPackages(0);
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String pkName = packageInfos.get(i).packageName;
                if (packageName.equals(pkName)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }
}
