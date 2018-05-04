package com.randeejia.lib.updateapp.upgrade

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.randeejia.lib.updateapp.bean.DownloadBean
import com.randeejia.lib.updateapp.download.DownloadManager
import com.randeejia.lib.updateapp.download.IDownload
import com.randeejia.updateapp.BuildConfig
import com.randeejia.updateapp.bean.UpdateBean
import java.io.File


interface IUpgrade {

    fun init(context: Context)

    /**
     * 判断是否能升级
     */
    fun isCanUpgrade():Boolean

    /**
     * 开始下载
     * @param callback 回调方法
     */
    fun upgrade(callback: UpgradeManager.UpgradeCallback)
}


object UpgradeManager :IUpgrade{

    val TAG = "UpgradeManager"

    lateinit var mOutputFile: File
    lateinit var mContext: Context

    lateinit var mUpdateBean: UpdateBean
    lateinit var mUpgradeCallback: UpgradeCallback
    lateinit var download: IDownload

    /**
     * 升级回调接口
     */
    interface UpgradeCallback {

        /**
         * 升级开始
         */
        fun onStart()

        /**
         * 升级中
         */
        fun onUpgrade()

        /**
         * 升级中
         * @param progress 百分比
         */
        fun onUpgradeProgress(progress: Int)

        /**
         * 升级成功
         */
        fun onSuccess()

        /**
         * 升级失败
         * @param errMsg 异常信息
         */
        fun onFailed(errMsg: String)

        /**
         * 升级结束
         */
        fun onEnd()

    }

    override fun init(context: Context) {
        mContext = context
    }

    fun setUpdateBean(updateBean: UpdateBean) {
        this.mUpdateBean = updateBean

        mOutputFile = getDownloadApk(updateBean.getNameFromUrl())

        var downloadBean = DownloadBean(mUpdateBean.updateUrl, mOutputFile.absolutePath)

        DownloadManager.mContext = mContext
        DownloadManager.mDownloadBean = downloadBean

        download = DownloadManager
    }

    override fun isCanUpgrade():Boolean{
        if (mUpdateBean.versionCode > BuildConfig.VERSION_CODE) {
            return true
        }
        return false
    }

    /**
     * 升级新版本
     *
     * 第一步：判断是否能升级
     *
     * 第二步：如果能升级，判断app是否存在，如果存在则提示用户安装，如果不存在则下载App再提示用户安装
     *
     * @param callback 升级回调
     */
    override fun upgrade(callback:UpgradeCallback){

        if (mUpdateBean == null) {
            throw RuntimeException ("Please Call " +
                    "UpgradeManager.setUpdateBean(UpdateBean updateBean) " +
                    "method and set UpdateBean Object")
        }

        if (isCanUpgrade()) {

            if (mUpgradeCallback != null) {
                mUpgradeCallback.onStart()
            }

            this.mUpgradeCallback = callback

            if (!mOutputFile.exists()) {//如果文件不存在，则开始下载Apk
                download.start(object : DownloadManager.DownloadCallback {

                    override fun onSuccess() {

                    }

                    override fun onDownloading(progress:Int) {
                        mUpgradeCallback!!.onUpgradeProgress(progress)
                    }

                    override fun onFailed(errMsg:String) {
                            mUpgradeCallback!!.onFailed(errMsg)
                            mUpgradeCallback!!.onEnd()

                    }

                    override fun onCompleted() {
                        if (mUpgradeCallback != null) {
                            mUpgradeCallback!!.onUpgrade()
                            mUpgradeCallback!!.onEnd()
                        }
                    }
                })
            } else {
                mUpgradeCallback!!.onUpgrade()
                mUpgradeCallback!!.onEnd()
            }
        }
    }

    /**
     * 跳转到系统安装页面
     */
    fun gotoInstallPage() {
        var intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setDataAndType(Uri.fromFile(mOutputFile),
                "application/vnd.android.package-archive")
        // 如果用户取消安装的话, 会返回结果,回调方法onActivityResult
        mContext.startActivity(intent)
    }

    /**
     * 获取Apk下载存放目录
     * @param outputFileName
     * @return
     */
    fun getDownloadApk(outputFileName: String): File {
        return File(Environment.getExternalStoragePublicDirectory
        (Environment.DIRECTORY_DOWNLOADS), outputFileName)
    }

}