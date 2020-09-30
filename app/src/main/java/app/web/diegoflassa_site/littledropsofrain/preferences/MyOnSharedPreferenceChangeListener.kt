package app.web.diegoflassa_site.littledropsofrain.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.R
import java.util.*

class MyOnSharedPreferenceChangeListener(var context: Context): SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        var TAG = MyOnSharedPreferenceChangeListener::class.simpleName
        const val SP_KEY_SUBSCRIBE_TO_NEWS ="subs_news"
        const val SP_KEY_SUBSCRIBE_TO_PROMOS ="subs_promos"
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        if(key.equals(SP_KEY_SUBSCRIBE_TO_NEWS)){
            if(prefs?.getBoolean(key, true)!!){
                subscribeToNews()
            }else{
                unsubscribeToNews()
            }
        }else if(key.equals(SP_KEY_SUBSCRIBE_TO_PROMOS)){
                if(prefs?.getBoolean(key, true)!!){
                    subscribeToPromos()
                }else{
                    unsubscribeToPromos()
                }
        }
    }

    private fun unsubscribeToPromos() {
        val topic = Helper.getTopicPromosForCurrentLanguage(context)
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                var msg = context.getString(R.string.msg_unsubscribed)
                if (!task.isSuccessful) {
                    msg = context.getString(R.string.msg_subscribe_failed)
                }
                Log.d(TAG, msg)
            }
    }

    private fun subscribeToPromos() {
        val topic = Helper.getTopicPromosForCurrentLanguage(context)
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = context.getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    msg = context.getString(R.string.msg_subscribe_failed)
                }
                Log.d(TAG, msg)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
    }

    private fun unsubscribeToNews() {
        val topic = Helper.getTopicNewsForCurrentLanguage(context)
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                var msg = context.getString(R.string.msg_unsubscribed)
                if (!task.isSuccessful) {
                    msg = context.getString(R.string.msg_subscribe_failed)
                }
                Log.d(TAG, msg)
            }
    }

    private fun subscribeToNews() {
        val topic = Helper.getTopicNewsForCurrentLanguage(context)
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = context.getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    msg = context.getString(R.string.msg_subscribe_failed)
                }
                Log.d(TAG, msg)
            }
    }
}