package com.example.musicplayer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.model.User

@Dao
interface UserDao {
    // Đăng ký: Thêm user mới
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: User)

    // Đăng nhập: Tìm user khớp User và Pass
    @Query("SELECT * FROM users WHERE username = :u AND password = :p LIMIT 1")
    suspend fun login(u: String, p: String): User?

    // Kiểm tra tồn tại: Xem username đã có chưa
    @Query("SELECT * FROM users WHERE username = :u LIMIT 1")
    suspend fun checkUserExist(u: String): User?
}