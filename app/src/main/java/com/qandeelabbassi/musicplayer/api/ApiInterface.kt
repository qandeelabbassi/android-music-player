package com.qandeelabbassi.musicplayer.api

import com.qandeelabbassi.musicplayer.models.SearchResult
import com.qandeelabbassi.musicplayer.util.Const.API.SEARCH_MUSIC
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET(SEARCH_MUSIC)
    fun searchMusic(@Query("term") artistName: String): Observable<Response<SearchResult>>
}