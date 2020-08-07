package com.ziwenl.baselibrary.base.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ziwenl.baselibrary.base.view.IView

/**
 * PackageName : com.ziwenl.baselibrary.base.view.fragment
 * Author : Ziwen Lan
 * Date : 2020/7/2
 * Time : 11:21
 * Introduction :
 */
abstract class BaseFragment : Fragment(), IView {

    private var mRootView: View? = null

    abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mRootView = inflater.inflate(layoutId, container, false)
        return mRootView!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRootView = null
    }
}