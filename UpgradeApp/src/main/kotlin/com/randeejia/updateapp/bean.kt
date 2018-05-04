package com.randeejia.lib.updateapp.bean

import android.os.Parcel
import android.os.Parcelable


/**
 * 下载信息
 *
 * @param downloadUrl apk下载地址
 * @param outputPath 本地存储路径
 * @param progress  下载进度
 * @param currentFileSize 当前文件大小
 * @param totalFileSize 文件总大小
 */
data class DownloadBean(var downloadUrl:String,var outputPath:String):Parcelable{

     var progress:Int = 0
     var currentFileSize:Long = 0
     var totalFileSize:Long = 0

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    fun getDownloadFileNameFromUrl():String{
        return downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(downloadUrl)
        parcel.writeString(outputPath)
        parcel.writeInt(progress)
        parcel.writeLong(currentFileSize)
        parcel.writeLong(totalFileSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DownloadBean> {
        override fun createFromParcel(parcel: Parcel): DownloadBean {
            return DownloadBean(parcel)
        }

        override fun newArray(size: Int): Array<DownloadBean?> {
            return arrayOfNulls(size)
        }
    }
}

/**
 * 升级信息
 *
 * @param downloadUrl apk下载地址
 * @param versionCode 最新Apk版本号
 */
data class UpdateBean(var downloadUrl:String,var versionCode:Int){

    /**
     * 从下载连接中解析出文件名
     * @return 文件名
     */
    fun getDownloadFileNameFromUrl():String{
        return downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1)
    }
}

