package com.example.musicplayer.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.musicplayer.data.local.MusicDatabase
import java.io.File
import java.io.FileOutputStream
import java.net.URL

// Worker chạy ngầm để tải nhạc
class DownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // 1. Lấy dữ liệu đầu vào (được gửi từ Repository)
        val songId = inputData.getInt("SONG_ID", -1)
        val songUrl = inputData.getString("SONG_URL") ?: return Result.failure()

        // 2. Tạo file rỗng trong bộ nhớ máy để chuẩn bị lưu
        val fileName = "song_$songId.mp3"
        val file = File(applicationContext.filesDir, fileName)

        return try {
            // 3. Tải dữ liệu từ mạng về file
            URL(songUrl).openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            // 4. Tải xong -> Cập nhật vào Database
            val database = MusicDatabase.getDatabase(applicationContext)
            val songDao = database.songDao()

            val song = songDao.getSongById(songId)
            if (song != null) {
                song.localPath = file.absolutePath // Lưu đường dẫn tuyệt đối
                songDao.update(song)
            }

            // Báo thành công
            Result.success(workDataOf("FILE_PATH" to file.absolutePath))

        } catch (e: Exception) {
            e.printStackTrace()
            // Nếu lỗi thì xóa file rác đi
            if (file.exists()) file.delete()
            Result.failure()
        }
    }
}