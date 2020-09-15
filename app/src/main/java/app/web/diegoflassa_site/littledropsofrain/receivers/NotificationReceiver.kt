package app.web.diegoflassa_site.littledropsofrain.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import com.google.firebase.auth.FirebaseAuth

class NotificationReceiver : BroadcastReceiver() {

    companion object{
        const val ACTION_SAVE = "ACTION_SAVE"
        const val EXTRA_NID = "EXTRA_NID"
        const val EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI"
        const val EXTRA_TOPIC = "EXTRA_TOPIC"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action.equals(ACTION_SAVE)){
            val notificationId = intent.extras?.getInt(EXTRA_NID)
            val imageUri = intent.extras?.get(EXTRA_IMAGE_URI) as Uri?
            val topic = intent.extras?.getString(EXTRA_TOPIC)
            val title = intent.extras?.getString(EXTRA_TITLE)
            val message = intent.extras?.getString(EXTRA_MESSAGE)

            val messageToSave = Message()
            messageToSave.type = MessageType.NOTIFICATION.toString()
            messageToSave.owners.add(FirebaseAuth.getInstance().currentUser?.email!!)
            messageToSave.imageUrl = imageUri?.toString()
            messageToSave.message = topic?.replace("\\", "")  + System.lineSeparator() + title + System.lineSeparator() + message + System.lineSeparator() + imageUri.toString()
            MessageDao.insert(messageToSave)
            Helper.updateNotificationMessageSaved(context, imageUri, notificationId!!, title!!, message!!)
        }
    }
}

