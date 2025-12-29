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

        // Tái sử dụng SongAdapter của màn hình List
        adapter = SongAdapter { song ->
            viewModel.selectSong(song) // Chọn bài
            findNavController().navigate(R.id.action_favorite_to_player) // Chuyển sang Player
        }
        rvFavorites.adapter = adapter

        // Quan sát danh sách yêu thích (Offline)
        viewModel.favoritePlaylist.observe(viewLifecycleOwner) { songs ->
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