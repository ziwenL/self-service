package com.ziwenl.baselibrary.widgets.dialog

import android.view.View
import androidx.annotation.NonNull
import com.ziwenl.baselibrary.R
import kotlinx.android.synthetic.main.hint_dialog.*

/**
 * PackageName : com.ziwenl.baselibrary.widgets.dialog
 * Author : Ziwen Lan
 * Date : 2020/7/22
 * Time : 9:09
 * Introduction :
 */
class HintDialog : BaseDialogFragment() {
    override val layoutId: Int = R.layout.hint_dialog
    private var mHintOnClickCallback: HintOnClickCallback? = null

    private var mTitleText: String? = null
    private var mContextText: String = ""
    private var mBtnTextArgs: Array<out String>? = null

    fun title(title: String): HintDialog {
        mTitleText = title
        return this
    }

    fun content(content: String): HintDialog {
        mContextText = content
        return this
    }

    fun buttonText(@NonNull vararg btnTextArgs: String): HintDialog {
        mBtnTextArgs = btnTextArgs
        return this
    }

    fun addOnClickListener(hintOnClickCallback: HintOnClickCallback): HintDialog {
        mHintOnClickCallback = hintOnClickCallback
        return this
    }

    override fun initView() {
        super.initView()
        tv_title.text = mTitleText
        tv_title.visibility = if (mTitleText.isNullOrEmpty()) View.GONE else View.VISIBLE
        tv_content.text = mContextText
        if (mBtnTextArgs != null) {
            if (mBtnTextArgs!!.size > 1) {
                btn_left.text = mBtnTextArgs!![0]
                btn_right.text = mBtnTextArgs!![1]
                ll_btn_root.visibility = View.VISIBLE
                btn_confirm.visibility = View.GONE
            } else {
                btn_confirm.text = mBtnTextArgs!![0]
                ll_btn_root.visibility = View.GONE
                btn_confirm.visibility = View.VISIBLE
            }
        }
        val onClickListener: View.OnClickListener = View.OnClickListener {
            when (it.id) {
                R.id.btn_left -> {
                    dismiss()
                    mHintOnClickCallback?.onClickLeftBtn()
                }
                R.id.btn_right -> {
                    dismiss()
                    mHintOnClickCallback?.onClickRightBtn()
                }
                R.id.btn_confirm -> {
                    dismiss()
                    mHintOnClickCallback?.onClickConfirmBtn()
                }
            }
        }
        btn_left.setOnClickListener(onClickListener)
        btn_right.setOnClickListener(onClickListener)
        btn_confirm.setOnClickListener(onClickListener)
    }
}

interface HintOnClickCallback {
    fun onClickLeftBtn() {}
    fun onClickRightBtn() {}
    fun onClickConfirmBtn() {}
}