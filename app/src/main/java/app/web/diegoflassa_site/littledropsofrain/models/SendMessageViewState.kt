/*
 * Copyright 2020 The Little Drops of Rain Project
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

package app.web.diegoflassa_site.littledropsofrain.models

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.data.entities.User

class SendMessageViewState : LiveData<SendMessageViewState>() {

    @Keep
    enum class SendMethod(private val method: String) {
        MESSAGE("message"),
        EMAIL("email"),
        UNKNOWN("unknown");

        override fun toString(): String {
            return method
        }
    }

    var title = ""
    var replyUid = ""
    var text = ""
    var body = ""
    var dest = User()
    var sender = User()
    var isUserAdmin = false
    var sendMethod = SendMethod.MESSAGE
}
