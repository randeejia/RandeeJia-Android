package com.randeejia.lib.updateapp.download

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.randeejia.lib.updateapp.bean.DownloadBean
import okhttp3.*
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


interface IDownload{
    /**
     * 开始下载
     */
    fun start()
    /**
     * 开始下载
     * @param callback 回调方法
     */
    fun start(callback: DownloadManager.DownloadCallback)
    /**
     * 停止下载
     */
    fun stop()
}


object DownloadManager:IDownload{

    lateinit var mContext:Context
    lateinit var mDownloadBean:DownloadBean
    var mDownloadService = DownloadService()

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
       DownloadService.setDownloadCallback(callback)
        startService()
    }

    override fun stop() {
        stopService()
    }

    private fun startService(){
        var intent = Intent(mContext,mDownloadService.javaClass)
        intent.putExtra(DownloadService.DOWNLOAD_BEAN,mDownloadBean)
        mContext.startService(intent)
    }

    private fun stopService(){
        var intent = Intent(mContext,mDownloadService.javaClass)
        mContext.stopService(intent)
    }

}

class DownloadService :IntentService("DownloadService"){

//    var mConnectMgr: ConnectivityManager? = null
    var newCall: Call? = null


    override fun onDestroy() {
        super.onDestroy()
        cancelDownload()
//        unregisterReceiver(connectionReceiver)
    }

    override fun onHandleIntent(intent: Intent?) {
        var downloadBean:DownloadBean = intent!!.getParcelableExtra(DOWNLOAD_BEAN)
        if (downloadBean !=null){
            download(downloadBean)
        }else{
            throw RuntimeException("DownloadBean can't be null")
        }
    }

    private fun download(downloadBean:DownloadBean){
        var request = Request.Builder().url(downloadBean.downloadUrl).build()
        var okHttpClient = OkHttpClient()
        newCall = okHttpClient.newCall(request)
        newCall!!.enqueue(object :Callback{
            override fun onFailure(call:Call ,e: IOException ) {

                Log.e(TAG,e.localizedMessage)
//                FileUtils.deleteFile(outFile)

                    sCallback!!.onFailed(e.localizedMessage)// 下载失败
                    sCallback!!.onCompleted()

                //下载失败停止服务
                stopSelf()
            }

            override fun onResponse(call:Call,response: Response) {
                Log.e(TAG,"onResponse success")
                var inputStream : InputStream? = null
                var  buf = ByteArray(2048)

                var fos: FileOutputStream? = null
                try {
                    inputStream = response.body().byteStream()

                    downloadBean.totalFileSize = response.body().contentLength()

                    fos = FileOutputStream(downloadBean.outputPath)

                    var sum:Long = 0

                    var len = 0

                    while (len != -1) {

                        len = inputStream.read(buf)

                        fos.write(buf, 0, len)

                        sum += len

                        downloadBean.currentFileSize = sum

                        downloadBean.progress =  (sum * 1.0f /  downloadBean.totalFileSize * 100).toInt()

                        sCallback!!.onDownloading( downloadBean.progress)//下载中
                    }
                    fos.flush()

                    sCallback!!.onSuccess()// 下载完成


                } catch (e:Exception) {
//                    FileUtils.deleteFile(outFile);
                    sCallback!!.onFailed(e.localizedMessage) //下载异常


                } finally {
                    try {
                        if (inputStream != null) inputStream.close()
                    } catch (e:IOException) {
                    }
                    try {
                        if (fos != null) fos.close()
                    } catch (e:IOException) {
                    }
                }

                sCallback!!.onCompleted()//下载完成

                //下载完成停止服务
                stopSelf()
            }
        })
    }

    private fun cancelDownload(){
        if (newCall!!.isExecuted){
            newCall!!.isCanceled
        }
    }

    companion object {

        val TAG = "DownloadService"

        val DOWNLOAD_BEAN = "download_bean"

        var sCallback:DownloadManager.DownloadCallback? = null

        fun setDownloadCallback(callback:DownloadManager.DownloadCallback ){
            sCallback = callback
        }
    }
}

