package com.randeejia.lib.updateapp.download

import android.content.Context
import android.content.Intent
import com.randeejia.lib.updateapp.bean.DownloadBean
import com.randeejia.updateapp.download.DownloadService


interface IDownload{
    fun start()
    fun start(callback: DownloadManager.DownloadCallback)
    fun stop()
}


class DownloadManager private constructor(context: Context,downloadBean:DownloadBean):IDownload{

    var mContext:Context = context
    var mDownloadBean:DownloadBean = downloadBean
    var mDownloadManager:DownloadManager =DownloadManager(context,downloadBean)

    interface DownloadCallback{
        /**
         * 下载成功
         */
        fun onSuccess()

        /**
         * 正在下载
         * @param progress 下载进度
         */
        fun onDownloading(progress: Int)

        /**
         * 下载失败
         * @param errMsg 异常信息
         */
        fun onFailed(errMsg: String)

        /**
         * 下载完成，无论成功或失败都执行该方法
         */
        fun onCompleted()
    }



    override fun start() {
        startService()
    }

    override fun start(callback:DownloadCallback) {
    }

    override fun stop() {
        stopService()
    }

    private fun startService(){
        var intent = Intent(mContext,DownloadService().javaClass)
        intent.putExtra(DownloadService.DOWNLOAD_BEAN,mDownloadBean)
        mContext.startService(intent)
    }

    private fun stopService(){
        var intent = Intent(mContext,DownloadService().javaClass)
        mContext.stopService(intent)
    }

}

