package com.randeejia.updateapp.download;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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
    private Call newCall;

    public DownloadService() {
        super(TAG);
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
        cancelDownload();
    }

    private void download(final DownloadBean downloadBean){
        Request request = new Request.Builder().url(downloadBean.getDownloadUrl()).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        newCall = okHttpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,e.getLocalizedMessage());
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
                Log.e(TAG,"onResponse success");
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
        if (newCall !=null && newCall.isExecuted()){
            newCall.isCanceled();
        }
    }
}
