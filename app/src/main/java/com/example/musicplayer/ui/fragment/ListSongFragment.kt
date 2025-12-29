package com.example.musicplayer.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView // Bỏ import Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.ui.adapter.SongAdapter
import com.example.musicplayer.ui.viewmodel.MusicViewModel

class ListSongFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()
    private lateinit var adapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_song, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvSongs = view.findViewById<RecyclerView>(R.id.rvSongs)
        val progressBar = view.findViewById<View>(R.id.progressBar)
        val tvEmpty = view.findViewById<View>(R.id.tvEmpty)

        // --- ĐÃ XÓA ĐOẠN CODE XỬ LÝ NÚT FAVORITES Ở ĐÂY ---

        adapter = SongAdapter { song ->
            viewModel.selectSong(song)
            try {
                findNavController().navigate(R.id.action_list_to_player)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        rvSongs.adapter = adapter
        rvSongs.layoutManager = LinearLayoutManager(context)

        viewModel.playlist.observe(viewLifecycleOwner) { songs ->
            Log.d("ListSongFragment", "Dữ liệu về: ${songs.size} bài hát")

            if (songs.isNullOrEmpty()) {
                progressBar.visibility = View.VISIBLE
                tvEmpty.visibility = View.VISIBLE
                rvSongs.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                tvEmpty.visibility = View.GONE
                rvSongs.visibility = View.VISIBLE
                adapter.submitList(songs)
            }
        }
    }
}