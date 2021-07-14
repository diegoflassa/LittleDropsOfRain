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

package app.web.diegoflassa_site.littledropsofrain.presentation.contracts

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

        val scopesGithub: List<String> = object : ArrayList<String>() {
            init {
                add("read:user")
                add("user:email")
            }
        }

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().enableEmailLinkSignIn()
                .setActionCodeSettings(actionCodeSettings).build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build(),
            AuthUI.IdpConfig.GitHubBuilder().setScopes(scopesGithub).build()
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
