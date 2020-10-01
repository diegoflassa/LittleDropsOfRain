package app.web.diegoflassa_site.littledropsofrain.contracts

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.firebase.ui.auth.AuthUI
import app.web.diegoflassa_site.littledropsofrain.R
import com.google.firebase.auth.ActionCodeSettings

class AuthActivityResultContract: ActivityResultContract<Any, Int>() {

    override fun createIntent(context: Context, input: Any?): Intent {
        return createSignInIntent()
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }

    private fun createSignInIntent():Intent{
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName("app.web.diegoflassa_site", /* installIfNotAvailable= */ true,
                /* minimumVersion= */ "23")
            .setHandleCodeInApp(true) // This must be set to true
            .setUrl("https://littledropsofrain.page.link/passwordless") // This URL needs to be whitelisted
            .build()

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().enableEmailLinkSignIn()
                .setActionCodeSettings(actionCodeSettings).build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.TwitterBuilder().build(),
                AuthUI.IdpConfig.GitHubBuilder().build())

        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.little_drops_of_rain) // Set logo drawable
            .setTheme(R.style.AppTheme) // Set theme
            .build()
    }

}