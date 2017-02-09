package cn.randeejia.util;

import android.content.Context;

/**
 * Created by randeejia on 2017/2/9.
 */

public class AppInfoUtil {

    public static String getPackageName(Context context) {
        String packageName ="";
        try {
            packageName = context.getPackageName();
        } catch (Exception e) {
        }
        return packageName;
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(getPackageName(context), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(getPackageName(context), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
