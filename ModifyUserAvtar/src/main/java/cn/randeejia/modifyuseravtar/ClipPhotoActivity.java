package cn.randeejia.modifyuseravtar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Date;

import cn.randeejia.util.LogUtil;
import cn.randeejia.util.StorageUtil;
import cn.randeejia.util.ToastUtil;

/**
 * 裁剪并保存图片
 */
public class ClipPhotoActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = LogUtil.makeLogTag(ClipPhotoActivity.class);

    public static final String PHOTO_NAME = "photo_name";
    public static final int INIT_SCALE = 2;


    private ImageView mScreenShotImg;
    private ImageButton mCancelBtn;
    private TextView mOkTextView;
    private ImageButton mRefreshBtn;
    private ClipView mClipview;
    private ProgressBar mLoadingPhoto;


    private int mScreenWidth;// 屏幕宽度
    private int mScreenHeight;// 屏幕高度
    private Bitmap bitmap;
    private Rect rectIV;
    private int statusBarHeight = 0;
    private int titleBarHeight = 0;
    private int angleInt = 0; // 旋转次数
    private int n = 0;// angleInt % 4 的值，用于计算旋转后图片区域

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();


    float minScaleR;// 最小缩放比例
    static final float MAX_SCALE = 10f;// 最大缩放比例

    // We can be in one of these 3 states
    static final int NONE = 0;// 初始状态
    static final int DRAG = 1;// 拖动
    static final int ZOOM = 2;// 缩放
    int mode = NONE;

    // Remember some things for zooming
    PointF prev = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_photo);

        initView();
        registerListeners();
        initViewTask();
    }

    private void initView() {
        mScreenShotImg = (ImageView) findViewById(R.id.imageView);
        mCancelBtn = (ImageButton) findViewById(R.id.cancel_btn);
        mOkTextView = (TextView) findViewById(R.id.ensure_btn);
        mRefreshBtn = (ImageButton) findViewById(R.id.rotate_btn);
        mClipview = (ClipView) findViewById(R.id.clip_view);
        mLoadingPhoto = (ProgressBar) findViewById(R.id.loading_photo);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    private void registerListeners() {
        mCancelBtn.setOnClickListener(this);
        mOkTextView.setOnClickListener(this);
        mRefreshBtn.setOnClickListener(this);
        mScreenShotImg.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancel_btn) {
            finish();

        } else if (i == R.id.ensure_btn) {// Bitmap fianBitmap = getBitmap();

            Intent intent = new Intent();
            Bitmap fianBitmap = getBitmap();

            StringBuffer stringBuffer = new StringBuffer(String.valueOf(new Date().getTime()));
            stringBuffer.append(".jpg");
            if (fianBitmap != null) {
                if (StorageUtil.savePhotoToSDCard(this,stringBuffer.toString(),fianBitmap)) {
                    intent.putExtra(PHOTO_NAME, stringBuffer.toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtil.showToast(ClipPhotoActivity.this, "sdCardError");
                }
            } else {
                ToastUtil.showToast(ClipPhotoActivity.this, getString(R.string.client_error));
            }

//            fianBitmap.recycle();
//            bitmap.recycle();

        } else if (i == R.id.rotate_btn) {
            rotateImageView(false);
        }
    }

    private void rotateImageView(boolean isCenter) {
        n = ++angleInt % 4;
        // 图片旋转-90度
        matrix.postRotate(90, mScreenShotImg.getWidth() / 2,
                mScreenShotImg.getHeight() / 2);
        savedMatrix.postRotate(90);
        if (isCenter) {
            center();
        }
        mScreenShotImg.setImageMatrix(matrix);
    }

    private void center() {
        center(true, true);
    }

    /**
     * 横向、纵向居中
     * @param horizontal 横向居中
     * @param vertical 纵向居中
     */
    protected void center(boolean horizontal, boolean vertical) {

        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移

            if (height < mScreenHeight) {
                deltaY = (mScreenHeight - height - statusBarHeight) / 2
                        - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < mScreenHeight) {
                deltaY = mScreenShotImg.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            if (width < mScreenWidth) {
                deltaX = (mScreenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right > mScreenWidth) {
                deltaX = (mScreenWidth - width) / 2 - rect.left;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
        if (n % 4 != 0) {
            matrix.postRotate(-90 * (n % 4),
                    mScreenShotImg.getWidth() / 2,
                    mScreenShotImg.getHeight() / 2);
        }
    }

    /* 获取矩形区域内的截图 */
    private Bitmap getBitmap() {
        getBarHeight();
        Bitmap screenShoot = takeScreenShot();
        Bitmap finalBitmap = Bitmap.createBitmap(screenShoot, ClipView.SX + 1,
                (mScreenHeight - mScreenWidth + statusBarHeight) / 2 + 1,
                mScreenWidth - ClipView.SX - ClipView.EX - 1, mScreenWidth - ClipView.SX
                        - ClipView.EX - 1);
        return finalBitmap;
    }

    private void getBarHeight() {
        // 获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        //这里便可以得到状态栏的高度，即最上面一条显示电量，信号等
        statusBarHeight = frame.top;
        //这里得到的是除了系统自带显示区域之外的所有区域，这里就是除了最上面的一条显示电量的状态栏之外的所有区域
        int contentTop = this.getWindow()
                .findViewById(Window.ID_ANDROID_CONTENT).getTop();
        // statusBarHeight是上面所求的状态栏的高度
        titleBarHeight = contentTop - statusBarHeight;

        LogUtil.v(TAG, "statusBarHeight = " + statusBarHeight
                + ", titleBarHeight = " + titleBarHeight);
    }

    // 获取Activity的截屏
    private Bitmap takeScreenShot() {
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /**
     * 下面的触屏方法摘自网上经典的触屏方法 只在判断是否在图片区域内做了少量修改
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 主点按下
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置初始点位置
                prev.set(event.getX(), event.getY());
                if (isOnCP(event.getX(), event.getY())) {
                    // 触点在图片区域内
                    LogUtil.d(TAG, "mode=DRAG");
                    mode = DRAG;
                } else {
                    mode = NONE;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                // 判断触点是否在图片区域内
                boolean isonpic = isOnCP(event.getX(), event.getY());
                LogUtil.d(TAG, "oldDist=" + oldDist);
                // 如果连续两点距离大于10，则判定为多点模式
                if (oldDist > 10f && isonpic) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                LogUtil.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - prev.x, event.getY()
                            - prev.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    LogUtil.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        CheckView();
        return true; // indicate event was handled
    }

    /**
     * 两点的距离 Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 两点的中点 Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 判断点所在的控制点
     *
     * @param evx
     * @param evy
     * @return
     */
    private boolean isOnCP(float evx, float evy) {
        if (rectIV == null) return false;
        float p[] = new float[9];
        matrix.getValues(p);
        float scale = Math.max(Math.abs(p[0]), Math.abs(p[1]));
        // 由于本人很久不用数学，矩阵的计算已经忘得差不多了，所以图片区域的计算只能按最笨的办法，
        // 根据旋转角度分四种情况计算图片区域，如果哪位达人修改一下只用一个表达式，那可以减少很多代码
        RectF rectf = null;
        switch (n) {
            case 0:
                rectf = new RectF(p[2], p[5], (p[2] + rectIV.width() * scale),
                        (p[5] + rectIV.height() * scale));
                break;
            case 1:
                rectf = new RectF(p[2], p[5] - rectIV.width() * scale, p[2]
                        + rectIV.height() * scale, p[5]);
                break;
            case 2:
                rectf = new RectF(p[2] - rectIV.width() * scale, p[5]
                        - rectIV.height() * scale, p[2], p[5]);
                break;
            case 3:
                rectf = new RectF(p[2] - rectIV.height() * scale, p[5], p[2], p[5]
                        + rectIV.width() * scale);
                break;
        }
        if (rectf != null && rectf.contains(evx, evy)) {
            return true;
        }
        return true;
    }

    /**
     * 最小缩放比例，最大为100%
     */
    private void minZoom() {
        minScaleR = Math.min((float) mScreenWidth / (float) bitmap.getWidth()
                / 2, (float) mScreenWidth / (float) bitmap.getHeight() / 2);
        if (minScaleR < 1.0 / 2) {
            float scale = Math.max(
                    (float) mScreenWidth / (float) bitmap.getWidth(),
                    (float) mScreenWidth / (float) bitmap.getHeight());
            matrix.postScale(scale, scale);
        } else {
            minScaleR = 1.0f;
        }
    }

    /**
     * 限制最大最小缩放比例
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        float scale = Math.max(Math.abs(p[0]), Math.abs(p[1]));
        if (mode == ZOOM) {
            if (scale < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
                center();
            }
            if (scale > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
    }

    /**
     * 获取状态栏高度 下面方法在oncreate中调用时获得的状态栏高度为0，不能用 Rect frame = new Rect();
     * getWindow().getDecorView().getWindowVisibleDisplayFrame(frame); int
     * statusBarHeight = frame.top;
     */
    private void getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getLocalizedMessage());
        }
    }

    private void initViewTask() {
        InitViewTask refreshTask = new InitViewTask();
        refreshTask.execute();
    }

    //异步任务
    private final class InitViewTask extends AsyncTask<String, String, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            Intent intent = getIntent();
            Uri uri = intent.getData();
            if (uri != null) {
                bitmap = AppUtil.getBitmapFromUri(ClipPhotoActivity.this, uri);
            }
            getStatusBarHeight();
            minZoom();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (bitmap != null) {
                mScreenShotImg.setImageBitmap(bitmap);
            }
            mLoadingPhoto.setVisibility(View.GONE);
            rectIV = mScreenShotImg.getDrawable().getBounds();
            matrix.setScale(INIT_SCALE, INIT_SCALE);
            center();
            mScreenShotImg.setImageMatrix(matrix);
        }
    }
}
