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

package app.web.diegoflassa_site.littledropsofrain.presentation.old.ui.send_message

import androidx.annotation.Keep
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.User

class SendMessageViewModel(state: SavedStateHandle) : ViewModel() {
    companion object {
        private const val SAVE_STATE_KEY_TITLE = "SEND_MESSAGE_SAVE_STATE_KEY_TITLE"
        private const val SAVE_STATE_KEY_REPLY_UID = "SEND_MESSAGE_SAVE_STATE_KEY_REPLY_UID"
        private const val SAVE_STATE_KEY_TEXT = "SEND_MESSAGE_SAVE_STATE_KEY_TEXT"
        private const val SAVE_STATE_KEY_BODY = "SEND_MESSAGE_SAVE_STATE_KEY_BODY"
        private const val SAVE_STATE_KEY_DEST = "SEND_MESSAGE_SAVE_STATE_KEY_DEST"
        private const val SAVE_STATE_KEY_SENDER = "SEND_MESSAGE_SAVE_STATE_KEY_SENDER"
        private const val SAVE_STATE_KEY_IS_USER_ADMIN = "SEND_MESSAGE_SAVE_STATE_KEY_IS_USER_ADMIN"
        private const val SAVE_STATE_KEY_SEND_METHOD = "SEND_MESSAGE_SAVE_STATE_KEY_SEND_METHOD"
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
        savedStateHandle.set(SAVE_STATE_KEY_REPLY_UID, mReplyUid)
        savedStateHandle.set(SAVE_STATE_KEY_TEXT, mText)
        savedStateHandle.set(SAVE_STATE_KEY_BODY, mBody)
        savedStateHandle.set(SAVE_STATE_KEY_DEST, mDest)
        savedStateHandle.set(SAVE_STATE_KEY_SENDER, mSender)
        savedStateHandle.set(SAVE_STATE_KEY_IS_USER_ADMIN, mIsUserAdmin)
        savedStateHandle.set(SAVE_STATE_KEY_SEND_METHOD, mSendMethod)
    }

    var title: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_TITLE)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_TITLE, value)
        }

    var replyUid: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_REPLY_UID)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_REPLY_UID, value)
        }

    var text: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_TEXT)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_TEXT, value)
        }

    var body: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_BODY)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_BODY, value)
        }

    var dest: User
        get(): User {
            return savedStateHandle.get(SAVE_STATE_KEY_DEST)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_DEST, value)
        }

    var sender: User
        get(): User {
            return savedStateHandle.get(SAVE_STATE_KEY_SENDER)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_SENDER, value)
        }

    var isUserAdmin: Boolean
        get(): Boolean {
            return savedStateHandle.get(SAVE_STATE_KEY_IS_USER_ADMIN)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_IS_USER_ADMIN, value)
        }

    var sendMethod: SendMethod
        get(): SendMethod {
            return savedStateHandle.get(SAVE_STATE_KEY_SEND_METHOD)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_SEND_METHOD, value)
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
}
