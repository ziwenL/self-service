package com.ziwenl.self_service.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import com.ziwenl.baselibrary.utils.LibContextProvider
import com.ziwenl.baselibrary.utils.bus.BusConst
import com.ziwenl.baselibrary.utils.bus.EventBusUtil
import com.ziwenl.baselibrary.utils.cache.CacheConst
import com.ziwenl.baselibrary.utils.cache.CacheUtil
import com.ziwenl.self_service.R
import com.ziwenl.self_service.bean.AttendanceDto
import com.ziwenl.self_service.bean.AttendanceListDto
import com.ziwenl.self_service.ui.test.TestActivity
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * PackageName : com.ziwenl.self_service.service
 * Author : Ziwen Lan
 * Date : 2020/7/22
 * Time : 15:23
 * Introduction : 处理打卡逻辑的前台服务
 */
class PunchCardService : Service() {
    companion object {
        private var punchCardService: PunchCardService? = null

        fun launch(context: Context) {
            if (!isEmpty()) return
            val intent = Intent(context, PunchCardService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            CacheUtil.put(CacheConst.KEY_IS_PUNCH_CARD, true)
        }

        fun stop() {
            CacheUtil.put(CacheConst.KEY_IS_PUNCH_CARD, false)
            CacheUtil.put(
                CacheConst.KEY_STOP_PUNCH_CARD_DAY_OF_WEEK,
                Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            )
            punchCardService?.stopSelf()
        }

        fun isEmpty(): Boolean {
            return punchCardService == null
        }

        /**
         * 保活
         */
        fun lifeSupport(context: Context) {
            if (CacheUtil.get(CacheConst.KEY_IS_PUNCH_CARD, false)) {
                launch(context)
            } else {
                //当 dayOfWeek 更新且非周末时，唤醒打卡服务
                val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                val oldDayOfWeek = CacheUtil.get(
                    CacheConst.KEY_STOP_PUNCH_CARD_DAY_OF_WEEK,
                    -1
                )
                var needWakeUp = oldDayOfWeek != -1
                        && oldDayOfWeek != dayOfWeek
                when (dayOfWeek) {
                    Calendar.SATURDAY, Calendar.SUNDAY -> {
                        //周末不打卡
                        needWakeUp = false
                    }
                }
                if (needWakeUp) {
                    launch(context)
                }
            }
        }
    }

    private val mMsgCodeCheckPunchCard = 1
    private val mMsgCodeStopPunchCard = 2
    private var mHandler: Handler? = null
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mNotificationBuild: Notification.Builder
    private val mNotificationId = 10

    override fun onCreate() {
        super.onCreate()
        EventBusUtil.post(BusConst.KEY_PUNCH_CARD_SERVICE_ACTIVE, true)
        punchCardService = this
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkPunchCard()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        punchCardService = null
        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null
        EventBusUtil.post(BusConst.KEY_PUNCH_CARD_SERVICE_ACTIVE, false)
        super.onDestroy()
    }


    /**
     * 检查打卡
     */
    private fun checkPunchCard() {
        //init
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    "PunchCardServiceId",
                    "PunchCardService",
                    NotificationManager.IMPORTANCE_HIGH
                )
            mNotificationManager.createNotificationChannel(channel)
            mNotificationBuild = Notification.Builder(this, "PunchCardServiceId")
        } else {
            mNotificationBuild = Notification.Builder(this)
        }
        mNotificationBuild.setContentTitle("钉钉自动打卡")
            .setContentText(String.format("APP保活服务"))
            .setSmallIcon(R.drawable.ic_shijian)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(mNotificationId, mNotificationBuild.build())
        } else {
            mNotificationManager.notify(
                mNotificationId,
                mNotificationBuild.build()
            )
        }
        //每次打卡前更新打卡数据
        val attendanceListDto = CacheUtil.get(
            CacheConst.KEY_ATTENDANCE_DATA,
            AttendanceListDto::class.java
        )
        if (attendanceListDto != null) {
            if (attendanceListDto.data != null) {
                val currentTime = parseToString(System.currentTimeMillis(), "HH:mm")
                val currentHour = currentTime?.substring(0, 2)?.toInt() ?: 0
                val currentMin = currentTime?.substring(3, 5)?.toInt() ?: 0
                for (dto in attendanceListDto.data) {
                    val hour = dto.time?.substring(0, 2)?.toInt() ?: 0
                    val min = dto.time?.substring(3, 5)?.toInt() ?: 0
                    if (currentHour > hour) {
                        dto.isPierced = true
                    } else if (currentHour == hour) {
                        dto.isPierced = currentMin >= min
                    } else {
                        dto.isPierced = false
                    }
                }
            }
            CacheUtil.put(CacheConst.KEY_ATTENDANCE_DATA, attendanceListDto)
        }
        //打卡事务逻辑处理
        mHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    //打卡处理中
                    mMsgCodeCheckPunchCard -> {
                        Timber.d("倒计时中，打卡处理中")
                        val nowTime = parseToString(System.currentTimeMillis(), "HH:mm")
                        val cacheData = CacheUtil.get(
                            CacheConst.KEY_ATTENDANCE_DATA,
                            AttendanceListDto::class.java
                        )!!
                        val attendanceList = cacheData.data!!
                        var needPunchAttendanceDto: AttendanceDto? = null
                        for (dto in attendanceList) {
                            if ((!dto.isPierced) && nowTime.equals(dto.time)) {
                                needPunchAttendanceDto = dto
                                dto.isPierced = true
                                CacheUtil.put(CacheConst.KEY_ATTENDANCE_DATA, cacheData)
                                break
                            }
                        }
                        //当前时刻是否需要打卡
                        if (needPunchAttendanceDto != null) {
                            punchCard(needPunchAttendanceDto)
                        }
                        //判断是否有下一个打卡时间
                        var nextNeedPunchCardDto: AttendanceDto? = null
                        for (dto in attendanceList) {
                            if (!dto.isPierced) {
                                nextNeedPunchCardDto = dto
                                break
                            }
                        }
                        if (nextNeedPunchCardDto == null) {
                            mNotificationBuild.setContentText(resources.getText(R.string.today_punch_card_has_been_completed))
                            mHandler?.sendEmptyMessageDelayed(mMsgCodeStopPunchCard, 10 * 1000)
                        } else {
                            mNotificationBuild.setContentText(
                                getString(
                                    R.string.next_punch_card_time,
                                    nextNeedPunchCardDto.time,
                                    if (nextNeedPunchCardDto.isPunchIn) "上班卡" else "下班卡"
                                )
                            )
                            mHandler?.sendEmptyMessageDelayed(mMsgCodeCheckPunchCard, 10 * 1000)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForeground(mNotificationId, mNotificationBuild.build())
                        } else {
                            mNotificationManager.notify(
                                mNotificationId,
                                mNotificationBuild.build()
                            )
                        }
                    }
                    //打卡结束
                    mMsgCodeStopPunchCard -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            stopForeground(true)
                        } else {
                            mNotificationManager.cancelAll()
                        }
                        stop()
                    }
                }
            }
        }
        mHandler?.sendEmptyMessageDelayed(mMsgCodeCheckPunchCard, 1000)
    }

    private fun punchCard(dto: AttendanceDto) {
        val powerManager =
            LibContextProvider.appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        //是否需要亮屏唤醒屏幕
        if (!powerManager.isInteractive) {
            val pm =
                LibContextProvider.appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
                TestActivity::class.java.simpleName
            )
            wl.acquire(60 * 1000L /*1 minutes*/)
            wl.release()
        }
        //是否需要解锁
        val mKeyguardManager =
            LibContextProvider.appContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (mKeyguardManager.isKeyguardLocked) {
            SelfAccessibilityService.unlock { punchCard(dto) }
        } else {
            //启动钉钉
            if (!dto.isPunchIn) {
                //下班卡
                SelfAccessibilityService.punchOut()
            }
            SelfAccessibilityService.openRimet(this, 0L)
            if (dto.isPunchIn) {
                SelfAccessibilityService.lockScreen(this, 4000)
            }
        }
    }

    private fun parseToString(time: Long, style: String?): String? {
        val now = Calendar.getInstance()
        now.timeInMillis = time
        val simpleDateFormat =
            DateFormat.getDateInstance() as SimpleDateFormat
        simpleDateFormat.applyPattern(style)
        return simpleDateFormat.format(now.time)
    }
}