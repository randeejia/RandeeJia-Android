package com.randeejia.updateapp.download;

public interface IDownload {
    /**
     * 开始下载
     */
    void start();
    /**
     * 开始下载
     * @param callback 回调方法
     */
    void start(DownloadManager.DownloadCallback callback);

    /**
     * 停止下载
     */
    void stop();
}
