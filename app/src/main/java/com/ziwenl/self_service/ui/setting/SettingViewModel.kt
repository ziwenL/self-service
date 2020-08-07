package com.ziwenl.self_service.ui.setting

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.ziwenl.baselibrary.base.viewmodel.BaseViewModel
import com.ziwenl.baselibrary.ext.postNext
import com.ziwenl.baselibrary.utils.LibContextProvider
import com.ziwenl.baselibrary.utils.cache.CacheConst
import com.ziwenl.baselibrary.utils.cache.CacheUtil
import com.ziwenl.baselibrary.widgets.dialog.HintDialog
import com.ziwenl.baselibrary.widgets.dialog.HintOnClickCallback
import com.ziwenl.self_service.R
import com.ziwenl.self_service.bean.AttendanceDto
import com.ziwenl.self_service.service.LockScreenDeviceAdminReceiver
import com.ziwenl.self_service.service.PunchCardService
import com.ziwenl.self_service.service.SelfAccessibilityService

/**
 * PackageName : com.ziwenl.self_service.ui.setting
 * Author : Ziwen Lan
 * Date : 2020/7/10
 * Time : 17:47
 * Introduction :
 */
class SettingViewModel @ViewModelInject constructor(
    val context: FragmentActivity,
    val repo: SettingRepository
) : BaseViewModel() {

    private val _viewStateLiveData: MutableLiveData<SettingViewState> =
        MutableLiveData(SettingViewState.initial())

    val viewStateLiveData: MutableLiveData<SettingViewState> = _viewStateLiveData

    init {
        _viewStateLiveData.postNext {
            it.copy(
                isLoading = false,
                throwable = null,
                isAdminActive = repo.localDataSource.isAdminActive(LibContextProvider.appContext),
                isAccessibilityActive = repo.localDataSource.isAccessibilityActive(),
                hasPassword = repo.localDataSource.hasPassword(),
                data = repo.localDataSource.getAttendanceDtoList(),
                hasOpenRimetPermission = CacheUtil.get(CacheConst.KEY_ENABLE_RIMET, false)
            )
        }
    }

    fun removeAttendance(dto: AttendanceDto) {
        repo.localDataSource.removeAttendanceDto(dto)
        _viewStateLiveData.postNext {
            it.data.clear()
            it.data.addAll(repo.localDataSource.getAttendanceDtoList())
            it.copy()
        }
    }

    fun addAttendance(dto: AttendanceDto) {
        repo.localDataSource.addAttendanceDto(dto)
        _viewStateLiveData.postNext {
            it.data.clear()
            it.data.addAll(repo.localDataSource.getAttendanceDtoList())
            it.copy()
        }
    }

    fun clickOpenRimet() {
        SelfAccessibilityService.openRimet(context, 0L)
        _viewStateLiveData.postNext {
            it.copy(hasOpenRimetPermission = CacheUtil.get(CacheConst.KEY_ENABLE_RIMET, false))
        }
    }

    fun clickSwitchAdmin() {
        if (repo.localDataSource.isAdminActive(context)) {
            //关闭
            val devicePolicyManager =
                context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            devicePolicyManager.removeActiveAdmin(
                ComponentName(
                    context,
                    LockScreenDeviceAdminReceiver::class.java
                )
            )
        } else {
            //启用
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                ComponentName(context, LockScreenDeviceAdminReceiver::class.java)
            )
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "自动锁屏")
            context.startActivity(intent)
        }
    }

    fun clearPassword() {
        if (PunchCardService.isEmpty()) {
            CacheUtil.put(CacheConst.KEY_LOCK_SCREEN_PASSWORD, "")
            _viewStateLiveData.postNext {
                it.copy(hasPassword = false)
            }
        } else {
            HintDialog().content(context.getString(R.string.closing_will_stop_service))
                .buttonText(context.getString(R.string.cancel), context.getString(R.string.confirm))
                .addOnClickListener(object : HintOnClickCallback {
                    override fun onClickRightBtn() {
                        CacheUtil.put(CacheConst.KEY_LOCK_SCREEN_PASSWORD, "")
                        _viewStateLiveData.postNext {
                            it.copy(hasPassword = false)
                        }
                    }
                })
                .show(context.supportFragmentManager)
        }
    }

    fun updatePassword() {
        _viewStateLiveData.postNext {
            val pwd = CacheUtil.get(CacheConst.KEY_LOCK_SCREEN_PASSWORD, "")
            it.copy(hasPassword = !pwd.isEmpty())
        }
    }

    fun clickSwitchAccessibility() {
        if (repo.localDataSource.isAccessibilityActive()) {
            SelfAccessibilityService.stop()
        } else {
            context.startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }
}