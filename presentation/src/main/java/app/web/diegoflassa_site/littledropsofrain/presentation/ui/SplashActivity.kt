/*
 * Copyright 2021 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.presentation.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.web.diegoflassa_site.littledropsofrain.BuildConfig
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.IntentHelper
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.UriToIntentMapper
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    private val mMapper: UriToIntentMapper = UriToIntentMapper(this, IntentHelper())

    companion object {
        var TAG = SplashActivity::class.simpleName
    }

    @ExperimentalStdlibApi
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
            finish()
        }
    }

    private fun fetchRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else TimeUnit.HOURS.toSeconds(12)).build()
        remoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(
                    TAG,
                    "Config applied successfully"
                )
                remoteConfig.fetchAndActivate()
                    .addOnCompleteListener { taskFetchAndActivate ->
                        if (taskFetchAndActivate.isSuccessful) {
                            Log.d(
                                TAG,
                                "fetchRemoteConfig ${getString(R.string.configuration_fetch_successfull)}"
                            )
                            Toast.makeText(
                                this, getString(R.string.configuration_fetch_successfull),
                                Toast.LENGTH_SHORT
                            ).show()
                            val updated: Boolean = taskFetchAndActivate.result
                            Log.d(TAG, "Activation successfull. Update value is $updated")
                        } else {
                            taskFetchAndActivate.exception?.printStackTrace()
                            Log.d(
                                TAG,
                                "fetchRemoteConfig ${getString(R.string.configuration_fetch_failed)}. Error was ${taskFetchAndActivate.exception}"
                            )
                            Toast.makeText(
                                this, getString(R.string.configuration_fetch_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Log.d(
                    TAG,
                    "Error applying config"
                )
            }
        }
    }
}
