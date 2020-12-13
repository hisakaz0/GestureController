package com.pinkienort.app.gesturecontroller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.preference.PreferenceManager
import com.pinkienort.app.gesturecontroller.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SETTING = 100
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var gestureConfig: GestureConfig
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var gestureHandler: GestureHandler
    private val gestureListener = object : GestureHandler.OnGestureListener {
        private val TAG = "GestureListener"

        override fun onSwipeToLeft() {
            Toast.makeText(this@MainActivity, "→ left swipe →", Toast.LENGTH_SHORT).show()
        }

        override fun onSwipeToRight() {
            Toast.makeText(this@MainActivity, "← right swipe ←", Toast.LENGTH_SHORT).show()
        }

        @SuppressLint("SetTextI18n")
        override fun onGestureEvent(logMessage: String) {
            binding.logTextView.text = "$gestureConfig\n$logMessage"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this).all
        gestureConfig = GestureConfig(prefs)
        gestureHandler = GestureHandler(gestureConfig, gestureListener)
        gestureDetector = GestureDetectorCompat(this, gestureHandler)
        gestureDetector.setOnDoubleTapListener(gestureHandler)

        binding.logTextView.text = "$gestureConfig"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(this, GestureConfigActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_SETTING)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SETTING -> {
                if (resultCode == RESULT_OK) {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(this).all
                    gestureConfig = GestureConfig(prefs)
                    gestureHandler.config = gestureConfig
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

