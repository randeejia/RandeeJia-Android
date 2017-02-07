package cn.randeejia.modifyuseravtar;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by randeejia on 2017/2/7.
 */

public class MyPreference {

    private static final String IMAGE_PATH = "image_path";

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    private static MyPreference mPreference;

    private MyPreference(Context context) {
        SharedPreferences localSharedPreferences = context.getSharedPreferences("MyConfig", 0);

        mPref = localSharedPreferences;

        SharedPreferences.Editor localEditor = mPref.edit();
        mEditor = localEditor;
    }

    public static MyPreference getInstance(Context context) {
        if (mPreference == null) {
            mPreference = new MyPreference(context);
        }
        return mPreference;
    }

    public void setImageCache(String imagePath) {
        mEditor.putString(IMAGE_PATH, imagePath);
        mEditor.commit();
    }

    public String getImageCache() {
        return mPref.getString(IMAGE_PATH, "");
    }
}
