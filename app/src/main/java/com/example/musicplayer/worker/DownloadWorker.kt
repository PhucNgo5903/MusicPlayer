package com.example.musicplayer.worker

import android.content.Context
import androidx.work.CoroutineWorker // SỬA: Đổi từ Worker sang CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.musicplayer.data.local.MusicDatabase
import com.example.musicplayer.data.model.Favorite
import kotlinx.coroutines.Dispatchers // THÊM: Để xử lý luồng
import kotlinx.coroutines.withContext // THÊM: Để chuyển luồng
import java.io.File
import java.io.FileOutputStream
import java.net.URL

// ... imports

class DownloadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val songId = inputData.getInt("SONG_ID", -1)
        val songUrl = inputData.getString("SONG_URL") ?: return Result.failure()

        // --- NHẬN THÊM USERNAME ---
        val username = inputData.getString("USERNAME") ?: return Result.failure()

        val fileName = "song_$songId.mp3"
        val file = File(applicationContext.filesDir, fileName)

        return try {
            // 1. Tải file (Giữ nguyên code cũ)
            if (!file.exists()) { // Chỉ tải nếu chưa có file
                URL(songUrl).openStream().use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val database = MusicDatabase.getDatabase(applicationContext)

            // 2. Cập nhật bảng Song (Giữ nguyên để biết file đã nằm trong máy)
            val songDao = database.songDao()
            val song = songDao.getSongById(songId)
            if (song != null) {
                song.localPath = file.absolutePath
                songDao.update(song)
            }

            // 3. --- QUAN TRỌNG: THÊM VÀO BẢNG FAVORITE CỦA USER ---
            val favDao = database.favoriteDao()
            // Kiểm tra tránh trùng lặp
            if (!favDao.isFavorite(username, songId)) {
                favDao.addFavorite(Favorite(username = username, songId = songId))
            }
            // -----------------------------------------------------

            Result.success(workDataOf("FILE_PATH" to file.absolutePath))
        } catch (e: Exception) {
            e.printStackTrace()
            if (file.exists()) file.delete()
            Result.failure()
        }
    }
}