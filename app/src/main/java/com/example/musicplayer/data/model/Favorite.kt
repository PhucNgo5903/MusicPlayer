package com.example.musicplayer.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    // Ràng buộc: Nếu bài hát bị xóa thì favorite cũng tự xóa theo (tùy chọn)
    foreignKeys = [ForeignKey(
        entity = Song::class,
        parentColumns = ["id"],
        childColumns = ["songId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val favId: Int = 0,
    val username: String, // User nào thích
    val songId: Int       // Thích bài nào
)