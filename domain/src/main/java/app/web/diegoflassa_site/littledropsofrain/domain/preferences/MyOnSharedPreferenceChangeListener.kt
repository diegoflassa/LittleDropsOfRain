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

package app.web.diegoflassa_site.littledropsofrain.domain.preferences

import android.content.Context
import android.content.SharedPreferences
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.Helper

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
