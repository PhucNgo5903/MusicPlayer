package com.example.musicplayer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Song

// Thêm tham số thứ 2: onRemoveClick (có thể null)
class SongAdapter(
    private val onSongClick: (Song) -> Unit,
    private val onRemoveClick: ((Song) -> Unit)? = null
) : ListAdapter<Song, SongAdapter.SongViewHolder>(SongDiffCallback()) {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemoveFav) // Ánh xạ nút xóa
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

        // Logic ảnh
        val baseUrl = "https://storage.googleapis.com/automotive-media/"
        val fullImageUrl = if (song.coverUrl.startsWith("http")) song.coverUrl else baseUrl + song.coverUrl

        Glide.with(holder.itemView.context)
            .load(fullImageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(holder.ivCover)

        // --- XỬ LÝ NÚT XÓA ---
        if (onRemoveClick != null) {
            // Nếu có truyền hàm xóa -> Hiện nút và bắt sự kiện
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnRemove.setOnClickListener {
                onRemoveClick.invoke(song)
            }
        } else {
            // Nếu không truyền (ListSongFragment) -> Ẩn nút đi
            holder.btnRemove.visibility = View.GONE
        }

        // Click vào cả dòng -> Phát nhạc
        holder.itemView.setOnClickListener {
            onSongClick(song)
        }
    }

    class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem == newItem
    }
}