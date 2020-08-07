package com.ziwenl.self_service.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import timber.log.Timber

/**
 * PackageName : com.ziwenl.self_service.utils
 * Author : Ziwen Lan
 * Date : 2020/7/14
 * Time : 13:52
 * Introduction :
 */
class AccessibilityActionUtil {

    companion object {
        /**
         * 滑动操作
         *
         * @param service   无障碍服务实例
         * @param path      移动路径
         * @param startTime 延时启动时间
         * @param duration  执行持续时间
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun move(
            service: AccessibilityService,
            path: Path,
            @IntRange(from = 0) startTime: Long,
            @IntRange(from = 0) duration: Long,
            callback: Callback?
        ) {
            if (callback == null) {
                service.dispatchGesture(
                    GestureDescription.Builder()
                        .addStroke(StrokeDescription(path, startTime, duration)).build(),
                    null,
                    null
                )
            } else {
                service.dispatchGesture(
                    GestureDescription.Builder()
                        .addStroke(StrokeDescription(path, startTime, duration)).build(),
                    object : GestureResultCallback() {
                        override fun onCompleted(gestureDescription: GestureDescription) {
                            super.onCompleted(gestureDescription)
                            callback.onSuccess()
                        }

                        override fun onCancelled(gestureDescription: GestureDescription) {
                            super.onCancelled(gestureDescription)
                            callback.onError()
                        }
                    },
                    null
                )
            }
        }

        /**
         * 查找节点信息
         *
         * @param id                 控件id  eg: com.tencent.mm:id/aqg
         * @param text               控件文本 eg: 打开
         * @param contentDescription 控件描述 eg: 表情
         * @return null表示未找到
         */
        fun findNodeInfo(
            service: AccessibilityService,
            id: String,
            text: String,
            contentDescription: String
        ): AccessibilityNodeInfo? {
            if (TextUtils.isEmpty(text) && TextUtils.isEmpty(contentDescription)) {
                return null
            }
            val nodeInfo = service.rootInActiveWindow
            if (nodeInfo != null) {
                val list = nodeInfo
                    .findAccessibilityNodeInfosByViewId(id)
                for (n in list) {
                    val nodeInfoText =
                        if (TextUtils.isEmpty(n.text)) "" else n.text
                            .toString()
                    val nodeContentDescription =
                        if (TextUtils.isEmpty(n.contentDescription)) "" else n.contentDescription
                            .toString()
                    if (TextUtils.isEmpty(text)) {
                        if (contentDescription == nodeContentDescription) {
                            return n
                        }
                    } else {
                        if (text == nodeInfoText) {
                            return n
                        }
                    }
                }
            }
            return null
        }

        /**
         * 点击节点
         *
         * @return true表示点击成功
         */
        fun performClickNodeInfo(nodeInfo: AccessibilityNodeInfo?): Boolean {
            if (nodeInfo != null) {
                if (nodeInfo.isClickable) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                } else {
                    val parent = nodeInfo.parent
                    if (parent != null) {
                        val isParentClickSuccess = performClickNodeInfo(parent)
                        parent.recycle()
                        return isParentClickSuccess
                    }
                }
            }
            return false
        }

        /**
         * 查找并点击节点
         */
        fun findAndPerformClickNodeInfo(
            service: AccessibilityService,
            id: String,
            text: String,
            contentDescription: String
        ): Boolean {
            return performClickNodeInfo(findNodeInfo(service, id, text, contentDescription))
        }

        /**
         * 通过文本查找节点（节点无id值时使用）
         */
        fun findNodeInfoByText(
            service: AccessibilityService,
            text: String,
            className: String
        ): AccessibilityNodeInfo? {
            val nodeInfo = service.rootInActiveWindow
            val list = nodeInfo.findAccessibilityNodeInfosByText(text)
            Timber.d("text = %s   list.size = %s", text, list.size)
            for (n in list) {
                Timber.d("n.text = %s   n.className = %s", n.text, n.className)
                if (n.className.equals(className)) {
                    val bounds = Rect()
                    n.getBoundsInScreen(bounds)
                    Timber.d(
                        "选中了这个 n.text = %s   n.className = %s \n bounds = %s",
                        n.text,
                        n.className,
                        bounds
                    )
                    return n
                }
            }
            return null
        }
    }
}

//---------------- 结果回调 ----------------
interface Callback {
    fun onSuccess()
    fun onError()
}