package app.web.diegoflassa_site.littledropsofrain.preferences

import android.content.Context
import android.content.SharedPreferences
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper

class MyOnSharedPreferenceChangeListener(var context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        var TAG = MyOnSharedPreferenceChangeListener::class.simpleName
        const val SP_KEY_SUBSCRIBE_TO_NEWS = "subs_news"
        const val SP_KEY_SUBSCRIBE_TO_PROMOS = "subs_promos"
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        if (key.equals(SP_KEY_SUBSCRIBE_TO_NEWS)) {
            if (prefs?.getBoolean(key, true)!!) {
                Helper.subscribeToNews(context)
            } else {
                Helper.unsubscribeToNews(context)
            }
        } else if (key.equals(SP_KEY_SUBSCRIBE_TO_PROMOS)) {
            if (prefs?.getBoolean(key, true)!!) {
                Helper.subscribeToPromos(context)
            } else {
                Helper.unsubscribeToPromos(context)
            }
        }
    }

}