package app.web.diegoflassa_site.littledropsofrain.contracts

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import app.web.diegoflassa_site.littledropsofrain.R
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ActionCodeSettings

class AuthActivityResultContract : ActivityResultContract<String?, Int>() {

    override fun createIntent(context: Context, input: String?): Intent {
        return createSignInIntent(input, context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }

    private fun createSignInIntent(emailLink: String?, context: Context): Intent {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName(
                context.getString(R.string.android_package_name), /* installIfNotAvailable= */ true,
                /* minimumVersion= */ "23"
            )
            .setHandleCodeInApp(true) // This must be set to true
            .setDynamicLinkDomain(context.getString(R.string.dynamic_link_domain))
            .setUrl(context.getString(R.string.dynamic_link_url)) // This URL needs to be whitelisted
            .build()

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().enableEmailLinkSignIn()
                .setActionCodeSettings(actionCodeSettings).build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            //AuthUI.IdpConfig.TwitterBuilder().build(),
            //AuthUI.IdpConfig.GitHubBuilder().build()
        )
        val builder = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setTosAndPrivacyPolicyUrls(
                context.getString(R.string.privacy_url),
                context.getString(R.string.tos_url)
            )
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.little_drops_of_rain) // Set logo drawable
            .setTheme(R.style.AppTheme) // Set theme
        if (emailLink != null) {
            builder.setEmailLink(emailLink)
        }
        return builder.build()
    }

}