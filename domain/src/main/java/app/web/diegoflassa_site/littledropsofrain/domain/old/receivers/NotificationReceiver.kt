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

package app.web.diegoflassa_site.littledropsofrain.domain.old.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import app.web.diegoflassa_site.littledropsofrain.data.old.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.domain.old.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.domain.old.helpers.LoggedUser

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SAVE = "ACTION_SAVE"
        const val EXTRA_NID = "EXTRA_NID"
        const val EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(ACTION_SAVE)) {
            val notificationId = intent.extras?.getInt(EXTRA_NID)
            val imageUri = intent.extras?.get(EXTRA_IMAGE_URI) as Uri?
            val title = intent.extras?.getString(EXTRA_TITLE)
            val message = intent.extras?.getString(EXTRA_MESSAGE)

            val messageToSave = Message()
            messageToSave.emailSender = LoggedUser.userLiveData.value!!.email
            messageToSave.sender = LoggedUser.userLiveData.value!!.name
            messageToSave.senderId = LoggedUser.userLiveData.value!!.uid
            messageToSave.fetched = true
            messageToSave.type = MessageType.NOTIFICATION.toString()
            messageToSave.owners.add(LoggedUser.userLiveData.value?.email!!)
            messageToSave.imageUrl = imageUri?.toString()
            messageToSave.message =
                title + System.lineSeparator() + System.lineSeparator() + message
            MessageDao.insert(messageToSave)
            Helper.updateNotificationMessageSaved(
                context,
                imageUri,
                notificationId!!,
                title!!,
                message!!
            )
        }
    }
}
