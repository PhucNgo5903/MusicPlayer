package com.example.musicplayer.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.ui.viewmodel.MusicViewModel
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()

    // Handler để cập nhật SeekBar mỗi giây
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ánh xạ View
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val tvTitle = view.findViewById<TextView>(R.id.tvPlayerTitle)
        val tvArtist = view.findViewById<TextView>(R.id.tvPlayerArtist)
        val ivCover = view.findViewById<ImageView>(R.id.ivPlayerCover)

        val btnFavoriteDownload = view.findViewById<ImageButton>(R.id.btnFavoriteDownload)

        val btnPlayPause = view.findViewById<ImageButton>(R.id.btnPlayPause)
        val btnPrevious = view.findViewById<ImageButton>(R.id.btnPrevious)
        val btnNext = view.findViewById<ImageButton>(R.id.btnNext)

        val sbProgress = view.findViewById<SeekBar>(R.id.sbProgress)
        val tvCurrentTime = view.findViewById<TextView>(R.id.tvCurrentTime)
        val tvTotalTime = view.findViewById<TextView>(R.id.tvTotalTime)

        // Nhớ thêm ID này vào XML nhé: android:id="@+id/btnVolume"
        val btnVolume = view.findViewById<ImageView>(R.id.btnVolume)

        // 2. Xử lý sự kiện nút bấm cơ bản
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnFavoriteDownload.setOnClickListener {
            viewModel.downloadCurrentSong()
        }

        btnPlayPause.setOnClickListener {
            viewModel.togglePlayPause()
        }

        // 3. Xử lý Next / Previous
        btnNext.setOnClickListener {
            viewModel.playNext()
        }

        btnPrevious.setOnClickListener {
            viewModel.playPrevious()
        }

        // 4. Xử lý nút Volume (Tắt/Bật tiếng)
        btnVolume.setOnClickListener {
            val isMuted = viewModel.toggleMute()
            if (isMuted) {
                btnVolume.alpha = 0.5f // Làm mờ nút khi tắt tiếng
                Toast.makeText(context, "Đã tắt tiếng", Toast.LENGTH_SHORT).show()
            } else {
                btnVolume.alpha = 1.0f // Sáng rõ khi bật tiếng
                Toast.makeText(context, "Đã bật tiếng", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Xử lý SeekBar (Tua nhạc)
        sbProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Khi người dùng đang kéo, cập nhật số giây hiển thị ngay lập tức
                if (fromUser) {
                    tvCurrentTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Khi bắt đầu kéo, có thể tạm dừng update tự động (tùy chọn)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Khi thả tay ra -> Tua đến đoạn đó
                seekBar?.let {
                    viewModel.seekTo(it.progress)
                }
            }
        })

        // 6. Runnable để cập nhật SeekBar tự động theo nhạc
        runnable = object : Runnable {
            override fun run() {
                val currentPosition = viewModel.getCurrentPosition()
                val totalDuration = viewModel.getDuration()

                // Cập nhật max và progress của SeekBar
                sbProgress.max = totalDuration

                // Chỉ update progress nếu người dùng không đang giữ tay kéo (tránh giật)
                // Tuy nhiên ở mức cơ bản, update thẳng cũng được:
                sbProgress.progress = currentPosition

                // Cập nhật text thời gian
                tvCurrentTime.text = formatTime(currentPosition)
                tvTotalTime.text = formatTime(totalDuration)

                // Lặp lại sau 1 giây (1000ms)
                handler.postDelayed(this, 1000)
            }
        }
        // Kích hoạt vòng lặp
        handler.post(runnable)


        // 7. Quan sát dữ liệu bài hát
        viewModel.currentSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                tvTitle.text = it.title
                tvArtist.text = it.artist

                val baseUrl = "https://storage.googleapis.com/automotive-media/"
                val fullImageUrl = if (it.coverUrl.startsWith("http")) it.coverUrl else baseUrl + it.coverUrl

                Glide.with(this)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(24)))
                    .into(ivCover)

                // Cập nhật trạng thái nút Tim
                if (it.localPath != null) {
                    btnFavoriteDownload.setImageResource(R.drawable.ic_heart_filled)
                    btnFavoriteDownload.isEnabled = false
                } else {
                    btnFavoriteDownload.setImageResource(R.drawable.ic_heart_border)
                    btnFavoriteDownload.isEnabled = true
                }
            }
        }

        // 8. Quan sát trạng thái Play/Pause
        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            if (isPlaying) {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
            }
            btnPlayPause.setColorFilter(android.graphics.Color.BLACK)
        }
    }

    // Hàm phụ trợ: Đổi mili-giây sang phút:giây (03:45)
    private fun formatTime(millis: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) -
                TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Dừng cập nhật SeekBar khi thoát màn hình để tránh rò rỉ bộ nhớ
        handler.removeCallbacks(runnable)
    }
}