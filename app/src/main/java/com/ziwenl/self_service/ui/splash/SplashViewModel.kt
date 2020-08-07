package com.ziwenl.self_service.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ziwenl.baselibrary.base.viewmodel.BaseViewModel
import com.ziwenl.baselibrary.ext.postNext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PackageName : com.ziwenl.self_service.ui.splash
 * Author : Ziwen Lan
 * Date : 2020/7/7
 * Time : 9:50
 * Introduction :
 */
class SplashViewModel @ViewModelInject constructor(
    val repo: SplashRepository
) : BaseViewModel() {

    private val _viewStateLiveData: MutableLiveData<SplashViewState> =
        MutableLiveData(SplashViewState.initial())

    val viewStateLiveData: LiveData<SplashViewState> = _viewStateLiveData

    init {
        viewModelScope.launch {
            //延迟5秒
            for (i in 5 downTo 0) {
                delay(1000)
                _viewStateLiveData.postNext {
                    it.copy(false, null, i)
                }
            }
        }
    }
}