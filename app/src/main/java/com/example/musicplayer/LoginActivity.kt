package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.data.local.MusicDatabase
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Kiểm tra nếu đã đăng nhập rồi thì vào thẳng Main luôn
        val prefs = getSharedPreferences("MusicAppPrefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)
        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val edtUser = findViewById<EditText>(R.id.edtUsername)
        val edtPass = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvGoToRegister)

        val db = MusicDatabase.getDatabase(this)

        // 2. Xử lý Đăng nhập
        btnLogin.setOnClickListener {
            val user = edtUser.text.toString().trim()
            val pass = edtPass.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val foundUser = db.userDao().login(user, pass)
                if (foundUser != null) {
                    // Đăng nhập thành công -> Lưu trạng thái
                    prefs.edit().apply {
                        putBoolean("IS_LOGGED_IN", true)
                        putString("USERNAME", foundUser.username)
                        putString("FULLNAME", foundUser.fullName)
                        apply()
                    }

                    Toast.makeText(this@LoginActivity, "Xin chào, ${foundUser.fullName}", Toast.LENGTH_SHORT).show()

                    // Chuyển sang màn hình chính
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 3. Chuyển sang màn hình Đăng ký
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}