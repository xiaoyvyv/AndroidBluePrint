package com.xiaoyv.floater.widget.listener

import android.media.MediaPlayer
import android.widget.MediaController
import com.xiaoyv.floater.widget.kts.orEmpty

/**
 * SimpleTextureMediaPlayerControl
 *
 * @author why
 * @since 2023/3/12
 */
abstract class SimpleTextureMediaPlayerControl : MediaController.MediaPlayerControl {

    abstract val player: MediaPlayer?

    override fun start() {
        player?.start()
    }

    override fun pause() {
        player?.pause()
    }

    override fun getDuration(): Int {
        return player?.duration.orEmpty()
    }

    override fun getCurrentPosition(): Int {
        return player?.currentPosition.orEmpty()
    }

    override fun seekTo(pos: Int) {
        player?.seekTo(pos.toLong(), MediaPlayer.SEEK_CLOSEST_SYNC)
    }

    override fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }

    override fun canPause(): Boolean {
        return isPlaying
    }

    override fun canSeekBackward(): Boolean {
        return false
    }

    override fun canSeekForward(): Boolean {
        return false
    }

    override fun getAudioSessionId(): Int {
        return player?.audioSessionId.orEmpty()
    }
}