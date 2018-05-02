package com.randeejia.updateapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.randeejia.updateapp.bean.DownloadBean;
import com.randeejia.updateapp.bean.UpdateBean;
import com.randeejia.updateapp.download.DownloadManager;
import com.randeejia.updateapp.download.IDownload;

import java.io.File;

/**
 * Created by randeejia on 2017/1/14.
 */

public class UpgradeManager {

    private static final String TAG = "UpgradeManager";

    private File mOutputFile;
    private Context mContext;

    private static UpgradeManager mUpgradeManager;
    private UpdateBean mUpdateBean;
    private UpgradeCallback mUpgradeCallback;
    private IDownload download;

    /**
     * 升级回调接口
     */
    public interface UpgradeCallback{

        /**
         * 升级开始
         */
        void onStart();
        /**
         * 升级中
         */
        void onUpgrade();
        /**
         * 升级中
         * @param progress 百分比
         */
        void onUpgradeProgress(int progress);
        /**
         * 升级成功
         */
        void onSuccess();
        /**
         * 升级失败
         * @param errMsg 异常信息
         */
        void onFailed(String errMsg);
        /**
         * 升级结束
         */
        void onEnd();

    }

    private UpgradeManager(Context context) {
        this.mContext = context;
    }

    public static void init(Context context){
        if (mUpgradeManager ==null){
            mUpgradeManager = new UpgradeManager(context);
        }
    }

    public void setUpdateBean(UpdateBean updateBean){
        this.mUpdateBean = updateBean;

        mOutputFile = getDownloadApk(updateBean.getNameFromUrl());

        DownloadBean downloadBean = new DownloadBean(mUpdateBean.updateUrl,mOutputFile.getAbsolutePath());

        download = DownloadManager.getInstance(mContext,downloadBean);
    }

    public static UpgradeManager getInstance(){
        if (mUpgradeManager == null){
            throw new RuntimeException("Please Call UpgradeManager.init() method, initialise lib.");
        }
        return mUpgradeManager;
    }


    public boolean isCanUpgrade(){
        if (mUpdateBean !=null && mUpdateBean.versionCode > BuildConfig.VERSION_CODE){
            return true;
        }
        return false;
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
    public void upgrade(UpgradeCallback callback) {
        this.mUpgradeCallback = callback;

        if (mUpgradeCallback !=null){
            mUpgradeCallback.onStart();
        }

        if (isCanUpgrade()){
            if (!mOutputFile.exists()){//如果文件不存在，则开始下载Apk
                download.start(new DownloadManager.DownloadCallback() {

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onDownloading(int progress) {
                        if (mUpgradeCallback !=null){
                            mUpgradeCallback.onUpgradeProgress(progress);
                        }
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        if (mUpgradeCallback !=null){
                            mUpgradeCallback.onFailed(errMsg);
                            mUpgradeCallback.onEnd();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (mUpgradeCallback !=null){
                            mUpgradeCallback.onUpgrade();
                            mUpgradeCallback.onEnd();
                        }
                    }
                });
            }else{
                if (mUpgradeCallback !=null){
                    mUpgradeCallback.onUpgrade();
                    mUpgradeCallback.onEnd();
                }
            }
        }
    }

    /**
     * 跳转到系统安装页面
     */
    public void gotoInstallPage() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(mOutputFile),
                "application/vnd.android.package-archive");
        // 如果用户取消安装的话, 会返回结果,回调方法onActivityResult
        mContext.startActivity(intent);
    }

    /**
     * 获取Apk下载存放目录
     * @param outputFileName
     * @return
     */
    private File getDownloadApk(String outputFileName){
        return new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), outputFileName);
    }
}
