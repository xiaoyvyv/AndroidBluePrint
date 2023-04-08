package com.xiaoyv.blueprint.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.*
import com.github.nukc.stateview.StateView
import com.xiaoyv.blueprint.NavActivity
import com.xiaoyv.blueprint.activity.*
import com.xiaoyv.blueprint.app.R
import com.xiaoyv.blueprint.app.databinding.ActivityMainBinding
import com.xiaoyv.blueprint.base.binding.BaseMvpBindingActivity
import com.xiaoyv.blueprint.kts.LazyUtils.loadRootFragment
import com.xiaoyv.calendar.CalendarAccount
import com.xiaoyv.calendar.CalendarEvent
import com.xiaoyv.calendar.CalendarReminder
import com.xiaoyv.calendar.ics.IcsCreator
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiNormalDialog
import com.xiaoyv.widget.dialog.UiOptionsDialog
import com.xiaoyv.widget.kts.isSoftInputModeAlwaysVisible
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.IOException

class MainActivity :
    BaseMvpBindingActivity<ActivityMainBinding, MainContract.View, MainPresenter>(),
    MainContract.View {

    private val tbsApk by lazy { PathUtils.getExternalAppFilesPath() + "/tbs/tbs.apk" }

    override fun createPresenter() = MainPresenter()

    private val events = arrayListOf<CalendarEvent>()

    @SuppressLint("NewApi")
    override fun initView() {
        stateController.fitTitleAndStatusBar = true

        binding.download.setOnFastLimitClickListener {
            ActivityUtils.startActivity(DownloadActivity::class.java)
        }

//        binding.toolbar.setLeftIcon()
        binding.toolbar.bottomDivider = true

        ResourceUtils.copyFileFromAssets("tbs.tbs", tbsApk)

        binding.tvTest1.updateLayoutParams {
            height = ConvertUtils.dp2px(60f)
        }
    }

    override fun initData() {
        val mainFragment = MainFragment()
        loadRootFragment(binding.flContainer.id, mainFragment)
    }

    override fun initListener() {
        val accountName = "201603246"
        val calendarAccount = CalendarAccount(
            name = "jwc-timetable",
            accountName = accountName,
            displayName = "新长大助手-课表日历"
        )

        binding.checkCalendar.setOnClickListener {
            runWithCalendarPermission(block = {
                val account = CalendarReminder.checkCalendarAccount(this, accountName)
                ToastUtils.showShort("ID: $account")
            })
        }
        binding.addCalendar.setOnClickListener {
            runWithCalendarPermission {
                val account =
                    CalendarReminder.createCalendarAccount(this, calendarAccount)
                ToastUtils.showShort("ID: $account")
            }
        }

        binding.tvCrop.setOnFastLimitClickListener {
            ActivityUtils.startActivity(CropActivity::class.java)
        }

        binding.addEvent.setOnClickListener {
            val startTimeStr = "2023-02-13 00:00:00"
            val startTimeMills = TimeUtils.string2Millis(startTimeStr)

            runWithCalendarPermission {
                events.clear()

                testTable.curriculums.forEach { table ->
                    val dayMills = 24 * 60 * 60 * 1000L
                    val weekMills = dayMills * 7L

                    table.allWeek.forEachIndexed { termWeekIndex, c ->
                        if (c == '1') {
                            // 周次时间偏移
                            val weekMillOffset: Long = termWeekIndex * weekMills
                            // 星期几时间偏移
                            val weekDayOffset = table.week.toLong() * dayMills
                            // 节次偏移
                            val sectionMill = table.section.toSectionMills()
                            val sectionStartOffset = sectionMill.first
                            val sectionEndOffset = sectionMill.second

                            val startTime =
                                startTimeMills + weekMillOffset + weekDayOffset + sectionStartOffset
                            Log.e(
                                "startTimeMills",
                                "startTime: $startTime, " +
                                        "startTimeMills: $startTimeMills, " +
                                        "termWeekIndex: $termWeekIndex, " +
                                        "weekMills: $weekMills, " +
                                        "weekMillOffset: $weekMillOffset, " +
                                        "weekDayOffset: $weekDayOffset, " +
                                        "sectionStartOffset: $sectionStartOffset"
                            )

                            val endTime =
                                startTimeMills + weekMillOffset + weekDayOffset + sectionEndOffset

                            val calendarEvent = CalendarEvent().apply {
                                this.eventName = table.name.orEmpty()
                                this.startTime = startTime
                                this.endTime = endTime
                                this.description = """
                                    ${table.name}

                                    老师：${table.teacher}
                                    教室：${table.room}
                                    星期 ${table.week.toInt() + 1}，第 ${table.section.toInt() + 1} 大节
                                    
                                    ${if (table.crashCourses.isNullOrEmpty()) "没有冲突课程" else "存在冲突课程！"}
                                """.trimIndent()
                                this.eventLocation = table.room
                            }

                            events.add(calendarEvent)
//                            Log.e(
//                                "Table", "${table.name}：" +
//                                        "第${termWeekIndex + 1}周，" +
//                                        "节次: ${table.section.toInt() + 1} -> ${sectionMill.first}，" +
//                                        "星期偏移：$weekDayOffset，" + TimeUtils.millis2String(
//                                    startTime
//                                )
//                            )

                            try {
                                //   CalendarReminder.createCalendarEvent(this, 111, calendarEvent)
                            } catch (e: Throwable) {
                                LogUtils.e(e)
                                ToastUtils.showShort(e.toString())
                            }
                        }
                    }
                }

                generateIcs(calendarAccount, events)

//                val eventId = CalendarReminder.createCalendarEvent(this, 3, CalendarEvent().apply {
//                    this.eventName = "TestEvent"
//                    this.startTime = System.currentTimeMillis() + 30000
//                    this.endTime = System.currentTimeMillis() + 60000
//                    this.description = "TestEvent Desc"
//                    this.eventLocation = "TestEvent Location"
//                })
            }
        }
        binding.deleteEvent.setOnClickListener {
            runWithCalendarPermission {
                CalendarReminder.deleteCalendarEvent(this, 2)
            }
        }
        binding.nav.setOnFastLimitClickListener {
            ActivityUtils.startActivity(NavActivity::class.java)
        }
        binding.tvTbs.setOnFastLimitClickListener {
            val url = "https://debugtbs.qq.com"
            ActivityUtils.startActivity(
                bundleOf("webUrl" to url), WebActivity::class.java
            )
        }
        binding.tvLocalStorage.setOnFastLimitClickListener {
//            LogUtils.e(tbsApk)
//            val time = System.currentTimeMillis()
//
//
//            X5InstallHelper.installByLocal(tbsApk, 46141) {
//                LogUtils.e("安装耗时：${System.currentTimeMillis() - time}, 安卓结果：$it")
//            }

//            X5InstallHelper.downloadTbs(useCache = true) {
//                ToastUtils.showShort("下载完成，准备安装！")
//
//                X5InstallHelper.installByLocal(it, 46141) { code ->
//                    ToastUtils.showShort("安装结果：$code！")
//                    doAsync {
//                        runBlocking {
//                            delay(2000)
//                            // AppUtils.exitApp()
//                        }
//                    }
//                }
//            }
        }
        binding.tvJwc.setOnFastLimitClickListener {
            val url = "https://atrust.yangtzeu.edu.cn:4443"
            ActivityUtils.startActivity(
                bundleOf("webUrl" to url), WebActivity::class.java
            )

        }
        binding.tvTest1.setOnFastLimitClickListener {
            val alertDialog = AlertDialog.Builder(this)
                .setMessage("网页请求，是否允许？")
                .setPositiveButton("允许") { _, _ ->
                    val url = "https://chat.openai.com/auth/login"
                    ActivityUtils.startActivity(
                        bundleOf("webUrl" to url), WebActivity::class.java
                    )
                }
                .setNegativeButton("取消", null)
                .create()
            alertDialog.show()
        }

        // PHPSESSID=unl536vasbbnmmok39t2e7871ktccskf
        binding.cookie.setOnFastLimitClickListener {
            val loginUrl =
                "https://atrust.yangtzeu.edu.cn:4443/passport/v1/user/onlineInfo?clientType=SDPBrowserClient&platform=Windows&lang=zh-CN"
            // val readCookie = X5CookieHelper.readCookie(url)
            val baiDuUrl =
                "http://mysearch.pae.baidu.com/api/favorites?action=r&wd=&stype=&pn=2&ipp=20&origin=wwwnormal&_=1673110374627&cb="

            val qiNiu = "https://portal.qiniu.com/api/gaea/user/overview"
            val bilibiliApi = "https://api.bilibili.com/x/web-interface/nav"

            @Suppress("UnnecessaryVariable")
            val url = bilibiliApi

            val httpClient = OkHttpClient.Builder()
                .cookieJar(OkHttpCookieJar())
                .build()
            httpClient.newCall(
                Request(
                    url.toHttpUrl(),
                    Headers.Builder()
                        .apply {
                            add("Referer", url)
                            add(
                                "User-Agent",
                                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:108.0) Gecko/20100101 Firefox/108.0"
                            )
                        }
                        .build(),
                    "GET"
                )
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    LogUtils.e(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val string = response.body.string()

                    runCatching {
                        val fromJson = GsonUtils.fromJson(string, Map::class.java)
                        LogUtils.e(GsonUtils.toJson(fromJson))
                    }.onFailure {
                        LogUtils.e(string)
                    }
                }
            })
        }

        binding.tvTest.setOnFastLimitClickListener(2500) {
//            stateController.showRetryView()
            ToastUtils.showShort("tttttttttttttt")

            val optionsDialog = UiOptionsDialog.Builder().apply {
                itemDataList = arrayListOf(
                    "横屏",
                    "Toast",
                    "Setting",
                    "375 - true",
                    "竖屏"
                )
                itemLastColor = Color.RED
                onOptionsClickListener = { dialog, data, position ->
                    when (position) {
                        0 -> {
                            ScreenUtils.setLandscape(this@MainActivity)
                        }

                        1 -> {

                            ToastUtils.showLong("adaptHeight")
                        }

                        2 -> {
                            dialog.dismiss()

                            UiNormalDialog.Builder().apply {
                                customView = R.layout.dialog_input
                                onDismissListener = {
                                    LogUtils.e("onDismissListener")
                                }
                                onStartListener = { dialog, window ->
                                    LogUtils.e("onStartListener")
                                    window.isSoftInputModeAlwaysVisible = true
                                }
                            }.create()
                                .show(this@MainActivity)
                        }

                        3 -> {

                        }

                        else -> {
                            ScreenUtils.setPortrait(this@MainActivity)
                        }
                    }
                    true
                }
            }.create()
            optionsDialog.show(this)

        }
    }

    private fun generateIcs(calendarAccount: CalendarAccount, events: ArrayList<CalendarEvent>) {
        val content = IcsCreator.createIcsContent(calendarAccount, events)
        LogUtils.e(content)

        FileIOUtils.writeFileFromString(
            PathUtils.getExternalAppFilesPath() + "/calendar.ics",
            content
        )
    }

    override fun onPresenterCreated() {
        presenter.v2pPrint()
    }

    override fun p2vShowInfo() {

    }

    override fun onClickStateView(stateView: StateView, view: View) {

    }

    override fun LifecycleOwner.initViewObserver() {

    }
}

fun String.toSectionMills(): Pair<Long, Long> {
    val section = toIntOrNull() ?: return 0L to 0L

    val minuteMills = 60 * 1000
    val hourMills = 60 * minuteMills

    // 课程大节
    return when (section + 1) {
        1 -> (8 * hourMills).toLong() to (9 * hourMills + 35 * minuteMills).toLong()
        2 -> (10 * hourMills + 5 * minuteMills).toLong() to (11 * hourMills + 40 * minuteMills).toLong()
        3 -> (14 * hourMills).toLong() to (15 * hourMills + 35 * minuteMills).toLong()
        4 -> (16 * hourMills + 5 * minuteMills).toLong() to (17 * hourMills + 40 * minuteMills).toLong()
        5 -> (19 * hourMills).toLong() to (20 * hourMills + 35 * minuteMills).toLong()
        6 -> (20 * hourMills + 45 * minuteMills).toLong() to (22 * hourMills + 20 * minuteMills).toLong()
        // 午间课
        7 -> (12 * hourMills).toLong() to (13 * hourMills + 35 * minuteMills).toLong()
        // 晚间课
        8 -> (18 * hourMills).toLong() to (18 * hourMills + 45 * minuteMills).toLong()
        else -> 0L to 0L
    }
}
