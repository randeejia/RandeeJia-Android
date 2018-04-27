package com.randeejia.updateapp.bean;

public class UpdateBean {

    //Apk 下载地址
    public String updateUrl;

    public int versionCode;

    /**
     * 从下载连接中解析出文件名
     * @return 文件名
     */
    public String getNameFromUrl() {
        return updateUrl.substring(updateUrl.lastIndexOf("/") + 1);
    }
}
