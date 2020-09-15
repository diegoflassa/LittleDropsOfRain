package app.web.diegoflassa_site.littledropsofrain.helpers

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.entities.IluriaProduct
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.receivers.NotificationReceiver
import com.google.firebase.auth.FirebaseUser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("Unused", "ControlFlowWithEmptyBody")
class Helper {

    companion object {

        private val TAG: String? = Helper::class.simpleName
        private var NOTIFICATION_ID: Int = 0

        fun getDateTime(date: Date): String? {
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                sdf.format(date)
            } catch (e: Exception) {
                e.toString()
            }
        }

        fun firebaseUserToUser(user: FirebaseUser): User {
            val userFb = User()
            userFb.uid = user.uid
            userFb.name = user.displayName
            userFb.email = user.email
            userFb.imageUrl = user.photoUrl.toString()
            return userFb
        }

        fun iluriaProductToProduct(iluriaProducts: List<IluriaProduct>): List<Product> {
            val productsList: MutableList<Product> = ArrayList(iluriaProducts.size)
            for (iluriaProduct in iluriaProducts)
                productsList.add(iluriaProductToProduct(iluriaProduct))
            return productsList
        }

        private fun iluriaProductToProduct(iluriaProduct: IluriaProduct): Product {
            val product = Product()
            product.title = iluriaProduct.title
            val st = StringTokenizer(iluriaProduct.category, ">")
            while (st.hasMoreTokens()) {
                product.categories.add(st.nextToken().trim())
            }
            product.idIluria = iluriaProduct.idProduct
            val price = iluriaProduct.price!!.replace(',', '.')
            product.price = (price.toFloat() * 100).toInt()
            product.disponibility = iluriaProduct.disponibility
            product.imageUrl = iluriaProduct.image
            product.installment = iluriaProduct.installment
            product.linkProduct = iluriaProduct.linkProduct
            return product
        }

        /**
         * Get price represented as dollar signs.
         */
        fun getPriceString(price: Int): String? {
            return when (price) {
                in 0..5000 -> "$"
                in 5100..10000 -> "$$"
                in 10100..100000 -> "$$$"
                else -> "$$$"
            }
        }

        fun sendEmail(
            context: Context,
            sendTos: ArrayList<String>,
            subject: String = "",
            body: String = ""
        ) {
            val inlinedSendTos = sendTos.joinToString { it }
            val intent = Intent(
                Intent.ACTION_SENDTO,
                Uri.parse("mailto:$inlinedSendTos?subject:$subject?body:$body")
            )
            intent.putExtra(Intent.EXTRA_EMAIL, ArrayList(sendTos))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, body)
            try {
                context.startActivity(
                    Intent.createChooser(
                        intent,
                        context.getString(R.string.email_send)
                    )
                )
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.email_no_clients_installed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /**
         * Create and show a simple notification containing the received FCM message.
         *
         * @param title Title of the message to be sent
         * @param body Body of the message to be sent
         */
        fun showNotification(
            context: Context,
            imageUri: Uri?,
            title: String,
            body: String,
            canSave: Boolean = true
        ) {
            showNotification(context, imageUri, "", title, body, canSave)
        }

        fun showNotification(
            context: Context,
            imageUri: Uri?,
            topic: String,
            title: String,
            body: String,
            canSave: Boolean = true
        ) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0 /* Request code */,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )
            val saveNotificationIntent = Intent(context, NotificationReceiver::class.java).apply {
                action = NotificationReceiver.ACTION_SAVE
                putExtra(NotificationReceiver.EXTRA_NID, NOTIFICATION_ID)
                putExtra(NotificationReceiver.EXTRA_IMAGE_URI, imageUri)
                putExtra(NotificationReceiver.EXTRA_TOPIC, topic)
                putExtra(NotificationReceiver.EXTRA_TITLE, title)
                putExtra(NotificationReceiver.EXTRA_MESSAGE, body)
            }
            val saveNotificationPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_ID,
                    saveNotificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            val channelId = context.getString(R.string.default_notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val largeIcon = BitmapFactory.decodeResource(
                context.resources,
                R.mipmap.ic_notification_black
            )
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_notification)
                .setColor(context.getColor(R.color.colorAccent))
                .setContentTitle(title)
                .setContentText(context.getString(R.string.new_notification))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            if (canSave) {
                notificationBuilder.addAction(
                    android.R.drawable.ic_menu_save,
                    context.getString(R.string.save),
                    saveNotificationPendingIntent
                )
            }

