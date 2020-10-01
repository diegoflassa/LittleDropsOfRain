package app.web.diegoflassa_site.littledropsofrain.helpers

import android.content.Context
import android.content.Intent
import app.web.diegoflassa_site.littledropsofrain.MainActivity


class IntentHelper {
    fun newMainActivityIntent(context: Context?): Intent {
        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return i
    }

    fun newMainActivityIntent(context: Context?, whatToStart : String): Intent {
        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.putExtra(EXTRA_START_WHAT, whatToStart)
        return i
    }

    fun newMainActivityIntentCopy(context: Context?, intent : Intent): Intent {
        return intent
    }

    companion object {
        var EXTRA_START_WHAT = "app.web.diegoflassa_site.littledropsofrain.EXTRA_START_WHAT"
    }
}