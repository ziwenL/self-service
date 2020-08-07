package com.ziwenl.self_service.ui.setting

import com.ziwenl.self_service.bean.AttendanceDto

/**
 * PackageName : com.ziwenl.self_service.ui.setting
 * Author : Ziwen Lan
 * Date : 2020/7/10
 * Time : 17:48
 * Introduction :
 */
data class SettingViewState(
    val isLoading: Boolean,
    val throwable: Throwable?,
    val isAdminActive: Boolean,
    val isAccessibilityActive: Boolean,
    val hasPassword: Boolean,
    val hasOpenRimetPermission: Boolean,
    var data: MutableList<AttendanceDto>
) {

    companion object {

        fun initial(): SettingViewState {
            return SettingViewState(
                isLoading = false,
                throwable = null,
                isAdminActive = false,
                isAccessibilityActive = false,
                hasPassword = false,
                data = mutableListOf(),
                hasOpenRimetPermission = false
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SettingViewState

        if (isLoading != other.isLoading) return false
        if (throwable != other.throwable) return false
        if (isAdminActive != other.isAdminActive) return false
        if (isAccessibilityActive != other.isAccessibilityActive) return false
        if (hasPassword != other.hasPassword) return false
        if (data != other.data) return false
        if (hasOpenRimetPermission != other.hasOpenRimetPermission) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + (throwable?.hashCode() ?: 0)
        return result
    }
}