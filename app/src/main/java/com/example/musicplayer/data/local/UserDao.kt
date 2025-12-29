package com.example.musicplayer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.musicplayer.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun register(user: User)

    @Query("SELECT * FROM users WHERE username = :u AND password = :p LIMIT 1")
    suspend fun login(u: String, p: String): User?

    @Query("SELECT * FROM users WHERE username = :u LIMIT 1")
    suspend fun checkUserExist(u: String): User?
}