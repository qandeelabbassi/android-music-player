package com.qandeelabbassi.musicplayer.models

import android.view.View
import androidx.annotation.DrawableRes

class ControlsState(
        @DrawableRes val playIcon: Int,
        val visibility: Int = View.GONE
)