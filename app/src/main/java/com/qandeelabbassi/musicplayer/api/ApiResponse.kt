package com.qandeelabbassi.musicplayer.api

sealed class ApiResponse<T : Any> {
    data class Success<T : Any>(val data: T) : ApiResponse<T>()
    data class Error<T : Any>(val message: String?) : ApiResponse<T>()
    class Loading<T : Any> : ApiResponse<T>()
}