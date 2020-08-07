package com.ziwenl.self_service.dialog

import android.widget.Toast
import com.ziwenl.baselibrary.utils.cache.CacheConst
import com.ziwenl.baselibrary.utils.cache.CacheUtil
import com.ziwenl.baselibrary.widgets.dialog.BaseDialogFragment
import com.ziwenl.self_service.R
import kotlinx.android.synthetic.main.test_record_password_dialog.*

/**
 * PackageName : com.ziwenl.self_service.ui.test
 * Author : Ziwen Lan
 * Date : 2020/7/16
 * Time : 13:50
 * Introduction :记录密码
 */
class RecordPasswordDialog : BaseDialogFragment() {

    override val layoutId: Int = R.layout.test_record_password_dialog

    private var mCallback: RecordPasswordCallback? = null

    override fun initView() {
        super.initView()

        btn_confirm.setOnClickListener {
            val pwd = tv_pwd.text
            if (pwd.isNullOrEmpty()) {
                Toast.makeText(context, getString(R.string.please_input_password), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CacheUtil.put(CacheConst.KEY_LOCK_SCREEN_PASSWORD, pwd.toString())
            dismiss()
            mCallback?.callback()
        }

    }

    fun setCallback(callback: RecordPasswordCallback) {
        mCallback = callback
    }
}

interface RecordPasswordCallback {
    fun callback()
}