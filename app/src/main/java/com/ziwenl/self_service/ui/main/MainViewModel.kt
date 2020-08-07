package com.ziwenl.self_service.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ziwenl.baselibrary.base.viewmodel.BaseViewModel
import com.ziwenl.baselibrary.ext.postNext
import com.ziwenl.self_service.service.PunchCardService
import kotlinx.coroutines.launch

/**
 * PackageName : com.ziwenl.self_service.ui.main
 * Author : Ziwen Lan
 * Date : 2020/7/8
 * Time : 15:13
 * Introduction :
 */
class MainViewModel @ViewModelInject constructor(private val repo: MainRepository) :
    BaseViewModel() {

    private val _stateLiveData: MutableLiveData<MainViewState> =
        MutableLiveData(MainViewState.initial())

    val stateLiveData: LiveData<MainViewState> = _stateLiveData

    init {
        viewModelScope.launch {
            _stateLiveData.postNext {
                it.copy(isStart = !PunchCardService.isEmpty())
            }
        }
    }
}