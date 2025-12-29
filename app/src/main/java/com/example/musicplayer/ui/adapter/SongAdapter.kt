package com.example.musicplayer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Song

class SongAdapter(private val onSongClick: (Song) -> Unit) :
    ListAdapter<Song, SongAdapter.SongViewHolder>(SongDiffCallback()) {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.tvTitle.text = song.title
        holder.tvArtist.text = song.artist

        // --- ĐOẠN CODE QUAN TRỌNG ĐỂ SỬA LỖI ẢNH ---
        val baseUrl = "https://storage.googleapis.com/automotive-media/"

        // Kiểm tra: Nếu link chưa có 'http' thì nối thêm baseUrl vào
        val fullImageUrl = if (song.coverUrl.startsWith("http")) {
            song.coverUrl
        } else {
            baseUrl + song.coverUrl
        }

        Glide.with(holder.itemView.context)
            .load(fullImageUrl) // Dùng link đầy đủ
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(holder.ivCover)
        // --------------------------------------------

        holder.itemView.setOnClickListener {
            onSongClick(song)
        }
    }

    class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
}