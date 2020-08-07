package com.ziwenl.self_service.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.ziwenl.baselibrary.base.view.activity.BaseActivity
import com.ziwenl.baselibrary.ext.observe
import com.ziwenl.baselibrary.utils.bus.BusConst
import com.ziwenl.baselibrary.utils.bus.EventBusUtil
import com.ziwenl.baselibrary.utils.bus.EventCallBack
import com.ziwenl.baselibrary.utils.cache.CacheConst
import com.ziwenl.baselibrary.utils.cache.CacheUtil
import com.ziwenl.baselibrary.widgets.dialog.HintDialog
import com.ziwenl.self_service.R
import com.ziwenl.self_service.dialog.ConfirmStartDialog
import com.ziwenl.self_service.dialog.StartCallback
import com.ziwenl.self_service.service.PunchCardService
import com.ziwenl.self_service.ui.setting.SettingActivity
import com.ziwenl.self_service.ui.start.StartActivity
import com.ziwenl.self_service.ui.test.TestActivity
import com.ziwenl.self_service.utils.ShortcutUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_activity.*

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val layoutId: Int = R.layout.main_activity

    private val mViewHolder: MainViewModel by viewModels()

    companion object {
        fun launch(activity: FragmentActivity) =
            activity.apply {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //添加快捷方式
        ShortcutUtil.createShortcut(
            this,
            R.drawable.ic_yingyongshangdian,
            "今日开启/停止服务",
            "今日开启/停止服务",
            "开启服务",
            StartActivity::class.java
        )

        //获取屏幕宽高存起来有用
        val dm = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(dm)
        CacheUtil.put(CacheConst.KEY_SCREEN_WIDTH, dm.widthPixels)
        CacheUtil.put(CacheConst.KEY_SCREEN_HEIGHT, dm.heightPixels)

        btn_test.setOnClickListener {
            TestActivity.launch(this)
        }
        btn_setting.setOnClickListener {
            SettingActivity.launch(this)
        }
        btn_start.setOnClickListener {
            if (PunchCardService.isEmpty()) {
                ConfirmStartDialog()
                    .addCallBack(object : StartCallback {
                        override fun onSuccess() {
                            btn_start.text = getString(R.string.stop_life_support_servie)
                        }
                        override fun onError(errorMsg: String) {
                            HintDialog()
                                .content(errorMsg)
                                .buttonText(getString(R.string.confirm))
                                .show(supportFragmentManager)
                        }
                    })
                    .show(supportFragmentManager)
            } else {
                btn_start.text = getString(R.string.start_life_support_service)
                PunchCardService.stop()
            }
        }

        EventBusUtil.observe(this, EventCallBack {
            when (it.key) {
                BusConst.KEY_PUNCH_CARD_SERVICE_ACTIVE -> {
                    val active = it.value as Boolean
                    if (active) {
                        btn_start.text = getString(R.string.stop_life_support_servie)
                    } else {
                        btn_start.text = getString(R.string.start_life_support_service)
                    }
                }
            }
        })

        observe(mViewHolder.stateLiveData, this::onNewState)
    }

    private fun onNewState(viewState: MainViewState) {
        btn_start.text =
            if (viewState.isStart) getString(R.string.stop_life_support_servie) else getString(R.string.start_life_support_service)
    }
}