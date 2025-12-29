package com.example.musicplayer.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicplayer.R

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy dữ liệu user từ SharedPreferences
        val prefs = requireActivity().getSharedPreferences("MusicAppPrefs", Context.MODE_PRIVATE)
        val fullName = prefs.getString("FULLNAME", "User")
        val username = prefs.getString("USERNAME", "username")

        view.findViewById<TextView>(R.id.tvProfileName).text = fullName
        view.findViewById<TextView>(R.id.tvProfileUser).text = "Tài khoản: $username"
    }
}