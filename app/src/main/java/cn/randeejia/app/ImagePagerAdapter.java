package cn.randeejia.app;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by randeejia on 2017/2/10.
 */

public class ImagePagerAdapter extends PagerAdapter {

    private static final String TAG = "ImagePagerAdapter";

    private int[] images= {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e};
    private Context context;

    public ImagePagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageDrawable(context.getResources().getDrawable(images[position]));
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView imageView = (ImageView)object;
        imageView.setImageDrawable(null);
        container.removeView((ImageView)object);
    }
}
