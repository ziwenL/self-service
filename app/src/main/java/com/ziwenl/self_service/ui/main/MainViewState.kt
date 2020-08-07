package com.ziwenl.self_service.ui.main

/**
 * PackageName : com.ziwenl.self_service.ui.main
 * Author : Ziwen Lan
 * Date : 2020/7/8
 * Time : 15:22
 * Introduction :
 */
data class MainViewState(
    val isLoading: Boolean,
    val throwable: Throwable?,
    val isStart:Boolean
) {
    companion object {

        fun initial(): MainViewState {
            return MainViewState(
                isLoading = false,
                throwable = null,
                isStart=false
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MainViewState

        if (isLoading != other.isLoading) return false
        if (throwable != other.throwable) return false
        if (isStart != other.isStart) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + (throwable?.hashCode() ?: 0)
        return result
    }
}