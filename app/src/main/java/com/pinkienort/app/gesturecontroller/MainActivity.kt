package com.pinkienort.app.gesturecontroller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.pinkienort.app.gesturecontroller.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SETTING = 100
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var gestureConfig: GestureConfig
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var gestureHandler: GestureHandler

    @SuppressLint(
        value = [
            "SetTextI18n",
            "DefaultLocale"
        ]
    )
    private val gestureListener = object : GestureHandler.OnGestureListener {
        private var jobToClearText: Job? = null
        private fun show(
            text: String,
            textView: TextView = binding.gestureNameTextView,
            delayTimeToClear: Long = 1000
        ) {
            textView.text = text
            jobToClearText?.cancel()
            jobToClearText = lifecycleScope.launchWhenStarted {
                delay(delayTimeToClear)
                textView.text = null
            }
        }

        override fun onSwipeToLeft() {
            show("swipe to left".toUpperCase())
        }

        override fun onSwipeToRight() {
            show("swipe to right".toUpperCase())
        }

        override fun onDoubleTapInLeftSide() {
            show("double tap in left".toUpperCase())
        }

        override fun onDoubleTapInRightSide() {
            show("double tap in right".toUpperCase())
        }

        override fun onLongPressInLeftSide() {
            show("long press in left".toUpperCase())
        }

        override fun onLongPressInRightSide() {
            show("long press in right".toUpperCase())
        }

        @SuppressLint("SetTextI18n")
        override fun onGestureEvent(logMessage: String) {
            Timber.d(logMessage)
            binding.logTextView.text = "$gestureConfig\n$logMessage"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this).all
        gestureConfig = GestureConfig(prefs)
        gestureHandler = GestureHandler(this, gestureConfig, gestureListener)
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

