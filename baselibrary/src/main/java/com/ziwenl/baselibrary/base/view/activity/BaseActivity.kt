package com.ziwenl.baselibrary.base.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ziwenl.baselibrary.base.view.IView

/**
 * PackageName : com.ziwenl.baselibrary.base.view.activity
 * Author : Ziwen Lan
 * Date : 2020/7/2
 * Time : 11:20
 * Introduction :
 */
abstract class BaseActivity : AppCompatActivity(), IView {
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }
}