package com.example.customapp.Views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.customapp.R

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_us)

        val backButton : Button = findViewById(R.id.backButton)
        backButton.setOnClickListener{
            finish()
        }
    }
}