package com.ziwenl.self_service.base

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.ziwenl.self_service.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * PackageName : com.ziwenl.self_service.ui.base
 * Author : Ziwen Lan
 * Date : 2020/7/3
 * Time : 15:18
 * Introduction :
 */
@HiltAndroidApp
class BaseApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        initLeakCanary()
    }


    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(
            priority: Int,
            tag: String?,
            message: String,
            t: Throwable?
        ) {

        }
    }
}