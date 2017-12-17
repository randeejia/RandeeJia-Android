package cn.randeejia.modifyuseravtar;

import java.io.File;

/**
 * Created by randeejia on 2017/2/9.
 */

public interface AppCallback {

    /**
     * 取消拍照或从相册选择照片
     */
    void cancelChoicePicture();

    void onCallback(File file);
}
