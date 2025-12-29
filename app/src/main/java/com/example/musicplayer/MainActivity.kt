package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
// QUAN TRỌNG: Phải import dòng này mới dùng được NavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setup Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        // --- SỬA LỖI 1: Thêm <NavigationView> để máy hiểu đây là Menu ---
        val navView = findViewById<NavigationView>(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 2. Setup NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 3. Hiển thị tên User lên Header
        val prefs = getSharedPreferences("MusicAppPrefs", MODE_PRIVATE)
        val fullName = prefs.getString("FULLNAME", "Bạn")

        // --- SỬA LỖI 2: Phải tìm view xong mới được gán text ---
        // Lấy cái View Header (phần trên cùng của menu) ra trước
        val headerView = navView.getHeaderView(0)

        // Tìm cái TextView tên là tvHeaderName nằm TRONG cái header đó
        val tvHeaderName = headerView.findViewById<TextView>(R.id.tvHeaderName)

        // Giờ mới gán text được
        tvHeaderName.text = "Xin chào, $fullName"

        // 4. Xử lý bấm Menu (Lỗi 3 sẽ tự hết khi sửa xong Lỗi 1)
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    navController.navigate(R.id.profileFragment)
                }
                R.id.nav_favorites -> {
                    navController.navigate(R.id.favoriteFragment)
                }
                R.id.nav_logout -> {
                    prefs.edit().clear().apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}