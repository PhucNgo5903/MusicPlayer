package com.example.musicplayer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.ui.viewmodel.MusicViewModel

class PlayerFragment : Fragment() {

    // Dùng chung ViewModel với ListSongFragment để biết bài nào đang được chọn
    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ánh xạ View
        val tvTitle = view.findViewById<TextView>(R.id.tvPlayerTitle)
        val tvArtist = view.findViewById<TextView>(R.id.tvPlayerArtist)
        val ivCover = view.findViewById<ImageView>(R.id.ivPlayerCover)
        val btnPlayPause = view.findViewById<ImageButton>(R.id.btnPlayPause)
        val btnDownload = view.findViewById<ImageButton>(R.id.btnDownload)

        // 2. Xử lý sự kiện bấm nút Download
        btnDownload.setOnClickListener {
            viewModel.downloadCurrentSong()
        }

        // 3. Quan sát bài hát đang phát -> Cập nhật UI
        viewModel.currentSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                tvTitle.text = it.title
                tvArtist.text = it.artist

                // --- A. XỬ LÝ HIỆN ẢNH (Nối link nếu thiếu) ---
                val baseUrl = "https://storage.googleapis.com/automotive-media/"
                val fullImageUrl = if (it.coverUrl.startsWith("http")) {
                    it.coverUrl
                } else {
                    baseUrl + it.coverUrl
                }

                Glide.with(this)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivCover)

                // --- B. XỬ LÝ TRẠNG THÁI NÚT DOWNLOAD (Đã tải hay chưa?) ---
                // Đây là đoạn bạn còn thiếu:
                if (it.localPath != null) {
                    // Nếu đã có đường dẫn local -> Đã tải xong
                    // Làm mờ nút đi và không cho bấm nữa
                    btnDownload.alpha = 0.5f
                    btnDownload.isEnabled = false
                } else {
                    // Chưa tải -> Hiển thị rõ và cho phép bấm
                    btnDownload.alpha = 1.0f
                    btnDownload.isEnabled = true
                }
            }
        }

        // 4. Quan sát trạng thái Play/Pause -> Đổi icon nút bấm
        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            if (isPlaying) {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
            }
        }

        // 5. Sự kiện bấm nút Play/Pause
        btnPlayPause.setOnClickListener {
            viewModel.togglePlayPause()
        }
    }
}