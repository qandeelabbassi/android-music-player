package com.qandeelabbassi.musicplayer

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qandeelabbassi.musicplayer.api.ApiInterface
import com.qandeelabbassi.musicplayer.api.ApiResponse
import com.qandeelabbassi.musicplayer.api.MusicRepository
import com.qandeelabbassi.musicplayer.api.RetrofitClient
import com.qandeelabbassi.musicplayer.models.ControlsState
import com.qandeelabbassi.musicplayer.models.SearchResult
import com.qandeelabbassi.musicplayer.util.MusicPlayer
import com.qandeelabbassi.musicplayer.util.Player
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivityVM : ViewModel(), MusicPlayer.Listener {
    private val disposables = CompositeDisposable()
    private val searchLD: MutableLiveData<ApiResponse<SearchResult>> = MutableLiveData()
    private val playLD: MutableLiveData<ControlsState> = MutableLiveData()
    private val apiInterface: ApiInterface = RetrofitClient.getInstance().create(ApiInterface::class.java)
    private val repository: MusicRepository = MusicRepository.getInstance(apiInterface)

    fun playState() = playLD

    fun searchMusicByArtist(name: String): MutableLiveData<ApiResponse<SearchResult>> {
        disposables.add(repository.searchByArtist(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .doOnSubscribe { searchLD.value = ApiResponse.Loading() }
                .subscribe({ result ->
                    val body = result.body()
                    if (result.isSuccessful && body != null) {
                        searchLD.postValue(ApiResponse.Success(body))
                    } else {
                        searchLD.postValue(ApiResponse.Error("No results found!"))
                    }
                }, { throwable ->
                    searchLD.postValue(ApiResponse.Error(throwable.message))
                })
        )
        return searchLD
    }

    override fun onStateChanged(state: Player.State) {
        when (state) {
            Player.State.PLAYING -> playLD.value = ControlsState(R.drawable.ic_pause, View.VISIBLE)
            Player.State.PAUSED -> playLD.value = ControlsState(R.drawable.ic_play, View.VISIBLE)
            Player.State.STOPPED -> playLD.value = ControlsState(R.drawable.ic_play, View.GONE)
        }
    }
}