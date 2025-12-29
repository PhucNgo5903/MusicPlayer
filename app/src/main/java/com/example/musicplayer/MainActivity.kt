package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Thay vì dùng setContent (Compose), ta dùng setContentView (XML)
        setContentView(R.layout.activity_main)
    }
}