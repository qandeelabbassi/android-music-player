package com.qandeelabbassi.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.qandeelabbassi.musicplayer.api.ApiResponse
import com.qandeelabbassi.musicplayer.databinding.ActivityMainBinding
import com.qandeelabbassi.musicplayer.models.SearchResult
import com.qandeelabbassi.musicplayer.ui.SearchView
import com.qandeelabbassi.musicplayer.util.MusicPlayer
import com.qandeelabbassi.musicplayer.util.Player

class MainActivity : AppCompatActivity(), SearchView.SearchListener {
    private val activityVM: MainActivityVM by viewModels()
    private lateinit var player: MusicPlayer
    private lateinit var binding: ActivityMainBinding
    private var tracksAdapter: TrackRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = MusicPlayer(lifecycle, activityVM)
        binding = setContentView(this, R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        binding.svMusic.setSearchQueryListener(this)
        binding.emptyText = getString(R.string.empty_text)
        binding.progressVisibility = View.GONE

        // recycler
        tracksAdapter = TrackRecyclerAdapter(this) { _, track ->
            player.play(track)
        }

        binding.recyclerTracks.adapter = tracksAdapter
        binding.recyclerTracks.layoutManager = LinearLayoutManager(this)

        // observers
        activityVM.playState().observe(this, {
            binding.layoutControls.visibility = it.visibility
            binding.btnPlayPause.setImageResource(it.playIcon)
        })
        binding.btnPlayPause.setOnClickListener { player.toggle() }
    }

    private fun consumeSearchResponse(response: ApiResponse<SearchResult>) {
        var progressVisibility = View.GONE
        when (response) {
            is ApiResponse.Loading -> progressVisibility = View.VISIBLE
            is ApiResponse.Error -> binding.emptyText = response.message
            is ApiResponse.Success -> {
                if (response.data.results.isEmpty())
                    binding.emptyText = getString(R.string.empty_search)
                else
                    binding.emptyText = ""
                tracksAdapter?.setTracks(response.data.results)
            }
        }
        binding.progressVisibility = progressVisibility
    }

    override fun onQueryChanged(query: String) {
    }

    override fun onActionClicked(query: String) {
        player.stop()
        activityVM.searchMusicByArtist(query).observe(this, this::consumeSearchResponse)
    }
}