package com.github.erikhuizinga.demo.welcome

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.moria_gate).setOnClickListener { mellon() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "Back pressed: finishing welcomed and welcome activities")
                (application as? WelcomeApplication)?.finshWelcomed()
                finish()
            }
        })
    }

    fun mellon() {
        Log.d(TAG, "Proceeding past welcome")
        (application as? WelcomeApplication)?.proceedPastWelcome()
        finish()
    }

    companion object {
        private const val TAG = "WelcomeActivity"
    }
}
