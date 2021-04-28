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
