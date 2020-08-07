package com.ziwenl.baselibrary.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * PackageName : com.ziwenl.baselibrary.ext
 * Author : Ziwen Lan
 * Date : 2020/7/3
 * Time : 15:12
 * Introduction :
 */
fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: (t: T) -> Unit) {

    liveData.observe(this, Observer { it?.let { t -> observer(t) } })

}