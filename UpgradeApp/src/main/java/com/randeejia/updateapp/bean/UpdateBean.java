package com.randeejia.updateapp.bean;

/**
 * 升级信息
 */
public class UpdateBean {

    //Apk 下载地址
    public String updateUrl;

    public int versionCode;

    public UpdateBean(String updateUrl, int versionCode) {
        this.updateUrl = updateUrl;
        this.versionCode = versionCode;
    }

    /**
     * 从下载连接中解析出文件名
     * @return 文件名
     */
    public String getNameFromUrl() {
        return updateUrl.substring(updateUrl.lastIndexOf("/") + 1);
    }
}
