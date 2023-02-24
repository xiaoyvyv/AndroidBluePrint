package com.xiaoyv.blueprint.entity

/**
 * LoadingState
 *
 * @author why
 * @since 2023/2/24
 */
sealed class LoadingState {
    object Starting : LoadingState()
    object Ending : LoadingState()
}
