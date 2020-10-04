package app.web.diegoflassa_site.littledropsofrain.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import java.util.*


class UriToIntentMapper(context: Context, intentHelper: IntentHelper) {
    private val mContext: Context = context
    private val mIntents: IntentHelper = intentHelper
    fun dispatchIntent(intent: Intent) {
        val uri: Uri? = intent.data
        var dispatchIntent: Intent? = null
        if (uri != null) {
            val scheme: String = uri.scheme!!.toLowerCase(Locale.ROOT)
            val host: String = uri.host!!.toLowerCase(Locale.ROOT)
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

    private fun mapAppLink(uri: Uri): Intent? {
        when (uri.host!!.toLowerCase(Locale.ROOT)) {
            "app" -> return mIntents.newMainActivityIntent(mContext)
        }
        return null
    }

    private fun mapWebLink(uri: Uri): Intent? {
        when (uri.path) {
            "/privacy" -> {
                val startWhat: String = uri.path!!.substring(1)
                return mIntents.newMainActivityIntent(mContext, startWhat)
            }
            "/tos" -> {
                val startWhat: String = uri.path!!.substring(1)
                return mIntents.newMainActivityIntent(mContext, startWhat)
            }
            "/licenses" -> {
                val startWhat: String = uri.path!!.substring(1)
                return mIntents.newMainActivityIntent(mContext, startWhat)
            }
        }
        return null
    }

}