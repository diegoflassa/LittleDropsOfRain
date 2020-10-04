package app.web.diegoflassa_site.littledropsofrain.services

import android.net.Uri
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.helpers.LoggedUser
import app.web.diegoflassa_site.littledropsofrain.workers.MyWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val TAG = MyFirebaseMessagingService::class.simpleName
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        val data = remoteMessage.data
        val from = remoteMessage.from

        val notification: RemoteMessage.Notification? = remoteMessage.notification
        if (notification != null) {
            notification.body?.let { handleNotification(remoteMessage) }
        }

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: $from")

        // Check if message contains a data payload.
        data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: $data")

            if (data.isNotEmpty()) {
                // Handle message within 10 seconds
                handleNow(remoteMessage)
            } else {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Short lived task is done.")
        handleNotification(remoteMessage)
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message body received.
     */
    private fun handleNotification(remoteMessage: RemoteMessage) {
        val notificationTitle: String
        val notificationBody: String
        var imageUri: Uri?
        if (remoteMessage.data.isNotEmpty()) {
            notificationTitle = remoteMessage.data["title"].toString()
            notificationBody = remoteMessage.data["body"].toString()
            imageUri = Uri.parse(remoteMessage.data["imageUri"].toString())
            if (imageUri.toString().contains("null")) {
                imageUri = null
            }
        } else {
            notificationTitle = remoteMessage.notification!!.title.toString()
            notificationBody = remoteMessage.notification!!.body.toString()
            imageUri = remoteMessage.notification!!.imageUrl!!
        }

        val messageToSave = Message()
        messageToSave.type = MessageType.NOTIFICATION.toString()
        messageToSave.owners.add(LoggedUser.firebaseUserLiveData.value?.email!!)
        messageToSave.imageUrl = imageUri?.toString()
        messageToSave.message =
            notificationTitle + System.lineSeparator() + System.lineSeparator() + notificationBody
        MessageDao.insert(messageToSave)

        Helper.showNotification(
            applicationContext,
            null,
            imageUri,
            notificationTitle,
            notificationBody,
            false
        )
    }
}