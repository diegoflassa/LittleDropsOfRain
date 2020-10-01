package app.web.diegoflassa_site.littledropsofrain.contracts

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.firebase.ui.auth.AuthUI
import app.web.diegoflassa_site.littledropsofrain.R
import com.firebase.ui.auth.util.ExtraConstants

class EmailAuthActivityResultContract: ActivityResultContract<Intent, Int>() {

    override fun createIntent(context: Context, input: Intent?): Intent {
        return createSignInIntent(input!!)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }

    private fun createSignInIntent(intent : Intent):Intent{
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build())
        val link = intent.extras!!.getString(ExtraConstants.EMAIL_LINK_SIGN_IN)
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setEmailLink(link!!)
            .setAvailableProviders(providers)
            .build()
    }

}