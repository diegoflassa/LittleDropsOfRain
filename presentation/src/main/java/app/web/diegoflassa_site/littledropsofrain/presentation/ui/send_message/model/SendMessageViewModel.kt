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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.send_message.model

import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.send_message.SendMessageFragment

class SendMessageViewModel(state: SavedStateHandle) : ViewModel() {
    companion object {
        private const val SAVE_STATE_KEY_TITLE = "SEND_MESSAGE_SAVE_STATE_KEY_TITLE"
    }

    private val savedStateHandle = state
    private var mTitle: String = ""
    private var mReplyUid: String = ""
    private var mText: String = ""
    private var mBody: String = ""
    private var mDest: User = User()
    private var mSender: User = User()
    private var mIsUserAdmin: Boolean = false
    private var mSendMethod: SendMethod = SendMethod.MESSAGE

    init {
        savedStateHandle.set(SAVE_STATE_KEY_TITLE, mTitle)
    }

    val titleLiveData: MutableLiveData<String>
        get(): MutableLiveData<String> {
            return savedStateHandle.getLiveData(SAVE_STATE_KEY_TITLE)
        }
    val title: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_TITLE)!!
        }
}

@Keep
enum class SendMethod(private val method: String) {
    MESSAGE("message"),
    EMAIL("email"),
    UNKNOWN("unknown");

    override fun toString(): String {
        return method
    }
}
