package cn.randeejia.app;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import cn.randeejia.galleryview.GalleryTransformer;

public class GalleryDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_demo);

        final RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.activity_gallery_demo);

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setPageTransformer(false, new GalleryTransformer());
        mViewPager.setOffscreenPageLimit(3);
        // 将父节点Layout事件分发给viewpager，否则只能滑动中间的一个view对象
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });

        //设置ViewPager的滑动监听,必须在滑动的时候刷新界面才能看到效果
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //必须刷新页面,才能看到效果
                if (rootLayout != null) {
                    rootLayout.invalidate();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(this);
        mViewPager.setAdapter(mAdapter);
    }
}
