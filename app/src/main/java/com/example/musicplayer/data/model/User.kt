package com.example.musicplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String, // Trong thực tế nên mã hóa, bài tập thì để plain text cũng được
    val fullName: String
)