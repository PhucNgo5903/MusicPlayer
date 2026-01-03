package com.example.musicplayer.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView // Import cái này
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ui.adapter.SongAdapter
import com.example.musicplayer.ui.viewmodel.MusicViewModel

class ListSongFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()
    private lateinit var adapter: SongAdapter

    // Biến lưu danh sách gốc để khi xóa chữ thì hiện lại đầy đủ
    private var fullSongList: List<Song> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_song, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvSongs = view.findViewById<RecyclerView>(R.id.rvSongs)
        val searchView = view.findViewById<SearchView>(R.id.searchView) // Ánh xạ Search
        val progressBar = view.findViewById<View>(R.id.progressBar)
        val tvEmpty = view.findViewById<View>(R.id.tvEmpty)

        // Setup Adapter
        adapter = SongAdapter(onSongClick = { song ->
            viewModel.selectSong(song)
            try {
                findNavController().navigate(R.id.action_list_to_player)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        rvSongs.adapter = adapter
        rvSongs.layoutManager = LinearLayoutManager(context)

        // 1. Quan sát dữ liệu từ ViewModel
        viewModel.playlist.observe(viewLifecycleOwner) { songs ->
            if (songs.isNullOrEmpty()) {
                progressBar.visibility = View.VISIBLE
                rvSongs.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                rvSongs.visibility = View.VISIBLE

                // LƯU DỮ LIỆU GỐC VÀO BIẾN
                fullSongList = songs

                // Ban đầu hiển thị tất cả
                adapter.submitList(songs)
            }
        }

        // 2. Xử lý Tìm kiếm
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Khi bấm nút Enter trên bàn phím
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterSongs(query)
                return true
            }

            // Khi gõ từng chữ (Real-time search)
            override fun onQueryTextChange(newText: String?): Boolean {
                filterSongs(newText)
                return true
            }
        })
    }

    // Hàm lọc danh sách
    private fun filterSongs(query: String?) {
        val tvEmpty = view?.findViewById<TextView>(R.id.tvEmpty)

        if (query.isNullOrEmpty()) {
            // Nếu không nhập gì -> Hiện lại danh sách gốc
            adapter.submitList(fullSongList)
            tvEmpty?.visibility = View.GONE
            return
        }

        // Lọc theo Tên bài hát HOẶC Tên ca sĩ (không phân biệt hoa thường)
        val filteredList = fullSongList.filter { song ->
            song.title.contains(query, ignoreCase = true) ||
                    song.artist.contains(query, ignoreCase = true)
        }

        if (filteredList.isEmpty()) {
            tvEmpty?.text = "Không tìm thấy kết quả cho '$query'"
            tvEmpty?.visibility = View.VISIBLE
        } else {
            tvEmpty?.visibility = View.GONE
        }

        // Cập nhật Adapter với list mới
        adapter.submitList(filteredList)
    }
}