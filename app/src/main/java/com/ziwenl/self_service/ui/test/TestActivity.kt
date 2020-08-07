package com.ziwenl.self_service.ui.test

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.ziwenl.baselibrary.base.view.activity.BaseActivity
import com.ziwenl.baselibrary.ext.observe
import com.ziwenl.baselibrary.utils.bus.BusConst
import com.ziwenl.baselibrary.utils.bus.EventBusUtil
import com.ziwenl.baselibrary.utils.bus.EventCallBack
import com.ziwenl.baselibrary.utils.cache.CacheConst
import com.ziwenl.baselibrary.utils.cache.CacheUtil
import com.ziwenl.self_service.R
import com.ziwenl.self_service.dialog.RecordPasswordCallback
import com.ziwenl.self_service.dialog.RecordPasswordDialog
import com.ziwenl.self_service.service.SelfAccessibilityService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.test_activity.*

/**
 * PackageName : com.ziwenl.self_service.ui.test
 * Author : Ziwen Lan
 * Date : 2020/7/8
 * Time : 16:31
 * Introduction :
 */
@AndroidEntryPoint
class TestActivity : BaseActivity() {

    companion object {
        fun launch(activity: FragmentActivity) =
            activity.apply {
                startActivity(Intent(this, TestActivity::class.java))
            }
    }

    override val layoutId: Int = R.layout.test_activity

    private val mViewModel: TestViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btn_open_rimet.setOnClickListener {
            //打开钉钉
            mViewModel.openRimet()
        }
        btn_lock_screen.setOnClickListener {
            //锁屏
            mViewModel.timeLockScreen()
        }
        btn_unlock.setOnClickListener {
            //解锁
            val pwd = CacheUtil.get(CacheConst.KEY_LOCK_SCREEN_PASSWORD, "")
            if (pwd.isEmpty()) {
                val recordPasswordDialog = RecordPasswordDialog()
                recordPasswordDialog.setCallback(object : RecordPasswordCallback {
                    override fun callback() {
                        btn_unlock.performClick()
                    }
                })
                recordPasswordDialog.show(supportFragmentManager, btn_unlock)
            } else {
                if (SelfAccessibilityService.isEmpty()) {
                    btn_unlock.setTag(true)
                    startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))
                } else {
                    mViewModel.timeWakeScreen()
                }
            }
        }

        btn_punch_out.setOnClickListener {
            if (SelfAccessibilityService.isEmpty()) {
                startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else {
                SelfAccessibilityService.punchOut()
                SelfAccessibilityService.openRimet(this, 0L)
            }
        }

        EventBusUtil.observe(this, EventCallBack {
            when (it.key) {
                BusConst.KEY_DEVICE_ADMIN_IS_ENABLE -> {
                    if (it.value is Boolean && it.value as Boolean) {
                        btn_lock_screen.performClick()
                    }
                }
                BusConst.KEY_UNLOCK_ERROR -> {
                    Toast.makeText(this@TestActivity, it.value as String, Toast.LENGTH_SHORT).show()
                }
                BusConst.KEY_ACCESSIBILITY_IS_ENABLE -> {
                    if (it.value is Boolean && it.value as Boolean) {
                        if (btn_unlock.tag != null && btn_unlock.tag as Boolean) {
                            btn_unlock.tag = false
                            btn_unlock.performClick()
                        }
                    }
                }
            }
        })

        observe(mViewModel.viewStateLiveData, this::onNewState)
    }

    private fun onNewState(testViewState: TestViewState) {

        if (testViewState.downTimeLockScreen != -1L) {
            btn_lock_screen.isEnabled = false
            btn_lock_screen.text = String.format(
                getString(R.string.prepare_to_lock_screen),
                testViewState.downTimeLockScreen
            )
        } else {
            btn_lock_screen.isEnabled = true
            btn_lock_screen.text = String.format(getString(R.string.prepare_to_lock_screen), 10)
        }

        if (testViewState.downTimeUnlock != -1L) {
            btn_unlock.isEnabled = false
            btn_unlock.text =
                String.format(getString(R.string.ready_to_unlock), testViewState.downTimeUnlock)
        } else {
            btn_unlock.isEnabled = true
            btn_unlock.text = String.format(getString(R.string.ready_to_unlock), 10)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item)
    }
}