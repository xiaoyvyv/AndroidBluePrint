package com.xiaoyv.blueprint.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.*
import com.github.nukc.stateview.StateView
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

class MainActivity :
    BaseMvpBindingActivity<ActivityMainBinding, MainContract.View, MainPresenter>(),
    MainContract.View {

    override fun createPresenter() = MainPresenter()

    override fun createContentBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    @SuppressLint("NewApi")
    override fun initView() {
        stateController.fitTitleAndStatusBar = true


//        binding.toolbar.setLeftIcon()
        binding.toolbar.bottomDivider = true

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

        binding.tvTest1.setOnFastLimitClickListener { view, b ->
            ActivityUtils.startActivity(WebActivity::class.java)
        }

        binding.tvTest.setOnFastLimitClickListener(2500) { view, b ->
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