package com.qandeelabbassi.musicplayer.api

import com.qandeelabbassi.musicplayer.models.SearchResult
import io.reactivex.Observable
import retrofit2.Response


class MusicRepository private constructor(
        private val musicApi: ApiInterface
) {
    companion object {
        @Volatile
        private var INSTANCE: MusicRepository? = null

        fun getInstance(api: ApiInterface): MusicRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicRepository(api).also { INSTANCE = it }
            }
        }
    }

    fun searchByArtist(name: String): Observable<Response<SearchResult>> {
        return musicApi.searchMusic(name)
    }
}