package com.stresstest

import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.hypot
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var vibrator: Vibrator
    private val mediaPlayers = mutableListOf<MediaPlayer>()
    private var isRunning = false
    private var isPrankStarted = false
    private lateinit var rootLayout: FrameLayout
    private lateinit var terminalLog: TextView
    private lateinit var stopBtn: Button
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var audioManager: AudioManager
    private var maxVol = 0

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onLockTaskModeEntering(pkg: String, admin: ComponentName) {
        super.onLockTaskModeEntering(pkg, admin)
        startThePrank()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()

        rootLayout = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        setContentView(rootLayout)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        val splashId = resources.getIdentifier("splash_image", "drawable", packageName)
        val imageView = ImageView(this).apply {
            if (splashId != 0) {
                setImageResource(splashId)
            } else {
                setBackgroundColor(Color.DKGRAY)
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            params.gravity = Gravity.CENTER
            layoutParams = params
        }
        rootLayout.addView(imageView)

        try {
            startLockTask()
        } catch (e: Exception) {}

        handler.postDelayed({
            startThePrank()
        }, 3000)
    }

    private fun startThePrank() {
        if (isPrankStarted) return
        isPrankStarted = true
        isRunning = true

        rootLayout.removeAllViews()
        rootLayout.setBackgroundColor(Color.BLACK)

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0)
        val volumeRunnable = object : Runnable {
            override fun run() {
                if (!isRunning) return
                try {
                    if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < maxVol) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0)
                    }
                } catch (e: Exception) {}
                handler.postDelayed(this, 500)
            }
        }
        handler.post(volumeRunnable)

        val vid1Id = resources.getIdentifier("video1", "raw", packageName)
        if (vid1Id != 0) {
            val vv1 = VideoView(this).apply {
                setVideoURI(Uri.parse("android.resource://$packageName/$vid1Id"))
                setOnPreparedListener { mp -> mp.isLooping = true; mp.setVolume(1f, 1f); start() }
            }
            val params1 = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            params1.gravity = Gravity.CENTER
            rootLayout.addView(vv1, params1)
        }

        terminalLog = TextView(this).apply {
            setTextColor(Color.parseColor("#00FF00"))
            textSize = 10f
            typeface = Typeface.MONOSPACE
            setPadding(20, 20, 20, 20)
            text = "> INITIALIZING SYSTEM...\n"
        }
        rootLayout.addView(terminalLog, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))

        val hackerRunnable = object : Runnable {
            override fun run() {
                if (!isRunning) return
                val hex = Integer.toHexString(Random.nextInt(0x1000000)).uppercase()
                val logs = listOf("> SCANNING SECTOR $hex", "> ACCESS GRANTED", "> WARNING: INTRUDER DETECTED", "> BYPASSING FIREWALL...", "> DATA EXTRACTED: OK", "> EVASION PROTOCOL ON")
                terminalLog.append(logs.random() + "\n")
                if (terminalLog.text.length > 2000) terminalLog.text = "> SYSTEM OVERRIDE...\n"
                handler.postDelayed(this, 150)
            }
        }
        handler.post(hackerRunnable)

        val flyingViews = mutableListOf<View>()

        val vid2Id = resources.getIdentifier("video2", "raw", packageName)
        if (vid2Id != 0) {
            val vv2 = VideoView(this).apply {
                setVideoURI(Uri.parse("android.resource://$packageName/$vid2Id"))
                setOnPreparedListener { mp -> mp.isLooping = true; mp.setVolume(1f, 1f); start() }
            }
            val params2 = FrameLayout.LayoutParams(600, 600)
            params2.gravity = Gravity.CENTER
            rootLayout.addView(vv2, params2)
            flyingViews.add(vv2)
        }
        
        for (i in 1..2) {
            val imgId = resources.getIdentifier("image$i", "drawable", packageName)
            if (imgId != 0) {
                val iv = ImageView(this).apply {
                    setImageResource(imgId)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                val params = FrameLayout.LayoutParams(400, 400)
                params.gravity = Gravity.CENTER
                rootLayout.addView(iv, params)
                flyingViews.add(iv)
            }
        }

        val flyingTextView = TextView(this).apply {
            text = "Created by Rudoo"
            setTextColor(Color.CYAN)
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(15f, 0f, 0f, Color.BLUE)
        }
        val textParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        textParams.gravity = Gravity.CENTER
        rootLayout.addView(flyingTextView, textParams)
        flyingViews.add(flyingTextView)

        stopBtn = Button(this).apply {
            text = "X"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.RED)
            visibility = View.INVISIBLE
            val btnParams = FrameLayout.LayoutParams(80, 80)
            btnParams.gravity = Gravity.CENTER
            layoutParams = btnParams
            setOnClickListener { 
                stopPrank() 
                try { stopLockTask() } catch (e: Exception) {}
                finish() 
            }
        }
        rootLayout.addView(stopBtn)
        stopBtn.bringToFront()

        handler.postDelayed({
            if (isRunning) {
                stopBtn.visibility = View.VISIBLE
                terminalLog.append("\n> ERROR: TERMINATE BUTTON UNLOCKED\n")
                startButtonMoving(stopBtn)
            }
        }, 30000)

        handler.postDelayed({
            if (isRunning) {
                stopPrank()
                try { stopLockTask() } catch (e: Exception) {}
                finish()
            }
        }, 60000)

        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                if (stopBtn.visibility == View.VISIBLE) {
                    val touchX = event.x
                    val touchY = event.y
                    val btnX = stopBtn.x + (stopBtn.width / 2)
                    val btnY = stopBtn.y + (stopBtn.height / 2)
                    if (hypot(touchX - btnX, touchY - btnY) < 200f) {
                        val newX = Random.nextInt(0, rootLayout.width - stopBtn.width).toFloat()
                        val newY = Random.nextInt(0, rootLayout.height - stopBtn.height).toFloat()
                        stopBtn.animate().translationX(newX - (rootLayout.width / 2f - stopBtn.width / 2f)).translationY(newY - (rootLayout.height / 2f - stopBtn.height / 2f)).setDuration(0).start()
                    }
                }
            }
            true
        }

        startVibration()
        startMultiSound()
        rootLayout.post { flyingViews.forEach { startFlying(it) } }
    }

    private fun startButtonMoving(btn: Button) {
        val buttonMover = object : Runnable {
            override fun run() {
                if (!isRunning || btn.visibility != View.VISIBLE) return
                val screenWidth = rootLayout.width
                val screenHeight = rootLayout.height
                if (screenWidth > 0 && screenHeight > 0) {
                    val newX = Random.nextInt(0, screenWidth - btn.width).toFloat()
                    val newY = Random.nextInt(0, screenHeight - btn.height).toFloat()
                    btn.animate().translationX(newX - (screenWidth / 2f - btn.width / 2f))
                        .translationY(newY - (screenHeight / 2f - btn.height / 2f))
                        .setDuration(500)
                        .start()
                }
                handler.postDelayed(this, 2000)
            }
        }
        handler.post(buttonMover)
    }

    private fun startFlying(view: View) {
        val runnable = object : Runnable {
            override fun run() {
                if (!isRunning) return
                val screenWidth = rootLayout.width
                val screenHeight = rootLayout.height
                if (screenWidth == 0 || screenHeight == 0) { handler.postDelayed(this, 100); return }
                val viewWidth = view.width.takeIf { it > 0 } ?: 200
                val viewHeight = view.height.takeIf { it > 0 } ?: 200
                
                val maxX = if (screenWidth > viewWidth) screenWidth - viewWidth else 0
                val maxY = if (screenHeight > viewHeight) screenHeight - viewHeight else 0
                
                val targetX = Random.nextInt(0, maxX + 1).toFloat()
                val targetY = Random.nextInt(0, maxY + 1).toFloat()
                
                view.animate()
                    .translationX(targetX - (screenWidth / 2f - viewWidth / 2f))
                    .translationY(targetY - (screenHeight / 2f - viewHeight / 2f))
                    .setDuration(300L + Random.nextInt(700))
                    .setInterpolator(LinearInterpolator())
                    .withEndAction { if (isRunning) handler.post(this) }
                    .start()
            }
        }
        handler.post(runnable)
    }

    private fun startVibration() {
        val pattern = longArrayOf(0, 300, 100, 300, 100, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        else { @Suppress("DEPRECATION") vibrator.vibrate(pattern, 0) }
    }

    private fun startMultiSound() {
        for (i in 1..2) {
            val soundId = resources.getIdentifier("sound$i", "raw", packageName)
            if (soundId != 0) {
                val mp = MediaPlayer.create(this, soundId)
                mp?.apply {
                    isLooping = true
                    setVolume(1f, 1f)
                    start()
                    mediaPlayers.add(this)
                }
            }
        }
    }

    private fun stopPrank() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
        vibrator.cancel()
        mediaPlayers.forEach { try { it.stop(); it.release() } catch (e: Exception) {} }
        mediaPlayers.clear()
    }

    override fun onDestroy() { 
        stopPrank() 
        try { stopLockTask() } catch (e: Exception) {}
        super.onDestroy() 
    }
    override fun onBackPressed() { }
}
