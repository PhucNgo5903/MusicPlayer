package com.example.musicplayer.ui.viewmodel

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.local.MusicDatabase
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repository.MusicRepository
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MusicRepository
    val playlist: LiveData<List<Song>>

    // --- THÊM MỚI ---
    val favoritePlaylist: LiveData<List<Song>>

    private val _currentSong = MutableLiveData<Song?>()
    val currentSong: LiveData<Song?> = _currentSong

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private var mediaPlayer: MediaPlayer? = null

    init {
        val songDao = MusicDatabase.getDatabase(application).songDao()
        repository = MusicRepository(songDao, application.applicationContext)

        playlist = repository.allSongs

        // --- Gán dữ liệu ---
        favoritePlaylist = repository.favoriteSongs

        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            repository.refreshSongsFromNetwork()
        }
    }

    fun selectSong(song: Song) {
        _currentSong.value = song
        playMusic(song)
    }

    fun downloadCurrentSong() {
        val song = _currentSong.value ?: return
        if (song.localPath != null) {
            Toast.makeText(getApplication(), "Bài này đã tải rồi!", Toast.LENGTH_SHORT).show()
            return
        }
        repository.downloadSong(song)
        Toast.makeText(getApplication(), "Đang tải xuống...", Toast.LENGTH_SHORT).show()
    }

    private fun playMusic(song: Song) {
        val finalSource: String
        if (song.localPath != null) {
            finalSource = song.localPath!!
        } else {
            val baseUrl = "https://storage.googleapis.com/automotive-media/"
            finalSource = if (song.sourceUrl.startsWith("http")) song.sourceUrl else baseUrl + song.sourceUrl
        }

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(getApplication(), Uri.parse(finalSource))
                prepareAsync()
                setOnPreparedListener {
                    start()
                    _isPlaying.value = true
                }
                setOnCompletionListener { _isPlaying.value = false }
                setOnErrorListener { _, _, _ -> true }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}