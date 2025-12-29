package com.example.musicplayer.data.remote

import com.example.musicplayer.data.model.Song
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// URL mẫu chứa JSON bài hát
private const val BASE_URL = "https://storage.googleapis.com/automotive-media/"

interface MusicApiService {
    @GET("music.json") // Endpoint giả định, bạn thay bằng API thực tế của bạn
    suspend fun getPlaylist(): Map<String, List<Song>> // API mẫu của Google trả về format hơi lạ, ta sẽ xử lý ở Repository
}

object RetrofitClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: MusicApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MusicApiService::class.java)
    }
}