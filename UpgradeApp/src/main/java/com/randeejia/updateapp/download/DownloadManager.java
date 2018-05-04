package com.randeejia.updateapp.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.randeejia.updateapp.bean.DownloadBean;

import static android.content.Context.CONNECTIVITY_SERVICE;


/**
 * Created by randeejia on 2017/6/23.
 */

public class DownloadManager implements IDownload {


    private static DownloadManager downloadManager;
    private final ConnectivityManager mConnectMgr;
    private DownloadBean mDownloadBean;
    private Context mContext;

    // 重点:发生从wifi切换到4g时,提示用户是否需要继续播放,此处有两种做法:
    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            NetworkInfo mobNetInfo = mConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = mConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                stop();
            }
        }
    };

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
         * @param errMsg 异常信息
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

        mConnectMgr = (ConnectivityManager)mContext.getSystemService(CONNECTIVITY_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.registerReceiver(connectionReceiver,intentFilter);
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
    public void stop() {
        mContext.unregisterReceiver(connectionReceiver);
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
