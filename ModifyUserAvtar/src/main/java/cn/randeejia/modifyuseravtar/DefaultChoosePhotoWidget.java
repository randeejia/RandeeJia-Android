package cn.randeejia.modifyuseravtar;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
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


    public DefaultChoosePhotoWidget(final Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mContentView = inflater.inflate(R.layout.layout_choose_photo, null);
        initView();
        DisplayMetrics dm = new DisplayMetrics();
        ((FragmentActivity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;

        setWidth(mScreenWidth);
        setHeight(mScreenHeight);


        setContentView(mContentView);
        setFocusable(true);
        setOutsideTouchable(false);
//        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.translucence));
//        setBackgroundDrawable(dw);

    }

    private void initView() {
        mTakePhotoTextView = (TextView) mContentView.findViewById(R.id.text_take_photo);
        mChooseFromAlbumTextView = (TextView) mContentView.findViewById(R.id.text_choose_from_album);

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
