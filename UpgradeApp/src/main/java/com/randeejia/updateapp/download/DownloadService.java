package com.randeejia.updateapp.download;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.randeejia.updateapp.bean.DownloadBean;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载APK服务
 */
public class DownloadService extends IntentService{

    private static final String TAG = "DownloadService";

    public static final String DOWNLOAD_BEAN = "download_bean";

    private static DownloadManager.DownloadCallback sCallback;
    private ConnectivityManager mConnectMgr;
    private Call newCall;

    // 重点:发生从wifi切换到4g时,提示用户是否需要继续播放,此处有两种做法:
    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            NetworkInfo mobNetInfo = mConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = mConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                cancelDownload();
            }
        }
    };


    public DownloadService() {
        super(TAG);
        mConnectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectionReceiver,intentFilter);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DownloadBean downloadBean = intent.getParcelableExtra(DOWNLOAD_BEAN);
        if (downloadBean !=null){
            download(downloadBean);
        }else{
            throw new RuntimeException("DownloadBean can't be null");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionReceiver);
    }

    private void download(final DownloadBean downloadBean){
        Request request = new Request.Builder().url(downloadBean.getDownloadUrl()).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        newCall = okHttpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

//                FileUtils.deleteFile(outFile);
                if (sCallback !=null){ // 下载失败
                    sCallback.onFailed(e.getLocalizedMessage());
                    sCallback.onCompleted();
                }
                //下载失败停止服务
                stopSelf();
            }
            @Override
            public void onResponse(Call call, Response response) {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();

                    downloadBean.setTotalFileSize(total);

                    fos = new FileOutputStream(downloadBean.getOutputPath());
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;

                        downloadBean.setCurrentFileSize(sum);

                        int progress = (int) (sum * 1.0f / total * 100);

                        downloadBean.setProgress(progress);

                        if (sCallback !=null){ //下载中
                            sCallback.onDownloading(progress);
                        }
                    }
                    fos.flush();
                    if (sCallback !=null){  // 下载完成
                        sCallback.onSuccess();
                    }

                } catch (Exception e) {
//                    FileUtils.deleteFile(outFile);
                    if (sCallback !=null){  //下载异常
                        sCallback.onFailed(e.getLocalizedMessage());
                    }

                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }

                if (sCallback !=null){//下载完成
                    sCallback.onCompleted();
                }
                //下载完成停止服务
                stopSelf();
            }
        });
    }

    public static void setDownloadCallback(DownloadManager.DownloadCallback callback){
        sCallback = callback;
    }

    private void cancelDownload(){
        newCall.cancel();
    }
}
