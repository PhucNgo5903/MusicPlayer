package com.example.musicplayer.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.musicplayer.data.local.SongDao
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.musicplayer.worker.DownloadWorker

class MusicRepository(private val songDao: SongDao, private val context: Context) {

    val allSongs: LiveData<List<Song>> = songDao.getAllSongs()
    val favoriteSongs: LiveData<List<Song>> = songDao.getFavoriteSongs()
    suspend fun refreshSongsFromNetwork() {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getPlaylist()
                val songsFromNetwork = response["music"]
                if (!songsFromNetwork.isNullOrEmpty()) {
                    songDao.insertAll(songsFromNetwork)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun downloadSong(song: Song) {
        // 1. Tạo link đầy đủ (vì Worker không biết tự nối chuỗi)
        val baseUrl = "https://storage.googleapis.com/automotive-media/"
        val fullUrl = if (song.sourceUrl.startsWith("http")) song.sourceUrl else baseUrl + song.sourceUrl

        // 2. Đóng gói dữ liệu gửi cho Worker
        val inputData = workDataOf(
            "SONG_ID" to song.id,
            "SONG_URL" to fullUrl
        )

        // 3. Tạo yêu cầu chạy 1 lần
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .build()

        // 4. Gửi yêu cầu
        WorkManager.getInstance(context).enqueue(downloadRequest)
        Log.d("MusicRepository", "Đã gửi yêu cầu tải bài: ${song.title}")
    }
}