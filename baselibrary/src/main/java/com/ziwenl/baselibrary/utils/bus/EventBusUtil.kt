package com.ziwenl.baselibrary.utils.bus

import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * PackageName : com.ziwenl.baselibrary.utils.bus
 * Author : Ziwen Lan
 * Date : 2020/7/14
 * Time : 9:45
 * Introduction :
 */
class EventBusUtil {
    companion object {
        private const val EVENT_KEY = "event_key"

        fun post(key: String, value: Any?) {
            LiveEventBus.get(EVENT_KEY).post(MessageEventDto(key, value))
        }

        fun observe(owner: LifecycleOwner, @NonNull callBack: EventCallBack) {
            LiveEventBus
                .get(EVENT_KEY)
                .observe(owner, Observer {
                    if (it is MessageEventDto) {
                        callBack.onEventComing(it)
                    }
                })
        }
    }
}

data class MessageEventDto(val key: String, val value: Any?)

object BusConst {
    const val KEY_DEVICE_ADMIN_IS_ENABLE = "device_admin_is_enable"
    const val KEY_ACCESSIBILITY_IS_ENABLE = "accessibility_is_enable"
    const val KEY_UNLOCK_ERROR = "unlock_error"
    const val KEY_PUNCH_CARD_SERVICE_ACTIVE = "punch_card_service_active"
}