            if (imageUri == null) {
                notificationBuilder.setLargeIcon(largeIcon)
                notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(body))
            } else {
                notificationBuilder.setContentText(body)
                var imageNotif: Bitmap?
                runBlocking {
                    val job: Job = launch(context = Dispatchers.IO) {
                        val client = OkHttpClient()
                        client.setConnectTimeout(30, TimeUnit.SECONDS) // connect timeout
                        client.setReadTimeout(30, TimeUnit.SECONDS)    // socket timeout
                        val request = Request.Builder().url(imageUri.toString()).build()
                        val response = client.newCall(request).execute()
                        imageNotif = BitmapFactory.decodeStream(response.body().byteStream())

                        notificationBuilder
                            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(imageNotif)
                            .bigLargeIcon(null)
                        )
                        notificationBuilder.setLargeIcon(imageNotif)
                    }
                    job.join()

                }
            }

            val notificationManagerCompat = NotificationManagerCompat.from(context)

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    context.getString(R.string.name_notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManagerCompat.createNotificationChannel(channel)
            }

            notificationManagerCompat.notify(
                NOTIFICATION_ID++ /* ID of notification */,
                notificationBuilder.build()
            )
        }

        fun updateNotificationMessageSaved(
            context: Context,
            imageUri: Uri?,
            notificationId: Int,
            title: String,
            message: String
        ) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0 /* Request code */,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )
            val channelId = context.getString(R.string.default_notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val largeIcon = BitmapFactory.decodeResource(
                context.resources,
                R.mipmap.ic_notification_black
            )
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_notification)
                .setColor(context.getColor(R.color.colorAccent))
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.new_notification))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_save, context.getString(R.string.saved), null)

            if (imageUri == null) {
                notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            } else {
                notificationBuilder.setContentText(message)
                var imageNotif: Bitmap? = null
                runBlocking {
                    val job: Job = launch(context = Dispatchers.IO) {
                        val client = OkHttpClient()
                        client.setConnectTimeout(30, TimeUnit.SECONDS) // connect timeout
                        client.setReadTimeout(30, TimeUnit.SECONDS)    // socket timeout
                        val request = Request.Builder().url(imageUri.toString()).build()
                        val response = client.newCall(request).execute()
                        imageNotif = BitmapFactory.decodeStream(response.body().byteStream())

                        notificationBuilder.setStyle(
                            NotificationCompat
                                .BigPictureStyle().bigPicture(imageNotif)
                                .bigLargeIcon(null)
                        )
                        notificationBuilder.setLargeIcon(imageNotif)
                    }
                    job.join()

                }
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(imageNotif)
                )
            }

            val notificationManagerCompat = NotificationManagerCompat.from(context)

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    context.getString(R.string.name_notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManagerCompat.createNotificationChannel(channel)
            }
            notificationManagerCompat.notify(
                notificationId,
                notificationBuilder.build()
            )
        }

        fun requestReadInternalStoragePermission(activity: Activity){
            requestPermission(activity, "android.permission.WRITE_INTERNAL_STORAGE")
        }

        fun requestReadExternalStoragePermission(activity: Activity){
            requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        private fun requestPermission(activity: Activity, permission: String){
            if (checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(activity, permission)) {
                    // Explain to the user why we need to read the contacts
                }

                val permissions : MutableList<String> = ArrayList<String>(1)
                permissions.add(permission)
                requestPermissions(activity, permissions.toTypedArray(), 0)

                return
            }
        }

        fun rotateDrawable(d: Drawable, angle: Float): Drawable? {
            // Use LayerDrawable, because it's simpler than RotateDrawable.
            val arD: Array<Drawable> = arrayOf(d)
            return object : LayerDrawable(arD) {
                override fun draw(canvas: Canvas) {
                    canvas.save()
                    canvas.rotate(angle)
                    super.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }
}
