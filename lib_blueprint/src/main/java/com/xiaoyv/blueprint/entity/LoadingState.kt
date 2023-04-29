@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.blueprint.entity

import androidx.annotation.IntDef

/**
 * LoadingState
 *
 * @author why
 * @since 2023/2/24
 */
data class LoadingState(val type: Int, val error: Throwable? = null) {

    @IntDef(STATE_STARTING, STATE_ENDING)
    @Retention(AnnotationRetention.SOURCE)
    annotation class State

    companion object {
        const val STATE_STARTING = 1
        const val STATE_ENDING = 2
    }
}

fun loadingStateOfStarting() = LoadingState(LoadingState.STATE_STARTING)
fun loadingStateOfEnding(error: Throwable? = null) = LoadingState(LoadingState.STATE_ENDING, error)