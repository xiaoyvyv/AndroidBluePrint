package com.xiaoyv.widget.kts

import android.content.Context
import android.content.res.Configuration
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils


fun workDirOf(sub: String): String {
    val target = PathUtils.getFilesPathExternalFirst() + "/$sub"
    FileUtils.createOrExistsDir(target)
    return target
}

fun cacheDirOf(sub: String): String {
    val target = PathUtils.getCachePathExternalFirst() + "/$sub"
    FileUtils.createOrExistsDir(target)
    return target
}

fun isTablet(context: Context): Boolean {
    return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}
