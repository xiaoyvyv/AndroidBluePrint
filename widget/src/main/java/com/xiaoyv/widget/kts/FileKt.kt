package com.xiaoyv.widget.kts

import com.blankj.utilcode.util.FileUtils

/**
 * FileKt
 *
 * @author why
 * @since 2023/3/12
 */
fun String.mkdirs(): String {
    if (FileUtils.isFile(this)) {
        FileUtils.delete(this)
    }
    FileUtils.createOrExistsDir(this)
    FileUtils.deleteAllInDir(this)
    return this
}