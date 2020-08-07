package com.ziwenl.self_service.ui.setting

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import com.ziwenl.baselibrary.base.repository.BaseRepositoryBoth
import com.ziwenl.baselibrary.base.repository.ILocalDataSource
import com.ziwenl.baselibrary.base.repository.IRemoteDataSource
import com.ziwenl.baselibrary.utils.cache.CacheConst
import com.ziwenl.baselibrary.utils.cache.CacheUtil
import com.ziwenl.self_service.bean.AttendanceDto
import com.ziwenl.self_service.bean.AttendanceListDto
import com.ziwenl.self_service.service.LockScreenDeviceAdminReceiver
import com.ziwenl.self_service.service.SelfAccessibilityService
import javax.inject.Inject

/**
 * PackageName : com.ziwenl.self_service.ui.setting
 * Author : Ziwen Lan
 * Date : 2020/7/17
 * Time : 10:50
 * Introduction :
 */
class SettingRepository @Inject constructor(
    remoteDatasource: SettingRemoteDataSource,
    localDataSource: SettingLocalDataSource
) : BaseRepositoryBoth<SettingRemoteDataSource, SettingLocalDataSource>(
    remoteDatasource,
    localDataSource
)

class SettingRemoteDataSource @Inject constructor() : IRemoteDataSource {

}

class SettingLocalDataSource @Inject constructor() : ILocalDataSource {

    /**
     * 判断是否启用设备管理器
     */
    fun isAdminActive(context: Context): Boolean {
        val devicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return devicePolicyManager.isAdminActive(ComponentName(context, LockScreenDeviceAdminReceiver::class.java))
    }

    fun isAccessibilityActive(): Boolean {
        return !SelfAccessibilityService.isEmpty()
    }

    fun hasPassword(): Boolean {
        return !CacheUtil.get(CacheConst.KEY_LOCK_SCREEN_PASSWORD, "").isEmpty()
    }

    fun clearPassword() {
        CacheUtil.put(CacheConst.KEY_LOCK_SCREEN_PASSWORD, "")
    }

    fun setPassword(pwd: String) {
        CacheUtil.put(CacheConst.KEY_LOCK_SCREEN_PASSWORD, pwd)
    }

    fun addAttendanceDto(dto: AttendanceDto) {
        var attendanceListDto: AttendanceListDto? =
            CacheUtil.get(CacheConst.KEY_ATTENDANCE_DATA, AttendanceListDto::class.java)
        if (attendanceListDto == null) {
            attendanceListDto =
                AttendanceListDto(mutableListOf())
        }
        var index = -1
        if (attendanceListDto.data!!.size != 0) {
            for (i in 0 until attendanceListDto.data!!.size) {
                val hour = dto.time?.substring(0, 2)?.toInt() ?: 0
                val min = dto.time?.substring(3, 5)?.toInt() ?: 0
                val currentHour =
                    attendanceListDto.data?.get(i)?.time?.substring(0, 2)?.toInt() ?: 0
                val currentMin = attendanceListDto.data?.get(i)?.time?.substring(3, 5)?.toInt() ?: 0
                if (hour < currentHour) {
                    index = i
                    break
                } else if (hour == currentHour) {
                    if (min <= currentMin) {
                        index = i
                        break
                    } else {
                        index = if (i + 1 > attendanceListDto.data!!.size - 1) -1 else i + 1
                    }
                }
            }
        }
        if (index != -1) {
            attendanceListDto.data?.add(index, dto)
        } else {
            attendanceListDto.data?.add(dto)
        }
        CacheUtil.put(CacheConst.KEY_ATTENDANCE_DATA, attendanceListDto)
    }

    fun removeAttendanceDto(dto: AttendanceDto) {
        val attendanceListDto: AttendanceListDto? =
            CacheUtil.get(CacheConst.KEY_ATTENDANCE_DATA, AttendanceListDto::class.java)
        if (attendanceListDto?.data != null) {
            attendanceListDto.data.remove(dto)
            CacheUtil.put(CacheConst.KEY_ATTENDANCE_DATA, attendanceListDto)
        }
    }

    fun getAttendanceDtoList(): MutableList<AttendanceDto> {
        val attendanceListDto: AttendanceListDto? =
            CacheUtil.get(CacheConst.KEY_ATTENDANCE_DATA, AttendanceListDto::class.java)
        return if (attendanceListDto == null) {
            mutableListOf()
        } else {
            attendanceListDto.data ?: mutableListOf()
        }
    }
}