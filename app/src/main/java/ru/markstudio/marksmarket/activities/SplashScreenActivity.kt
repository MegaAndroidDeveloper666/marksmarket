package ru.markstudio.marksmarket.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import ru.markstudio.marksmarket.R
import java.util.*
import kotlin.concurrent.schedule

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        val SPLASH_TAG = "Splash"
        val SPLASH_DELAY: Long = 1500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash_screen)
//        initData()
        Timer(SPLASH_TAG, false).schedule(SPLASH_DELAY) {
            startMainActivity()
        }
    }

//    private fun initData() {
//        DataHolder.instance.readFromCSV(resources.openRawResource(R.raw.data))
//        DataHolder.instance.currentMode = AppMode.BUY
//    }

    private fun startMainActivity() {
        startActivity(Intent(applicationContext, GoodsListActivity::class.java))
        finish()
    }

}
