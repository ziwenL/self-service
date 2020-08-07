package com.ziwenl.baselibrary.widgets.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ziwenl.baselibrary.R
import timber.log.Timber.d
import java.lang.ref.WeakReference

/**
 * PackageName : com.ziwenl.baselibrary.base.view.dialog
 * Author : Ziwen Lan
 * Date : 2020/7/16
 * Time : 13:46
 * Introduction :
 */
abstract class BaseDialogFragment : DialogFragment() {

    abstract val layoutId: Int

    /**
     * 防止多次点击打开多个窗口
     */
    private var mClickViewWeakReference: WeakReference<View?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDialogStyle()
        if (savedInstanceState != null) {
            getArgumentsData(savedInstanceState)
        } else if (this.arguments != null) {
            getArgumentsData(this.arguments)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mContentView = inflater.inflate(layoutId, null, false)
        setDialogLocation()
        if (dialog != null) {
            dialog!!.setOnKeyListener { dialog: DialogInterface?, keyCode: Int, event: KeyEvent? -> keyCode == KeyEvent.KEYCODE_BACK }
        }
        return mContentView
    }


    override fun onStart() {
        super.onStart()
        initView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (this.arguments != null) {
            outState.putAll(this.arguments)
        }
    }


    override fun onDestroyView() {
        if (mClickViewWeakReference != null
            && mClickViewWeakReference!!.get() != null
        ) {
            mClickViewWeakReference!!.get()!!.isEnabled = true
        }
        super.onDestroyView()
    }

    open fun getArgumentsData(arguments: Bundle?) {}

    open fun setDialogStyle() {
        setStyle(STYLE_NO_TITLE, R.style.dialogCommon)
    }

    open fun setDialogLocation() {}

    open fun initView() {}


    open fun show(
        manager: FragmentManager,
        clickView: View?
    ) {
        if (manager.isDestroyed) {
            d("manager.isDestroyed()")
            return
        }
        if (this.isAdded) {
            d("already isAdded")
            return
        }
        if (clickView != null) {
            mClickViewWeakReference = WeakReference(clickView)
            clickView.isEnabled = false
        }
        super.show(manager, this.javaClass.simpleName)
    }

    open fun show(
        manager: FragmentManager
    ) {
        if (manager.isDestroyed) {
            d("manager.isDestroyed()")
            return
        }
        if (this.isAdded) {
            d("already isAdded")
            return
        }
        super.show(manager, this.javaClass.simpleName)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return WeakDialog(
            requireContext(),
            theme
        )
    }

    private class WeakDialog(context: Context, @StyleRes themeResId: Int) :
        Dialog(context, themeResId) {

        private var wrappedCancelListener: WrappedCancelListener? = null
        private var wrappedDismissListener: WrappedDismissListener? = null
        private var wrappedShowListener: WrappedShowListener? = null

        override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
            wrappedCancelListener =
                WrappedCancelListener(
                    listener
                )
            super.setOnCancelListener(wrappedCancelListener)
        }

        override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
            wrappedDismissListener =
                WrappedDismissListener(
                    listener
                )
            super.setOnDismissListener(wrappedDismissListener)
        }

        override fun setOnShowListener(listener: DialogInterface.OnShowListener?) {
            wrappedShowListener =
                WrappedShowListener(
                    listener
                )
            super.setOnShowListener(wrappedShowListener)
        }

    }

    class WrappedCancelListener(delegate: DialogInterface.OnCancelListener?) :
        DialogInterface.OnCancelListener {
        private var weakRef = WeakReference(delegate)
        override fun onCancel(dialog: DialogInterface?) {
            weakRef.get()?.onCancel(dialog)
        }
    }

    class WrappedDismissListener(delegate: DialogInterface.OnDismissListener?) :
        DialogInterface.OnDismissListener {
        private var weakRef = WeakReference(delegate)
        override fun onDismiss(dialog: DialogInterface?) {
            weakRef.get()?.onDismiss(dialog)
        }
    }

    class WrappedShowListener(delegate: DialogInterface.OnShowListener?) :
        DialogInterface.OnShowListener {
        private var weakRef = WeakReference(delegate)
        override fun onShow(dialog: DialogInterface?) {
            weakRef.get()?.onShow(dialog)
        }
    }
}