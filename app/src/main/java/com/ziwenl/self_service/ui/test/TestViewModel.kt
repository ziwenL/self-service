package com.ziwenl.self_service.ui.test

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ziwenl.baselibrary.base.viewmodel.BaseViewModel
import com.ziwenl.baselibrary.ext.postNext
import com.ziwenl.self_service.service.LockScreenDeviceAdminReceiver
import com.ziwenl.self_service.service.SelfAccessibilityService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PackageName : com.ziwenl.self_service.ui.test
 * Author : Ziwen Lan
 * Date : 2020/7/9
 * Time : 15:01
 * Introduction :
 */
class TestViewModel @ViewModelInject constructor( val context: FragmentActivity) : BaseViewModel() {
    private val _viewStateLiveData: MutableLiveData<TestViewState> =
        MutableLiveData(TestViewState.initial())

    val viewStateLiveData: LiveData<TestViewState> = _viewStateLiveData

    private var mComponentName: ComponentName? = null

    /**
     * 通过指定包名打开APP
     * 钉钉包名 com.alibaba.android.rimet
     */
    fun openRimet() {
        SelfAccessibilityService.openRimet(context, 0L)
    }


    /**
     * 定时锁屏
     */
    fun timeLockScreen() {
        viewModelScope.launch {
            if (mComponentName == null)
                mComponentName = ComponentName(context, LockScreenDeviceAdminReceiver::class.java)
            //判断是否已激活设备管理器
            val devicePolicyManager =
                context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            if (devicePolicyManager.isAdminActive(mComponentName!!)) {
                //锁屏
                for (i in 9 downTo 1) {
                    _viewStateLiveData.postNext {
                        it.copy(false, null, i.toLong(), it.downTimeUnlock)
                    }
                    delay(1000)
                }
                _viewStateLiveData.postNext {
                    it.copy(false, null, -1, it.downTimeUnlock)
                }
                devicePolicyManager.lockNow()
            } else {
                //激活设备管理器
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "自动锁屏")
                context.startActivity(intent)
            }
        }
    }

    /**
     * 定时唤醒屏幕
     */
    fun timeWakeScreen() {
        viewModelScope.launch {
            for (i in 9 downTo 1) {
                _viewStateLiveData.postNext {
                    it.copy(false, null, it.downTimeLockScreen, i.toLong())
                }
                delay(1000)
            }
            _viewStateLiveData.postNext {
                it.copy(false, null, it.downTimeLockScreen, -1)
            }
            //唤醒屏幕
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
                TestActivity::class.java.simpleName
            )
            wl.acquire(60 * 1000L /*1 minutes*/)
            wl.release()
            //解锁
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SelfAccessibilityService.unlock(null)
            }
        }
    }
}