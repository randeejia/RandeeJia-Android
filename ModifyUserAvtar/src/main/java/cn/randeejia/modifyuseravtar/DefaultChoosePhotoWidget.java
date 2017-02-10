package cn.randeejia.modifyuseravtar;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by randeejia on 2017/2/5.
 */

public class DefaultChoosePhotoWidget extends PopupWindow implements View.OnClickListener{

    private Context mContext;
    private View mContentView;
    private TextView mTakePhotoTextView;
    private TextView mChooseFromAlbumTextView;
    private FrameLayout mRootLayout;


    public DefaultChoosePhotoWidget(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mContentView = inflater.inflate(R.layout.layout_choose_photo, null);
        setContentView(mContentView);

        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x22000000);
        this.setBackgroundDrawable(dw);

        initView();

        // 设置popWindow的显示和消失动画
//        this.setAnimationStyle(R.style.mypopwindow_anim_style);
//        // 在底部显示
//        this.showAtLocation(view,Gravity.BOTTOM, 0, 0);

    }

    private void initView() {
        mRootLayout = (FrameLayout) mContentView.findViewById(R.id.layout_root);
        mTakePhotoTextView = (TextView) mContentView.findViewById(R.id.text_take_photo);
        mChooseFromAlbumTextView = (TextView) mContentView.findViewById(R.id.text_choose_from_album);

        mRootLayout.setOnClickListener(this);
        mTakePhotoTextView.setOnClickListener(this);
        mChooseFromAlbumTextView.setOnClickListener(this);
    }

    /**
     * 展示在某控件下面
     */
    public void show() {
        showAsDropDown(mContentView);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.text_take_photo){
            AppUtil.goToCamera((FragmentActivity) mContext,AppUtil.REQUEST_TAKE_PHOTO);
        }else if (viewId == R.id.text_choose_from_album){
            AppUtil.goToAlbumOfSystem((FragmentActivity) mContext,AppUtil.REQUEST_OPEN_ALBUM);
        }
        dismiss();
    }
}
