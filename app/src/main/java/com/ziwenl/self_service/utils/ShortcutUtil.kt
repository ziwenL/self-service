package com.ziwenl.self_service.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.DrawableRes
import java.util.*

/**
 * PackageName : com.ziwenl.self_service.utils
 * Author : Ziwen Lan
 * Date : 2020/7/30
 * Time : 15:12
 * Introduction :
 */
class ShortcutUtil {
    companion object {

        /**
         * 创建动态快捷方式
         *
         * @param iconResId  icon资源id
         * @param shortLabel 短标签
         * @param longLabel  长标签
         * @param id         快捷方式ID
         * @param aimsCls    目标Activity
         *
         * 每个应用最多可以注册5个Shortcuts，无论是动态形式还是静态形式
         */
        fun createShortcut(
            context: Context,
            @DrawableRes iconResId: Int,
            shortLabel: String,
            longLabel: String,
            id: String,
            aimsCls: Class<*>?
        ): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val shortcutManager =
                    context.getSystemService(ShortcutManager::class.java) ?: return false
                val infos: MutableList<ShortcutInfo> =
                    ArrayList()
                val intent = Intent(context, aimsCls)
                intent.action = Intent.ACTION_VIEW
                val info = ShortcutInfo.Builder(context, id)
                    .setShortLabel(shortLabel)
                    .setLongLabel(longLabel)
                    .setIcon(Icon.createWithResource(context, iconResId))
                    .setIntent(intent)
                    .build()
                infos.add(info)
                var isUpdate = false
                for (shortcutInfo in shortcutManager.dynamicShortcuts) {
                    if (shortcutInfo.id == id) {
                        isUpdate = true
                        break
                    }
                }
                if (shortcutManager.dynamicShortcuts.size < 5 || isUpdate) {
                    shortcutManager.addDynamicShortcuts(infos)
                    return true
                } else {
                    //超出注册数量
                    return false
                }
            } else {
                //系统版本低于 7.0
                return false
            }
        }


        /**
         * 移除所有动态快捷方式并禁用所有桌面快捷方式
         * @param tips 提示语：当用户点击被禁用的桌面快捷方式时显示的提示语
         */
        fun removeAll(context: Context, tips: String?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val shortcutManager =
                    context.getSystemService(ShortcutManager::class.java) ?: return
                //禁用所有动态快捷方式
                val dynamicShortcuts =
                    shortcutManager.dynamicShortcuts
                val dynamicIds: MutableList<String> =
                    ArrayList()
                for (shortcutInfo in dynamicShortcuts) {
                    dynamicIds.add(shortcutInfo.id)
                }
                shortcutManager.disableShortcuts(
                    dynamicIds, if (tips.isNullOrEmpty()) "已失效" else tips
                )
                //禁用所有桌面快捷方式
                val pinnedShortcuts =
                    shortcutManager.pinnedShortcuts
                val pinnedIds: MutableList<String> =
                    ArrayList()
                for (shortcutInfo in pinnedShortcuts) {
                    pinnedIds.add(shortcutInfo.id)
                }
                shortcutManager.disableShortcuts(
                    pinnedIds, if (tips.isNullOrEmpty()) "已失效" else tips
                )
                //移除所有快捷方式
                shortcutManager.removeAllDynamicShortcuts()
            }
        }

        /**
         * 移除指定动态快捷方式并禁用指定桌面快捷方式
         *
         * @param ids 需要移除的快捷方式ID
         * @param tips 提示语：当用户点击被禁用的桌面快捷方式时显示的提示语
         */
        fun removeShortcut(
            context: Context,
            vararg ids: String,
            tips: String?
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val shortcutManager =
                    context.getSystemService(ShortcutManager::class.java) ?: return
                val dynamicIds: List<String> =
                    ArrayList(Arrays.asList<String>(*ids))
                shortcutManager.disableShortcuts(
                    dynamicIds,
                    if (tips.isNullOrEmpty()) "已失效" else tips
                )
                shortcutManager.removeDynamicShortcuts(dynamicIds)
            }
        }
    }
}