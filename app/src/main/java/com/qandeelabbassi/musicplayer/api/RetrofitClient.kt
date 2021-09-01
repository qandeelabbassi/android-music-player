package com.qandeelabbassi.musicplayer.api

import com.qandeelabbassi.musicplayer.util.Const
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Const.API.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    fun getInstance(): Retrofit {
        return retrofit
    }
}