package cn.randeejia.util;

import android.text.TextUtils;
import android.util.Log;
/**
 * Created by randeejia on 2017/2/9.
 */
public class LogUtil {

	private static boolean debug = BuildConfig.DEBUG;

	public static String makeLogTag(String prefixName,Class<?> cls) {
		StringBuffer stringBuffer = new StringBuffer(prefixName);
		stringBuffer.append(cls.getSimpleName());
		return stringBuffer.toString();
	}

	public static String makeLogTag(Class<?> cls) {
		return cls.getSimpleName();
	}

	public static void v(String tag, String msg) {
		if (debug && !TextUtils.isEmpty(msg)) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (debug && !TextUtils.isEmpty(msg)) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (debug && !TextUtils.isEmpty(msg)) {
			int maxLogSize = 1000;
			for (int i = 0; i <= msg.length() / maxLogSize; i++) {
				int start = i * maxLogSize;
				int end = (i + 1) * maxLogSize;
				end = end > msg.length() ? msg.length() : end;
				Log.i(tag, msg.substring(start, end));
			}
		}
	}

	public static void w(String tag, String msg) {
		if (debug && !TextUtils.isEmpty(msg)) {
			int maxLogSize = 1000;
			for (int i = 0; i <= msg.length() / maxLogSize; i++) {
				int start = i * maxLogSize;
				int end = (i + 1) * maxLogSize;
				end = end > msg.length() ? msg.length() : end;
				Log.w(tag, msg.substring(start, end));
			}
		}
	}

	public static void e(String tag, String msg) {
		if (debug && !TextUtils.isEmpty(msg)) {
			Log.e(tag, msg);
		}

	}public static void e(String tag, String msg, Throwable tr) {
		if (debug && !TextUtils.isEmpty(msg)) {
			Log.e(tag,msg,tr);
		}
	}

}
