package com.example.musicplayer.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.model.Favorite
import com.example.musicplayer.data.model.Song

@Dao
interface FavoriteDao {
    // Thêm bài vào danh sách yêu thích của user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    // Kiểm tra xem user đã thích bài này chưa
    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE username = :user AND songId = :songId)")
    suspend fun isFavorite(user: String, songId: Int): Boolean

    // LẤY DANH SÁCH BÀI HÁT THEO USER (Quan trọng nhất)
    // Kết hợp bảng songs và favorites
    @Query("""
        SELECT songs.* FROM songs 
        INNER JOIN favorites ON songs.id = favorites.songId 
        WHERE favorites.username = :user
    """)
    fun getFavoriteSongsByUser(user: String): LiveData<List<Song>>

    @Query("DELETE FROM favorites WHERE username = :username AND songId = :songId")
    suspend fun removeFavorite(username: String, songId: Int)
}