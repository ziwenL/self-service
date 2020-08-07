package com.ziwenl.self_service.dialog

import android.app.TimePickerDialog
import android.widget.Toast
import com.ziwenl.baselibrary.utils.cache.CacheConst
import com.ziwenl.baselibrary.utils.cache.CacheUtil
import com.ziwenl.baselibrary.widgets.dialog.BaseDialogFragment
import com.ziwenl.self_service.R
import com.ziwenl.self_service.bean.AttendanceListDto
import kotlinx.android.synthetic.main.setting_add_attendance_dialog.*

/**
 * PackageName : com.ziwenl.self_service.dialog
 * Author : Ziwen Lan
 * Date : 2020/7/20
 * Time : 14:06
 * Introduction :
 */
class AddAttendanceDialog : BaseDialogFragment() {
    override val layoutId: Int = com.ziwenl.self_service.R.layout.setting_add_attendance_dialog

    private var mTimePickerDialog: TimePickerDialog? = null

    private var mCallback: AddAttendanceDialogCallback? = null

    override fun initView() {
        super.initView()
        tv_time.setOnClickListener {
            if (mTimePickerDialog == null) {
                mTimePickerDialog =
                    TimePickerDialog(
                        context,
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            tv_time.setText(String.format("%02d:%02d", hourOfDay, minute))
                        },
                        9,
                        0,
                        true
                    )
            }
            mTimePickerDialog?.show()
        }
        btn_confirm.setOnClickListener {
            val attendanceListDto: AttendanceListDto? =
                CacheUtil.get(CacheConst.KEY_ATTENDANCE_DATA, AttendanceListDto::class.java)
            if (attendanceListDto?.data != null) {
                for (dto in attendanceListDto.data) {
                    if (tv_time.text.toString().equals(dto.time)) {
                        Toast.makeText(
                            context,
                            R.string.the_clock_time_already_exists,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }
            }
            mCallback?.callback(rb_punch_in.isChecked, tv_time.text.toString())
            dismiss()
        }
    }

    fun setCallback(callback: AddAttendanceDialogCallback) {
        mCallback = callback
    }
}

interface AddAttendanceDialogCallback {
    fun callback(isPunchIn: Boolean, time: String)
}