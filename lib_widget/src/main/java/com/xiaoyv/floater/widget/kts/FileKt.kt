package com.xiaoyv.floater.widget.kts

import com.blankj.utilcode.util.FileUtils

/**
 * FileKt
 *
 * @author why
 * @since 2023/3/12
 */
fun String.mkdirs(deleteAllInDir: Boolean = false): String {
    if (FileUtils.isFile(this)) {
        FileUtils.delete(this)
    }
    FileUtils.createOrExistsDir(this)
    if (deleteAllInDir) {
        FileUtils.deleteAllInDir(this)
    }
    return this
}