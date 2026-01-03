package com.example.musicplayer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.ui.adapter.SongAdapter
import com.example.musicplayer.ui.viewmodel.MusicViewModel

class FavoriteFragment : Fragment() {

    // Dùng chung ViewModel để chia sẻ dữ liệu
    private val viewModel: MusicViewModel by activityViewModels()
    private lateinit var adapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvFavorites = view.findViewById<RecyclerView>(R.id.rvFavorites)
        val tvNoData = view.findViewById<TextView>(R.id.tvNoData)

        // Cập nhật Adapter với 2 hàm callback:
        // 1. Click bài hát -> Phát nhạc
        // 2. Click nút xóa -> Xóa khỏi favorite
        adapter = SongAdapter(
            onSongClick = { song ->
                viewModel.selectSong(song)
                findNavController().navigate(R.id.action_favorite_to_player)
            },
            onRemoveClick = { song ->
                // Hỏi xác nhận trước khi xóa (Optional)
                viewModel.removeFromFavorite(song)
            }
        )
        rvFavorites.adapter = adapter

        // Quan sát danh sách yêu thích (Offline)
        viewModel.loadFavorites().observe(viewLifecycleOwner) { songs ->
            if (songs.isNullOrEmpty()) {
                tvNoData.visibility = View.VISIBLE
                rvFavorites.visibility = View.GONE
            } else {
                tvNoData.visibility = View.GONE
                rvFavorites.visibility = View.VISIBLE
                adapter.submitList(songs)
            }
        }
    }
}