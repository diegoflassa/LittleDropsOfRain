package app.web.diegoflassa_site.littledropsofrain.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.helpers.LoggedUser

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

