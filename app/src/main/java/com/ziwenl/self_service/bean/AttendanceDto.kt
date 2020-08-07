package com.ziwenl.self_service.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * PackageName : com.ziwenl.self_service.ui.setting
 * Author : Ziwen Lan
 * Date : 2020/7/17
 * Time : 14:00
 * Introduction :
 */
data class AttendanceDto(val isPunchIn: Boolean, val time: String? = null,var isPierced:Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isPunchIn) 1 else 0)
        parcel.writeString(time)
        parcel.writeByte(if (isPierced) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AttendanceDto> {
        override fun createFromParcel(parcel: Parcel): AttendanceDto {
            return AttendanceDto(parcel)
        }

        override fun newArray(size: Int): Array<AttendanceDto?> {
            return arrayOfNulls(size)
        }
    }
}