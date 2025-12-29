package com.example.musicplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @Json(name = "title") val title: String,
    @Json(name = "artist") val artist: String,
    @Json(name = "source") val sourceUrl: String,
    @Json(name = "image") val coverUrl: String,

    // --- MỚI: Đường dẫn file trong máy (null = chưa tải) ---
    var localPath: String? = null
)