package com.ziwenl.self_service.ui.setting

//import com.ziwenl.self_service.dialog.AddAttendanceDialogCallback
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ziwenl.baselibrary.base.view.activity.BaseActivity
import com.ziwenl.baselibrary.ext.observe
import com.ziwenl.baselibrary.ext.postNext
import com.ziwenl.baselibrary.utils.bus.BusConst
import com.ziwenl.baselibrary.utils.bus.EventBusUtil
import com.ziwenl.baselibrary.utils.bus.EventCallBack
import com.ziwenl.baselibrary.widgets.dialog.HintDialog
import com.ziwenl.baselibrary.widgets.dialog.HintOnClickCallback
import com.ziwenl.self_service.R
import com.ziwenl.self_service.bean.AttendanceDto
import com.ziwenl.self_service.dialog.AddAttendanceDialog
import com.ziwenl.self_service.dialog.AddAttendanceDialogCallback
import com.ziwenl.self_service.dialog.RecordPasswordCallback
import com.ziwenl.self_service.dialog.RecordPasswordDialog
import com.ziwenl.self_service.service.PunchCardService
import com.ziwenl.self_service.ui.setting.adapter.AttendanceTimeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.setting_activity.*

/**
 * PackageName : com.ziwenl.self_service.ui.setting
 * Author : Ziwen Lan
 * Date : 2020/7/9
 * Time : 17:46
 * Introduction :
 */
@AndroidEntryPoint
class SettingActivity : BaseActivity() {

    companion object {
        fun launch(activity: FragmentActivity) =
            activity.apply {
                startActivity(Intent(this, SettingActivity::class.java))
            }
    }

    override val layoutId: Int = R.layout.setting_activity

    private val mViewModel: SettingViewModel by viewModels()

    private var mAttendanceAdapter: AttendanceTimeAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_details_settings.setOnClickListener {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
            )
        }
        btn_open_rimet.setOnClickListener {
            mViewModel.clickOpenRimet()
        }
        switch_device_admin.setOnClickListener {
            val isShowTips = (!PunchCardService.isEmpty()) && !switch_device_admin.isChecked
            if (isShowTips) {
                HintDialog().content(getString(R.string.closing_will_stop_service))
                    .buttonText(getString(R.string.cancel), getString(R.string.confirm))
                    .addOnClickListener(object : HintOnClickCallback {
                        override fun onClickRightBtn() {
                            PunchCardService.stop()
                            mViewModel.clickSwitchAdmin()
                        }
                    })
                    .show(supportFragmentManager)
            } else {
                mViewModel.clickSwitchAdmin()
            }
            switch_device_admin.isChecked = isShowTips
        }
        switch_accessibility.setOnClickListener {
            val isShowTips = (!PunchCardService.isEmpty()) && !switch_accessibility.isChecked
            if (isShowTips) {
                HintDialog().content(getString(R.string.closing_will_stop_service))
                    .buttonText(getString(R.string.cancel), getString(R.string.confirm))
                    .addOnClickListener(object : HintOnClickCallback {
                        override fun onClickRightBtn() {
                            PunchCardService.stop()
                            mViewModel.clickSwitchAccessibility()
                        }
                    })
                    .show(supportFragmentManager)
            } else {
                mViewModel.clickSwitchAccessibility()
            }
            switch_accessibility.isChecked = isShowTips
        }
        btn_set_pwd.setOnClickListener {
            val mRecordPasswordDialog = RecordPasswordDialog()
            mRecordPasswordDialog.setCallback(object : RecordPasswordCallback {
                override fun callback() {
                    mViewModel.updatePassword()
                }
            })
            mRecordPasswordDialog.show(supportFragmentManager, btn_set_pwd)
        }
        btn_clear_pwd.setOnClickListener {
            mViewModel.clearPassword()
        }
        btn_add_attendance.setOnClickListener {
            val addAttendanceDialog = AddAttendanceDialog()
            addAttendanceDialog.setCallback(object : AddAttendanceDialogCallback {
                override fun callback(isPunchIn: Boolean, time: String) {
                    mViewModel.addAttendance(AttendanceDto(isPunchIn, time, false))
                }
            })
            addAttendanceDialog.show(supportFragmentManager, btn_add_attendance)
        }

        EventBusUtil.observe(this, EventCallBack {
            when (it.key) {
                BusConst.KEY_DEVICE_ADMIN_IS_ENABLE -> {
                    mViewModel.viewStateLiveData.postNext { viewState ->
                        viewState.copy(isAdminActive = it.value as Boolean)
                    }
                }
                BusConst.KEY_ACCESSIBILITY_IS_ENABLE -> {
                    mViewModel.viewStateLiveData.postNext { viewState ->
                        viewState.copy(isAccessibilityActive = it.value as Boolean)
                    }
                }
            }
        })

        observe(mViewModel.viewStateLiveData, this::onNewState)
    }

    private fun onNewState(viewState: SettingViewState) {
        tv_open_rimet_permission.text =
            getString(
                R.string.check_has_open_rimet_permission,
                if (viewState.hasOpenRimetPermission) "是" else "否"
            )
        btn_open_rimet.visibility =
            if (viewState.hasOpenRimetPermission) View.GONE else View.VISIBLE
        switch_device_admin.isChecked = viewState.isAdminActive
        switch_accessibility.isChecked = viewState.isAccessibilityActive
        tv_pwd_tips.text = getString(
            R.string.lock_screen_password_has_stored,
            if (viewState.hasPassword) "是" else "否"
        )
        if (rv_attendance.adapter == null) {
            mAttendanceAdapter = AttendanceTimeAdapter(viewState.data, View.OnLongClickListener {
                if (!PunchCardService.isEmpty()) {
                    HintDialog()
                        .content(getString(R.string.life_support_service_is_running))
                        .buttonText(getString(R.string.confirm))
                        .show(supportFragmentManager)
                } else {
                    HintDialog()
                        .content(getString(R.string.delete_this_attendance_time))
                        .buttonText(getString(R.string.cancel), getString(R.string.confirm))
                        .addOnClickListener(object : HintOnClickCallback {
                            override fun onClickRightBtn() {
                                super.onClickRightBtn()
                                val position = it.getTag() as Int
                                mViewModel.removeAttendance(viewState.data[position])
                            }
                        })
                        .show(supportFragmentManager)
                }
                false
            })
            rv_attendance.layoutManager = LinearLayoutManager(this)
            rv_attendance.adapter = mAttendanceAdapter
        } else {
            mAttendanceAdapter?.data?.clear()
            mAttendanceAdapter?.data?.addAll(viewState.data)
            mAttendanceAdapter?.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item)
    }
}