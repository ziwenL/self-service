package com.ziwenl.self_service.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.ziwenl.baselibrary.utils.bus.BusConst
import com.ziwenl.baselibrary.utils.bus.EventBusUtil
import timber.log.Timber

/**
 * PackageName : com.ziwenl.self_service.service
 * Author : Ziwen Lan
 * Date : 2020/7/17
 * Time : 17:10
 * Introduction : 设备管理器启用状态监听器
 */
class LockScreenDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Timber.d("激活使用")
        //通知已经激活
        EventBusUtil.post(BusConst.KEY_DEVICE_ADMIN_IS_ENABLE, true)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Timber.d("取消激活")
        EventBusUtil.post(BusConst.KEY_DEVICE_ADMIN_IS_ENABLE, false)
    }
}