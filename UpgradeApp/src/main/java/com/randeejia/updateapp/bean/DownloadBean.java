package com.randeejia.updateapp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 下载信息
 */

public class DownloadBean implements Parcelable {

    private String downloadUrl;//网络下载地址
    private String outputPath;//文件输出路径

    private int progress;
    private long currentFileSize;
    private long totalFileSize;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    /**
     * 从下载连接中解析出文件名
     * @return 文件名
     */
    public String getNameFromUrl() {
        return downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
    }

    public String getDownloadUrl(){
        return downloadUrl;
    }

    public String getOutputPath(){
        return outputPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.progress);
        dest.writeLong(this.currentFileSize);
        dest.writeLong(this.totalFileSize);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.outputPath);
    }

    public DownloadBean(String downloadUrl,String outputPath) {
        this.downloadUrl = downloadUrl;
        this.outputPath = outputPath;
    }

    protected DownloadBean(Parcel in) {
        this.progress = in.readInt();
        this.currentFileSize = in.readLong();
        this.totalFileSize = in.readLong();
        this.downloadUrl = in.readString();
        this.outputPath = in.readString();
    }

    public static final Creator<DownloadBean> CREATOR = new Creator<DownloadBean>() {
        @Override
        public DownloadBean createFromParcel(Parcel source) {
            return new DownloadBean(source);
        }

        @Override
        public DownloadBean[] newArray(int size) {
            return new DownloadBean[size];
        }
    };
}
