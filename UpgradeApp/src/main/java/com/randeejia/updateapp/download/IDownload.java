package com.randeejia.updateapp.download;

public interface IDownload {
    void start();
    void start(DownloadManager.DownloadCallback callback);
    void cancel();
    void stop();
}
