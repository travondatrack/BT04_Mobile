package com.example.bt_sqlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import android.widget.TextView

class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create/open database and ensure a sample table exists
        val databaseHandler = DatabaseHandler(this, "QuanLySinhVien.sqlite", null, 1)
        databaseHandler.QueryData("CREATE TABLE IF NOT EXISTS SinhVien(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name VARCHAR(100))")

        // Use a traditional XML layout instead of Jetpack Compose
        setContentView(R.layout.activity_main)

        // Update the TextView in the layout
        val tvGreeting = findViewById<TextView>(R.id.textGreeting)
        tvGreeting.text = "Hello Android!"
    }
}