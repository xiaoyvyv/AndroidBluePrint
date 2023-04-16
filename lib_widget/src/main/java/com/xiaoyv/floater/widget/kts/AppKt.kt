package com.xiaoyv.floater.widget.kts

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils

fun workDirOf(sub: String): String {
    val target = PathUtils.getFilesPathExternalFirst() + "/$sub"
    FileUtils.createOrExistsDir(target)
    return target
}