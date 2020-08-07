package com.ziwenl.self_service.ui.start

import android.os.Bundle
import android.widget.Toast
import com.ziwenl.baselibrary.base.view.activity.BaseActivity
import com.ziwenl.baselibrary.widgets.dialog.HintDialog
import com.ziwenl.baselibrary.widgets.dialog.HintOnClickCallback
import com.ziwenl.self_service.R
import com.ziwenl.self_service.dialog.ConfirmStartDialog
import com.ziwenl.self_service.dialog.StartCallback
import com.ziwenl.self_service.service.PunchCardService
import com.ziwenl.self_service.ui.setting.SettingActivity

/**
 * PackageName : com.ziwenl.self_service.ui.start
 * Author : Ziwen Lan
 * Date : 2020/7/30
 * Time : 15:47
 * Introduction :
 * 一个透明的 Activity ，用来启动或停止自动考勤
 */
class StartActivity : BaseActivity() {
    override val layoutId: Int = R.layout.start_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //开启或停止
        if (PunchCardService.isEmpty()) {
            ConfirmStartDialog()
                .addCallBack(object : StartCallback {
                    override fun onSuccess() {
                        Toast.makeText(this@StartActivity, "已启动自动考勤", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    override fun onError(errorMsg: String) {
                        val hintDialog = HintDialog()
                        hintDialog.isCancelable = false
                        hintDialog
                            .content(errorMsg)
                            .buttonText(getString(R.string.confirm))
                            .addOnClickListener(object : HintOnClickCallback {
                                override fun onClickConfirmBtn() {
                                    SettingActivity.launch(this@StartActivity)
                                    finish()
                                }
                            })
                            .show(supportFragmentManager)
                    }
                })
                .show(supportFragmentManager)
        } else {
            PunchCardService.stop()
            Toast.makeText(this, "已停止自动考勤", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}