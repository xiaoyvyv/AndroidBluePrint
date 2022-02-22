package com.xiaoyv.blueprint.base.rxjava.event

import androidx.annotation.Keep
import java.io.Serializable

/**
 * RxEvent
 *
 * @author why
 * @since 2022/2/10
 */
@Keep
class RxEvent : Serializable {
    /**
     * 携带的元数据
     */
    var dataBoolean: Boolean = false
    var dataInt: Int = 0
    var dataLong: Long = 0
    var dataFloat: Float = 0f
    var dataDouble: Double = 0.0

    var dataSerializable: Serializable? = null
}