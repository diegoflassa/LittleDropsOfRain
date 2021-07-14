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
import app.web.diegoflassa_site.littledropsofrain.presentation.MainActivity

class IntentHelper {
    fun newMainActivityIntent(context: Context?): Intent {
        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return i
    }

    fun newMainActivityIntent(context: Context?, whatToStart: String): Intent {
        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.putExtra(EXTRA_START_WHAT, whatToStart)
        return i
    }

    companion object {
        var EXTRA_START_WHAT = "app.web.diegoflassa_site.littledropsofrain.EXTRA_START_WHAT"
    }
}
