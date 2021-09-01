package com.qandeelabbassi.musicplayer.util

import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.qandeelabbassi.musicplayer.models.Track
import java.lang.IllegalStateException

class MusicPlayer(
        private val lifecycle: Lifecycle,
        private val listener: Listener?
) : Player<Track>, LifecycleObserver {
    interface Listener {
        fun onStateChanged(state: Player.State)
    }

    private val player: MediaPlayer = MediaPlayer()
    private var currentState = Player.State.STOPPED
    private var pausePosition = -1

    init {
        lifecycle.addObserver(this)
    }

    override val state: Player.State
        get() = currentState

    override fun play(track: Track) {
        try {
            setState(Player.State.PLAYING)
            player.reset()
            player.setDataSource(track.previewUrl)
            player.prepareAsync()
            player.setOnPreparedListener(OnPreparedListener { player ->
                if (currentState == Player.State.STOPPED) return@OnPreparedListener
                player.start()
            })
        } catch (e: IllegalStateException) {
            Log.d("test", "player not ready")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun pause() {
        if (player.isPlaying) {
            pausePosition = player.currentPosition
            player.pause()
            setState(Player.State.PAUSED)
        }
    }

    override fun resume() {
        if (pausePosition != -1) {
            player.seekTo(pausePosition)
            player.start()
            setState(Player.State.PLAYING)
        }
    }

    override fun toggle() {
        if (state == Player.State.PAUSED)
            resume()
        else if (state == Player.State.PLAYING)
            pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun stop() {
        try {
            player.stop()
            if (lifecycle.currentState == Lifecycle.State.DESTROYED)
                player.release()
        } catch (e: IllegalStateException) {
            Log.d("test", "player not ready")
        }
        pausePosition = -1
        setState(Player.State.STOPPED)
    }

    private fun setState(state: Player.State) {
        currentState = state
        listener?.onStateChanged(state)
    }
}