package com.qandeelabbassi.musicplayer.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

interface Player<T> : LifecycleObserver {
    enum class State {
        PLAYING,
        PAUSED,
        STOPPED
    }

    val state: State

    fun play(track: T)

    fun resume()

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop()

    fun toggle()
}