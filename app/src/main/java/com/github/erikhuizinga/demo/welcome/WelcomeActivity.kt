package com.github.erikhuizinga.demo.welcome

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
    }

    fun mellon(view: View) {
        val nextIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NEXT_INTENT, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NEXT_INTENT)
        }
        if (nextIntent == null) {
            Log.d(TAG, "No next intent found, finishing")
            finish()
            return
        }

        nextIntent.putExtra(FROM_WELCOME, true)
        Log.d(TAG, "Starting next intent: $nextIntent")
        startActivity(nextIntent)
        finish()
    }

    companion object {
        const val NEXT_INTENT = "next_intent"
        const val FROM_WELCOME = "from_welcome"
        private const val TAG = "WelcomeActivity"
    }
}
