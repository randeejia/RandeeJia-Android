package cn.randeejia.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.randeejia.lib.updateapp.bean.UpdateBean
import com.randeejia.lib.updateapp.upgrade.UpgradeManager


open class KotlinMainActivity: AppCompatActivity() {

    val tag = "KotlinMainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UpgradeManager.init(this)

        var btnUpgrade = findViewById<Button>(R.id.btn_upgrade)

        btnUpgrade.setOnClickListener {

            val updateBean = UpdateBean("http://dl001.liqucn.com/upload/2014/shenghuo/cn.wecook.app_4.2.0_liqucn.com.apk", 9)
            UpgradeManager.setUpdateBean(updateBean)

            UpgradeManager.upgrade(object : UpgradeManager.UpgradeCallback {
                override fun onStart() {
                    Log.e(tag, "onStart")
                }

                override fun onUpgrade() {
                    Log.e(tag, "onUpgrade")
                }

                override fun onUpgradeProgress(progress: Int) {
                    Log.e(tag, "onUpgrade :$progress")
                }

                override fun onSuccess() {
                    Log.e(tag, "onSuccess")
                }

                override fun onFailed(errMsg: String) {
                    Log.e(tag, "onFailed:$errMsg")
                }

                override fun onEnd() {
                    Log.e(tag, "onEnd")
                }
            })
        }
    }
}