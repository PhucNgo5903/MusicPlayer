package com.example.musicplayer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.data.local.MusicDatabase
import com.example.musicplayer.data.model.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtFullName = findViewById<EditText>(R.id.edtRegFullName)
        val edtUser = findViewById<EditText>(R.id.edtRegUsername)
        val edtPass = findViewById<EditText>(R.id.edtRegPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegisterAction)
        val tvLogin = findViewById<TextView>(R.id.tvGoToLogin)

        val db = MusicDatabase.getDatabase(this)

        // Bấm nút Đăng ký
        btnRegister.setOnClickListener {
            val fullName = edtFullName.text.toString().trim()
            val user = edtUser.text.toString().trim()
            val pass = edtPass.text.toString().trim()

            if (fullName.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Kiểm tra xem username đã tồn tại chưa
                val existingUser = db.userDao().checkUserExist(user)
                if (existingUser != null) {
                    Toast.makeText(this@RegisterActivity, "Tài khoản này đã tồn tại!", Toast.LENGTH_SHORT).show()
                } else {
                    // Tạo user mới
                    val newUser = User(username = user, password = pass, fullName = fullName)
                    db.userDao().registerUser(newUser)

                    Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    finish() // Đóng màn hình đăng ký để quay về đăng nhập
                }
            }
        }

        // Bấm nút "Đã có tài khoản" -> Quay lại
        tvLogin.setOnClickListener {
            finish()
        }
    }
}