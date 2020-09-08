package app.web.diegoflassa_site.littledropsofrain

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.*
import app.web.diegoflassa_site.littledropsofrain.preferences.MyOnSharedPreferenceChangeListener
import java.lang.ref.WeakReference

class MyApplication : Application() {

    private lateinit var db : FirebaseFirestore

    override fun onCreate() {
        super.onCreate()
        Iconify
            .with(FontAwesomeModule())
            .with(EntypoModule())
            .with(TypiconsModule())
            .with(MaterialModule())
            .with(MaterialCommunityModule())
            .with(MeteoconsModule())
            .with(WeathericonsModule())
            .with(SimpleLineIconsModule())
            .with(IoniconsModule())
        setup()
        setupCacheSize()

        subscribeToNews()
        subscribeToPromotions()
        context =  WeakReference(this)
    }

    private fun subscribeToNews() {
        val sp : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if(sp.getBoolean(MyOnSharedPreferenceChangeListener.SP_KEY_SUBSCRIBE_TO_NEWS, true)) {
            FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.topic_news))
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed_to_news)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_news_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        }else{
            Log.d(TAG, "Not registered to receive news")
        }
    }

    private fun subscribeToPromotions() {
        val sp : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if(sp.getBoolean(MyOnSharedPreferenceChangeListener.SP_KEY_SUBSCRIBE_TO_PROMOS, true)) {
            FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.topic_promo))
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed_to_promos)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_promos_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        }else{
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

    companion object{
        private val TAG= MyApplication::class.simpleName
        private lateinit var context: WeakReference<Context>
        fun getContext():Context{
            return context.get()!!
        }
    }
}