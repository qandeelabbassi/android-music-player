package com.qandeelabbassi.musicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.qandeelabbassi.musicplayer.models.Track

class TrackRecyclerAdapter(
        private val context: Context,
        private val listener: TrackActionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun interface TrackActionListener {
        fun onTrackSelected(pos: Int, track: Track)
    }

    private val tracks = mutableListOf<Track>()
    private val glide = Glide.with(context)
    private var selectedIndex = -1

    fun setTracks(list: List<Track>) {
        selectedIndex = -1
        tracks.clear()
        tracks.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_track_list, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val trackVH = holder as TrackViewHolder
        val track = tracks[position]
        trackVH.txtTrackNumber.text = (position + 1).toString()
        trackVH.txtTrackName.text = track.trackName
        trackVH.txtArtistName.text = track.artistName
        trackVH.txtAlbumName.text = track.collectionName
        trackVH.imgActive.visibility = if (selectedIndex == position) View.VISIBLE else View.GONE
        glide.load(track.artworkUrl100)
                .transform(CenterCrop(), RoundedCorners(context.resources.getDimension(R.dimen.track_img_radius).toInt()))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(trackVH.imgTrack)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    inner class TrackViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        val imgTrack: ImageView = v.findViewById(R.id.img_track_album)
        val imgActive: ImageView = v.findViewById(R.id.img_active_state)
        val txtTrackNumber: TextView = v.findViewById(R.id.txt_track_number)
        val txtTrackName: TextView = v.findViewById(R.id.txt_track_name)
        val txtArtistName: TextView = v.findViewById(R.id.txt_artist_name)
        val txtAlbumName: TextView = v.findViewById(R.id.txt_album_name)

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val pos = adapterPosition
            if (pos != -1) {
                listener.onTrackSelected(pos, tracks[pos])
                if (selectedIndex != -1)
                    notifyItemChanged(selectedIndex)
                selectedIndex = pos
                notifyItemChanged(selectedIndex)
            }
        }
    }
}