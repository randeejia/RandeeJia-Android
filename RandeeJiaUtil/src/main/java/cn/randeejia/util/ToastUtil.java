package cn.randeejia.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by kephi on 13-12-4.
 */
public class ToastUtil {

	public static Toast getToast(Context mContext, String content) {
		return getToast(mContext, content, 1000);
	}
	
	public static Toast getToast(Context mContext, String content, int duration) {
		Toast toast = new Toast(mContext);
		toast.setDuration(duration);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.layout_toast, null);
		TextView showTextView = (TextView) view.findViewById(R.id.text_toast_content);
		showTextView.setText(content);
		toast.setView(view);
		return toast;
	}

	public static void showToast(Context mContext, String content) {
		getToast(mContext, content).show();
	}
	
	public static void showToast(Context mContext, String content, int duration) {
		getToast(mContext, content,duration).show();
	}

}
