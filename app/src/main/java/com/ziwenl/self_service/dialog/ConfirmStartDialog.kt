package com.ziwenl.self_service.dialog

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import com.ziwenl.baselibrary.utils.cache.CacheConst
import com.ziwenl.baselibrary.utils.cache.CacheUtil
import com.ziwenl.baselibrary.widgets.dialog.BaseDialogFragment
import com.ziwenl.self_service.R
import com.ziwenl.self_service.bean.AttendanceListDto
import com.ziwenl.self_service.service.LockScreenDeviceAdminReceiver
import com.ziwenl.self_service.service.PunchCardService
import com.ziwenl.self_service.service.SelfAccessibilityService
import kotlinx.android.synthetic.main.start_confirm_start_dialog.*
import kotlinx.coroutines.*

/**
 * PackageName : com.ziwenl.self_service.dialog
 * Author : Ziwen Lan
 * Date : 2020/7/22
 * Time : 11:09
 * Introduction :
 */
class ConfirmStartDialog : BaseDialogFragment() {

    override val layoutId: Int = com.ziwenl.self_service.R.layout.start_confirm_start_dialog
    private var mJob: Job? = null
    private var mStartCallback: StartCallback? = null

    override fun initView() {
        super.initView()
        isCancelable = false
        mJob = GlobalScope.launch(Dispatchers.Main) {
            addText(getText(R.string.start_steps_check))
            delay(800)
            if (CacheUtil.get(CacheConst.KEY_ENABLE_RIMET, false)) {
                addText(getText(R.string.start_steps_has_open_rimet_permission))
                delay(800)
                val devicePolicyManager =
                    ll_main.context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                if (devicePolicyManager.isAdminActive(
                        ComponentName(
                            ll_main.context,
                            LockScreenDeviceAdminReceiver::class.java
                        )
                    )
                ) {
                    addText(getText(R.string.start_steps_has_lock_screen_permission))
                    delay(800)
                    if (!CacheUtil.get(CacheConst.KEY_LOCK_SCREEN_PASSWORD, "").isEmpty()) {
                        removeText()
                        addText(getText(R.string.start_steps_has_lock_screen_password))
                        delay(800)
                        val dataDto = CacheUtil.get(
                            CacheConst.KEY_ATTENDANCE_DATA,
                            AttendanceListDto::class.java
                        )
                        if (dataDto != null) {
                            removeText()
                            addText(getText(R.string.start_steps_has_attendance_time))
                            delay(800)
                            if (!SelfAccessibilityService.isEmpty()) {
                                removeText()
                                addText(getText(R.string.start_steps_attendance_service_activated))
                                delay(800)
                                removeText()
                                addText(getText(R.string.start_steps_start_keep_alive_service))
                                PunchCardService.launch(requireContext())
                                delay(800)
                                removeText()
                                removeText()
                                removeText()
                                progress_bar.visibility = View.GONE
                                addText(getText(R.string.start_steps_successfully))
                                delay(800)
                                dismiss()
                                mStartCallback?.onSuccess()
                            } else {
                                addText(getText(R.string.self_service_is_not_activated))
                                delay(800)
                                dismiss()
                                mStartCallback?.onError(getString(R.string.tips_start_accessibility_service))
                            }
                        } else {
                            addText(getString(R.string.No_attendance_time_is_set))
                            delay(800)
                            dismiss()
                            mStartCallback?.onError(getString(R.string.tips_set_attendance_time))
                        }

                    } else {
                        addText(getString(R.string.no_password_is_set))
                        delay(800)
                        dismiss()
                        mStartCallback?.onError(getString(R.string.tips_set_password))
                    }
                } else {
                    addText(getString(R.string.no_lock_screen_permission))
                    delay(800)
                    dismiss()
                    mStartCallback?.onError(getString(R.string.tips_get_lock_screen_permission))
                }
            } else {
                addText(getString(R.string.no_open_rimet_permission))
                delay(800)
                dismiss()
                mStartCallback?.onError(getString(R.string.tips_get_open_rimet_permission))
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mJob?.cancel()
    }

    fun addText(text: CharSequence) {
        val textView = TextView(ll_main.context)
        textView.text = text
        textView.setPadding(5, 5, 5, 5)
        ll_main.addView(textView)
    }

    fun removeText() {
        if (ll_main.childCount != 0) {
            ll_main.removeViewAt(0)
        }
    }

    fun addCallBack(callback: StartCallback): ConfirmStartDialog {
        mStartCallback = callback
        return this
    }

}

interface StartCallback {
    fun onSuccess()

    fun onError(errorMsg: String)
}