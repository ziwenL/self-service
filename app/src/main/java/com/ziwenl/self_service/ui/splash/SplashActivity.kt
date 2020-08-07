package com.ziwenl.self_service.ui.splash

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.activity.viewModels
import com.ziwenl.baselibrary.base.view.activity.BaseActivity
import com.ziwenl.baselibrary.ext.observe
import com.ziwenl.baselibrary.utils.cache.CacheConst
import com.ziwenl.baselibrary.utils.cache.CacheUtil
import com.ziwenl.self_service.R
import com.ziwenl.self_service.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.splash_activity.*

/**
 * PackageName : com.ziwenl.self_service.ui.splash
 * Author : Ziwen Lan
 * Date : 2020/7/2
 * Time : 13:42
 * Introduction :
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    override val layoutId = R.layout.splash_activity

    private val mViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取屏幕宽高存起来有用
        val dm = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(dm)
        CacheUtil.put(CacheConst.KEY_SCREEN_WIDTH, dm.widthPixels)
        CacheUtil.put(CacheConst.KEY_SCREEN_HEIGHT, dm.heightPixels)

        tv_downtime.setOnClickListener {
            tv_downtime.isEnabled = false
            MainActivity.launch(this)
        }

        observe(mViewModel.viewStateLiveData, this::onNewState)
    }

    private fun onNewState(state: SplashViewState) {
        mProgressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        if (state.downTime != -1) {
            tv_downtime.text = String.format(getString(R.string.pass), state.downTime)
            if (state.downTime == 0) {
                MainActivity.launch(this)
            }
        }
    }
}