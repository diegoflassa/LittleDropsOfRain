package app.web.diegoflassa_site.littledropsofrain.contracts

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import app.web.diegoflassa_site.littledropsofrain.R
import com.firebase.ui.auth.AuthUI

class EmailLinkAuthActivityResultContract : ActivityResultContract<Intent, Int>() {

    override fun createIntent(context: Context, input: Intent?): Intent {
        return createSignInIntent(input!!)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }

    private fun createSignInIntent(intent: Intent): Intent {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
        )

        val emailLink = intent.data.toString()
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setEmailLink(emailLink)
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.little_drops_of_rain) // Set logo drawable
            .setTheme(R.style.AppTheme) // Set theme
            .build()
    }

}