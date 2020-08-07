package com.ziwenl.baselibrary.utils.cache

import android.os.Parcelable
import android.text.TextUtils
import com.tencent.mmkv.MMKV
import com.ziwenl.baselibrary.R
import com.ziwenl.baselibrary.utils.LibContextProvider
import timber.log.Timber

/**
 * PackageName : com.ziwenl.baselibrary.utils.cache
 * Author : Ziwen Lan
 * Date : 2020/7/16
 * Time : 10:51
 * Introduction :
 */
class CacheUtil {
    companion object {

        init {
            val rootDir = MMKV.initialize(LibContextProvider.appContext)
            Timber.d("存储路径 rootDir = $rootDir")
        }

        fun put(key: String, value: Any?): Boolean {
            if (TextUtils.isEmpty(key)) {
                Timber.v("key 为空")
                return false
            }
            if (value == null) {
                MMKV.defaultMMKV().removeValueForKey(key)
                Timber.v("value 为 null 删除存储值")
                return false
            } else {
                if (value is Int) {
                    MMKV.defaultMMKV().encode(key, value)
                    return true
                } else if (value is String) {
                    MMKV.defaultMMKV().encode(key, value)
                    return true
                } else if (value is Double) {
                    MMKV.defaultMMKV().encode(key, value)
                    return true
                } else if (value is Float) {
                    MMKV.defaultMMKV().encode(key, value)
                    return true
                } else if (value is ByteArray) {
                    MMKV.defaultMMKV().encode(key, value)
                    return true
                } else if (value is Boolean) {
                    MMKV.defaultMMKV().encode(key, value)
                    return true
                } else if (value is Long) {
                    MMKV.defaultMMKV().encode(key, value)
                    return true
                } else if (value is Parcelable) {
                    MMKV.defaultMMKV().encode(key, value)
                    return true
                } else {
                    Timber.w("value 类型不符合存储要求")
                    return false
                }
            }
        }

        fun <T> get(key: String, defaultValue: T): T {
            if (MMKV.defaultMMKV().containsKey(key)) {
                if (defaultValue is Int) {
                    return MMKV.defaultMMKV().decodeInt(key, defaultValue) as T
                } else if (defaultValue is String) {
                    return MMKV.defaultMMKV().decodeString(key, defaultValue) as T
                } else if (defaultValue is Double) {
                    return MMKV.defaultMMKV().decodeDouble(key, defaultValue) as T
                } else if (defaultValue is Float) {
                    return MMKV.defaultMMKV().decodeFloat(key, defaultValue) as T
                } else if (defaultValue is ByteArray) {
                    return MMKV.defaultMMKV().decodeBytes(key, defaultValue) as T
                } else if (defaultValue is Boolean) {
                    return MMKV.defaultMMKV().decodeBool(key, defaultValue) as T
                } else if (defaultValue is Long) {
                    return MMKV.defaultMMKV().decodeLong(key, defaultValue) as T
                } else {
                    Timber.w(R.string.warn_type_does_not_match.toString())
                    return defaultValue
                }
            } else {
                Timber.w("key: $key 不存在")
                return defaultValue
            }
        }

        fun <T : Parcelable> get(
            key: String,
            tClass: Class<T>
        ): T? {
            return MMKV.defaultMMKV().decodeParcelable(key, tClass)
        }

        fun <T : Parcelable> get(
            key: String,
            tClass: Class<T>,
            defaultValue: T
        ): T {
            return MMKV.defaultMMKV().decodeParcelable(key, tClass, defaultValue)
        }
    }
}