package com.example.musicplayer.ui.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
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

    private val _currentSong = MutableLiveData<Song?>()
    val currentSong: LiveData<Song?> = _currentSong

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private var mediaPlayer: MediaPlayer? = null

    // Biến lưu tên người dùng hiện tại
    private var currentUsername: String = ""

    private var isMuted = false
    init {
        val db = MusicDatabase.getDatabase(application)
        val songDao = db.songDao()
        // 1. Lấy thêm FavoriteDao
        val favDao = db.favoriteDao()

        // 2. Lấy Username từ SharedPreferences
        val prefs = application.getSharedPreferences("MusicAppPrefs", Context.MODE_PRIVATE)
        currentUsername = prefs.getString("USERNAME", "") ?: ""

        // 3. Khởi tạo Repository với cả 2 DAO
        repository = MusicRepository(songDao, favDao, application.applicationContext)

        playlist = repository.allSongs

        fetchData()
    }

    // --- HÀM MỚI: Lấy danh sách yêu thích của User hiện tại ---
    fun loadFavorites(): LiveData<List<Song>> {
        return repository.getFavoritesByUsername(currentUsername)
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

    // --- CẬP NHẬT: Tải nhạc kèm theo Username ---
    fun downloadCurrentSong() {
        val song = _currentSong.value ?: return

        // Kiểm tra đăng nhập
        if (currentUsername.isEmpty()) {
            Toast.makeText(getApplication(), "Vui lòng đăng nhập để tải nhạc!", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra xem đã tải chưa (dựa vào đường dẫn file)
        if (song.localPath != null) {
            Toast.makeText(getApplication(), "Bài này đã có trong máy!", Toast.LENGTH_SHORT).show()
            // Logic mở rộng: Nếu muốn thêm vào Favorite của user khác dù file đã có,
            // bạn có thể gọi repository.addFavoriteOnly(song, username) - tuỳ nhu cầu.
            return
        }

        // Gọi Repository tải nhạc cho User này
        repository.downloadSong(song, currentUsername)
        Toast.makeText(getApplication(), "Đang thêm vào thư viện của $currentUsername...", Toast.LENGTH_SHORT).show()
    }

    fun playNext() {
        val list = playlist.value ?: return
        val current = _currentSong.value ?: return
        val currentIndex = list.indexOfFirst { it.id == current.id }

        if (currentIndex != -1) {
            // Nếu là bài cuối cùng thì quay về bài đầu (Wrap around), hoặc dừng lại tùy bạn
            val nextIndex = if (currentIndex < list.size - 1) currentIndex + 1 else 0
            selectSong(list[nextIndex])
        }
    }

    // 2. Chức năng Previous (Bài trước đó)
    fun playPrevious() {
        val list = playlist.value ?: return
        val current = _currentSong.value ?: return
        val currentIndex = list.indexOfFirst { it.id == current.id }

        if (currentIndex != -1) {
            // Nếu là bài đầu tiên thì quay về bài cuối
            val prevIndex = if (currentIndex > 0) currentIndex - 1 else list.size - 1
            selectSong(list[prevIndex])
        }
    }

    // 3. Chức năng Tua nhạc (Seek)
    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    // 4. Lấy thời gian hiện tại và tổng thời gian (để cập nhật SeekBar)
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    fun getDuration(): Int = mediaPlayer?.duration ?: 0

    // 5. Chức năng Volume (Ở đây làm đơn giản là Mute/Unmute)
    fun toggleMute(): Boolean {
        isMuted = !isMuted
        if (isMuted) {
            mediaPlayer?.setVolume(0f, 0f) // Tắt tiếng
        } else {
            mediaPlayer?.setVolume(1.0f, 1.0f) // Bật tiếng
        }
        return isMuted
    }

    private fun playMusic(song: Song) {
        val finalSource: String

        // Ưu tiên phát từ file Offline
        if (song.localPath != null) {
            finalSource = song.localPath!!
        } else {
            // Nếu chưa tải -> Phát Online
            val baseUrl = "https://storage.googleapis.com/automotive-media/"
            finalSource = if (song.sourceUrl.startsWith("http")) song.sourceUrl else baseUrl + song.sourceUrl
        }

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setVolume(if(isMuted) 0f else 1f, if(isMuted) 0f else 1f)
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
    fun removeFromFavorite(song: Song) {
        viewModelScope.launch {
            repository.removeSongFromFavorites(currentUsername, song.id)
            Toast.makeText(getApplication(), "Đã xóa khỏi thư viện", Toast.LENGTH_SHORT).show()
            // Vì LiveData 'favoritePlaylist' đang observe database,
            // nên khi xóa xong, list sẽ tự động cập nhật, không cần làm gì thêm.
        }
    }
}