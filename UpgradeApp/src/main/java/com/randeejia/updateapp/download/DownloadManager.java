package com.randeejia.updateapp.download;

import android.content.Context;
import android.content.Intent;

import com.randeejia.updateapp.bean.DownloadBean;


/**
 * Created by randeejia on 2017/6/23.
 */

public class DownloadManager implements IDownload {


    private static DownloadManager downloadManager;
    private DownloadBean mDownloadBean;
    private Context mContext;

    /**
     * 下载回调接口
     */
    public interface DownloadCallback {
        /**
         * 下载成功
         */
        void onSuccess();

        /**
         * 正在下载
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onFailed(String errMsg);

        /**
         * 下载完成，无论成功或失败都执行该方法
         */
        void onCompleted();
    }

    private DownloadManager(Context context,DownloadBean downloadBean) {
        mContext = context;
        mDownloadBean = downloadBean;
    }

    public static DownloadManager getInstance(Context context,DownloadBean downloadBean){
        if (downloadManager ==null){
            downloadManager = new DownloadManager(context,downloadBean);
        }
        return downloadManager;
    }


    @Override
    public void start() {
        startService();
    }

    @Override
    public void start(DownloadCallback callback) {
        DownloadService.setDownloadCallback(callback);
        startService();
    }


    @Override
    public void cancel() {
        stopService();
    }

    @Override
    public void stop() {
        stopService();
    }

    private void startService() {
        Intent intent = new Intent(mContext,DownloadService.class);
        intent.putExtra(DownloadService.DOWNLOAD_BEAN,mDownloadBean);
        mContext.startService(intent);
    }

    private void stopService(){
        Intent intent = new Intent(mContext,DownloadService.class);
        mContext.stopService(intent);
    }
}
