package com.ziwenl.self_service.ui.splash

/**
 * PackageName : com.ziwenl.self_service.ui.splash
 * Author : Ziwen Lan
 * Date : 2020/7/7
 * Time : 15:00
 * Introduction :
 */
data class SplashViewState(
    val isLoading: Boolean,
    val throwable: Throwable?,
    val downTime: Int
) {

    companion object {

        fun initial(): SplashViewState {
            return SplashViewState(
                isLoading = false,
                throwable = null,
                downTime = -1
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SplashViewState

        if (isLoading != other.isLoading) return false
        if (throwable != other.throwable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + (throwable?.hashCode() ?: 0)
        return result
    }
}