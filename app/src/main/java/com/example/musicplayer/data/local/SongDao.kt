package com.example.musicplayer.data.local

import androidx.lifecycle.LiveData
import androidx.room.* // Import hết cho gọn
import com.example.musicplayer.data.model.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): LiveData<List<Song>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<Song>)

    // --- THÊM CÁC HÀM DƯỚI ĐÂY ---

    // 1. Lấy bài hát theo ID (để Worker cập nhật)
    @Query("SELECT * FROM songs WHERE id = :id LIMIT 1")
    fun getSongById(id: Int): Song?

    // 2. Cập nhật thông tin bài hát (lưu đường dẫn localPath)
    @Update
    fun update(song: Song)

    // Lấy danh sách yêu thích (những bài đã tải)
    @Query("SELECT * FROM songs WHERE localPath IS NOT NULL")
    fun getFavoriteSongs(): LiveData<List<Song>>
}
