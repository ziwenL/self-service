package com.ziwenl.self_service.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * PackageName : com.ziwenl.self_service.ui.setting
 * Author : Ziwen Lan
 * Date : 2020/7/17
 * Time : 14:14
 * Introduction :
 */
data class AttendanceListDto(val data: MutableList<AttendanceDto>? = null) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(AttendanceDto)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AttendanceListDto> {
        override fun createFromParcel(parcel: Parcel): AttendanceListDto {
            return AttendanceListDto(parcel)
        }

        override fun newArray(size: Int): Array<AttendanceListDto?> {
            return arrayOfNulls(size)
        }
    }
}