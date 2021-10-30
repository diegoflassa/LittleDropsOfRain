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

package app.web.diegoflassa_site.littledropsofrain.presentation.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import app.web.diegoflassa_site.littledropsofrain.presentation.MainActivity
import java.util.*

@ExperimentalStdlibApi
class UriToIntentMapper(context: Context, intentHelper: IntentHelper) {
    private val mContext: Context = context
    private val mIntents: IntentHelper = intentHelper

    @ExperimentalStdlibApi
    fun dispatchIntent(intent: Intent) {
        val uri: Uri? = intent.data
        var dispatchIntent: Intent? = null
        if (uri != null) {
            val scheme: String = uri.scheme!!.lowercase(Locale.ROOT)
            val host: String = uri.host!!.lowercase(Locale.ROOT)
            if ("app" == scheme) {
                dispatchIntent = mapAppLink(uri)
            } else if (("http" == scheme || "https" == scheme) &&
                ("ldor.page.link" == host || "littledropsofrain-site.web.app" == host || "littledropsofrain.web.app" == host || "littledropsofrain" == host)
            ) {
                dispatchIntent = mapWebLink(uri)
            }
            if (dispatchIntent != null) {
                mContext.startActivity(dispatchIntent)
            }
        } else {
            routeToAppropriatePage()
        }
    }

    private fun routeToAppropriatePage() {
        mContext.startActivity(Intent(mContext, MainActivity::class.java))
    }

    @ExperimentalStdlibApi
    private fun mapAppLink(uri: Uri): Intent? {
        when (uri.host!!.lowercase(Locale.ROOT)) {
            "app" -> return mIntents.newMainActivityIntent(mContext)
        }
        return null
    }

    private fun mapWebLink(uri: Uri): Intent? {
        when (uri.path) {
            "/privacy", "/tos", "/licenses" -> {
                val startWhat: String = uri.path!!.substring(1)
                return mIntents.newMainActivityIntent(mContext, startWhat)
            }
        }
        return null
    }
}
