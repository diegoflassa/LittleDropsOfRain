package io.github.diegoflassa.littledropsofrain.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging
import io.github.diegoflassa.littledropsofrain.R

class MyOnSharedPreferenceChangeListener(var context: Context): SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        var TAG = MyOnSharedPreferenceChangeListener::class.simpleName
        const val SP_KEY_SUBSCRIBE_TO_NEWS ="subs_news"
        const val SP_KEY_SUBSCRIBE_TO_PROMOS ="subs_promos"
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        if(key.equals(SP_KEY_SUBSCRIBE_TO_NEWS)){
            if(prefs?.getBoolean(key, false)!!){
                subscribeToNews()
            }else{
                unsubscribeToNews()
            }
        }else if(key.equals(SP_KEY_SUBSCRIBE_TO_PROMOS)){
                if(prefs?.getBoolean(key, false)!!){
                    subscribeToPromos()
                }else{
                    unsubscribeToPromos()
                }
        }
    }

    private fun unsubscribeToPromos() {
        val sp : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if(sp.getBoolean("subs_promo", false)) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(context.getString(R.string.topic_promo))
                .addOnCompleteListener { task ->
                    var msg = context.getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = context.getString(R.string.msg_subscribe_failed)
                    }
                    Log.d(TAG, msg)
                }
        }else{
            Log.d(TAG, "Not registered to receive news")
        }
    }

    private fun subscribeToPromos() {
        val sp : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if(sp.getBoolean("subs_promo", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic(context.getString(R.string.topic_promo))
                .addOnCompleteListener { task ->
                    var msg = context.getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = context.getString(R.string.msg_subscribe_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
        }else{
            Log.d(TAG, "Not registered to receive news")
        }
    }

    private fun unsubscribeToNews() {
        val sp : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if(sp.getBoolean("subs_news", false)) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(context.getString(R.string.topic_news))
                .addOnCompleteListener { task ->
                    var msg = context.getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = context.getString(R.string.msg_subscribe_failed)
                    }
                    Log.d(TAG, msg)
                }
        }else{
            Log.d(TAG, "Not registered to receive news")
        }
    }

    private fun subscribeToNews() {
        val sp : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if(sp.getBoolean("subs_news", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic(context.getString(R.string.topic_news))
                .addOnCompleteListener { task ->
                    var msg = context.getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = context.getString(R.string.msg_subscribe_failed)
                    }
                    Log.d(TAG, msg)
                }
        }else{
            Log.d(TAG, "Not registered to receive news")
        }
    }
}