package com.xiaoyv.blueprint.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.*
import com.github.nukc.stateview.StateView
import com.xiaoyv.blueprint.NavActivity
import com.xiaoyv.blueprint.activity.WebActivity
import com.xiaoyv.blueprint.app.R
import com.xiaoyv.blueprint.app.databinding.ActivityMainBinding
import com.xiaoyv.blueprint.base.binding.BaseMvpBindingActivity
import com.xiaoyv.blueprint.utils.LazyUtils.loadRootFragment
import com.xiaoyv.calendar.CalendarAccount
import com.xiaoyv.calendar.CalendarEvent
import com.xiaoyv.calendar.CalendarReminder
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiNormalDialog
import com.xiaoyv.widget.dialog.UiOptionsDialog
import com.xiaoyv.widget.utils.isSoftInputModeAlwaysVisible
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.IOException

class MainActivity :
    BaseMvpBindingActivity<ActivityMainBinding, MainContract.View, MainPresenter>(),
    MainContract.View {

    private val tbsApk by lazy { PathUtils.getExternalAppFilesPath() + "/tbs/tbs.apk" }

    override fun createPresenter() = MainPresenter()

    override fun createContentBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    @SuppressLint("NewApi")
    override fun initView() {
        stateController.fitTitleAndStatusBar = true


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

    @SuppressLint("MissingPermission")
    override fun initListener() {
        val accountName = "Test Account"
        val calendarAccount = CalendarAccount(
            name = "reminder",
            accountName = accountName,
            displayName = "Test Account Display Name"
        )

        binding.checkCalendar.setOnClickListener {
            val account = CalendarReminder.checkCalendarAccount(this, accountName)
            ToastUtils.showShort("ID: $account")
        }
        binding.addCalendar.setOnClickListener {
            val account = CalendarReminder.createCalendarAccount(this, calendarAccount)
            ToastUtils.showShort("ID: $account")
        }
        binding.addEvent.setOnClickListener {
            val eventId = CalendarReminder.createCalendarEvent(this, 9, CalendarEvent().apply {
                this.eventName = "TestEvent"
                this.startTime = System.currentTimeMillis() + 30000
                this.endTime = System.currentTimeMillis() + 60000
                this.description = "TestEvent Desc"
                this.eventLocation = "TestEvent Location"
            })
            ToastUtils.showShort("eventId: $eventId")
        }
        binding.deleteEvent.setOnClickListener {
            CalendarReminder.deleteCalendarEvent(this, 2)
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

//            val alertDialog = android.app.AlertDialog.Builder(this)
            val alertDialog = AlertDialog.Builder(this)
                .setMessage("网页请求，是否允许？")
                .setPositiveButton("允许") { _, _ ->
//            val url = "https://www.bilibili.com"
//            val url = "https://atrust.yangtzeu.edu.cn:4443"
//            val url = "https://portal.qiniu.com/kodo/overview"
                    val url = "https://212.129.249.109"

                    ActivityUtils.startActivity(
                        bundleOf(
                            "webUrl" to url
                        ), WebActivity::class.java
                    )
                }
                .setNegativeButton("取消", null)
                .create()
            alertDialog.show()
//            alertDialog.fitWindowWidth()
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

    override fun onPresenterCreated() {
        presenter.v2pPrint()
    }

    override fun p2vShowInfo() {

    }

    override fun p2vClickStatusView(stateView: StateView, view: View) {

    }
}