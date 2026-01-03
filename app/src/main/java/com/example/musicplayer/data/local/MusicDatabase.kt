package com.example.musicplayer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicplayer.data.model.Favorite
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.model.User

@Database(entities = [Song::class, User::class, Favorite::class], version = 3, exportSchema = false)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: MusicDatabase? = null

        fun getDatabase(context: Context): MusicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "music_database"
                )
                    .fallbackToDestructiveMigration() // Xóa DB cũ nếu version lệch (tránh crash)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}