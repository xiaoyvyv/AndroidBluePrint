package com.xiaoyv.blueprint

import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        // 20230619T180000Z
        val format = SimpleDateFormat("yyyyMMdd'T'HHmmssZ")
        val formatNormal = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        println("fromUtc: " +format.format(Date()))

        val date = format.parse("20230619T180000Z")
        println("fromUtc: " +formatNormal.format(date))
    }
}