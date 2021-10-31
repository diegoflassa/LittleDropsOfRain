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

package app.web.diegoflassa_site.littledropsofrain.presentation

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.domain.preferences.MyOnSharedPreferenceChangeListener
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.SettingsFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.SimpleLineIconsModule
import com.joanzapata.iconify.fonts.TypiconsModule
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference

@HiltAndroidApp
@ExperimentalStdlibApi
class MyApplication : Application() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate() {
        super.onCreate()
        initializeSafetyNet()
        Iconify
            // .with(FontAwesomeModule())
            // .with(EntypoModule())
            .with(TypiconsModule())
            // .with(MaterialModule())
            // .with(MaterialCommunityModule())
            // .with(MeteoconsModule())
            // .with(WeathericonsModule())
            .with(SimpleLineIconsModule())
        // .with(IoniconsModule())

        setup()
        setupCacheSize()

        subscribeToNews()
        subscribeToPromotions()
        updateSubscribedLanguage()
        context = WeakReference(this)
    }

    private fun initializeSafetyNet() {
        FirebaseApp.initializeApp(/*context=*/ this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )
    }

    @Suppress("DEPRECATION")
    private fun updateSubscribedLanguage() {
        val current =
            resources.configuration.locales.get(0)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(
            SettingsFragment.SUBSCRIBED_LANGUAGE_KEY, current.language
        ).apply()
    }

    private fun subscribeToNews() {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sp.getBoolean(MyOnSharedPreferenceChangeListener.SP_KEY_SUBSCRIBE_TO_NEWS, true)) {
            val topic = Helper.getTopicNewsForCurrentLanguage(this)
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed_to_news)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_news_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d(TAG, "Not registered to receive news")
        }
    }

    private fun subscribeToPromotions() {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sp.getBoolean(MyOnSharedPreferenceChangeListener.SP_KEY_SUBSCRIBE_TO_PROMOS, true)) {
            val topic = Helper.getTopicPromosForCurrentLanguage(this)
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed_to_promos)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_promos_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d(TAG, "Not registered to receive promos")
        }
    }

    private fun setup() {
        // [START get_firestore_instance]
        db = Firebase.firestore
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
        // [END set_firestore_settings]
    }

    private fun setupCacheSize() {
        // [START fs_setup_cache]
        val settings = firestoreSettings {
            cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
        }
        db.firestoreSettings = settings
        // [END fs_setup_cache]
    }

    companion object {
        private val TAG = MyApplication::class.simpleName
        private lateinit var context: WeakReference<Context>
        fun getContext(): Context {
            return context.get()!!
        }
    }
}
