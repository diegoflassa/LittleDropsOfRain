package app.web.diegoflassa_site.littledropsofrain.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import app.web.diegoflassa_site.littledropsofrain.BuildConfig
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.helpers.IntentHelper
import app.web.diegoflassa_site.littledropsofrain.helpers.UriToIntentMapper

class SplashActivity : AppCompatActivity() {

    private val mMapper: UriToIntentMapper = UriToIntentMapper(this, IntentHelper())

    override fun onCreate(savedInstanceState: Bundle?) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme_Launcher)
        super.onCreate(savedInstanceState)

        try {
            mMapper.dispatchIntent(intent)
        } catch (iae: IllegalArgumentException) {
            // Malformed URL
            if (BuildConfig.DEBUG) {
                Log.e("Deep links", "Invalid URI", iae)
            }
        } finally {
            // Always finish the activity so that it doesn't stay in our history
            finish()
        }
    }
}