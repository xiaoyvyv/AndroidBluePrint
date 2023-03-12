package com.xiaoyv.widget.kts

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.Utils
import kotlin.reflect.KClass

fun <T : Context> KClass<T>.createIntent(): Intent = Intent(Utils.getApp(), java)

