package app.web.diegoflassa_site.littledropsofrain.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.web.diegoflassa_site.littledropsofrain.BuildConfig
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.helpers.IntentHelper
import app.web.diegoflassa_site.littledropsofrain.helpers.UriToIntentMapper
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig

class SplashActivity : AppCompatActivity() {

    private val mMapper: UriToIntentMapper = UriToIntentMapper(this, IntentHelper())

    companion object {
        var TAG = SplashActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme_Launcher)
        super.onCreate(savedInstanceState)
        fetchRemoteConfig()
        try {
            mMapper.dispatchIntent(intent)
        } catch (iae: IllegalArgumentException) {
            // Malformed URL
            if (BuildConfig.DEBUG) {
                Log.e("Deep links", "Invalid URI", iae)
            }
        } finally {
            // Always finish the activity so that it doesn't stay in our history
        }
    }

    private fun fetchRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val updated: Boolean = it.result
                Log.d(
                    TAG,
                    "fetchRemoteConfig ${getString(R.string.configuration_fetch_successfull)}. Update is $updated"
                )
                Toast.makeText(
                    this, getString(R.string.configuration_fetch_successfull),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Log.d(TAG, "fetchRemoteConfig ${getString(R.string.configuration_fetch_failed)}")
                Toast.makeText(
                    this, getString(R.string.configuration_fetch_failed),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}