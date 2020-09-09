package app.web.diegoflassa_site.littledropsofrain.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.entities.IluriaProduct
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*

class Helper {

    companion object{

        private var NOTIFICATION_ID: Int = 0

        fun getDateTime(date: Date): String? {
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                sdf.format(date)
            } catch (e: Exception) {
                e.toString()
            }
        }

        fun firebaseUserToUser(user: FirebaseUser) : User {
            val userFb = User()
            userFb.uid = user.uid
            userFb.name = user.displayName
            userFb.email = user.email
            userFb.imageUrl = user.photoUrl.toString()
            return userFb
        }

        fun iluriaProductToProduct(iluriaProducts: List<IluriaProduct>) : List<Product>{
            val productsList : MutableList<Product> = ArrayList(iluriaProducts.size)
            for(iluriaProduct in iluriaProducts)
                productsList.add(iluriaProductToProduct(iluriaProduct))
            return productsList
        }

        private fun iluriaProductToProduct(iluriaProduct: IluriaProduct) : Product{
            val product = Product()
            product.title= iluriaProduct.title
            val st = StringTokenizer(iluriaProduct.category, ">")
            while(st.hasMoreTokens()){
                product.categories.add(st.nextToken().trim())
            }
            product.idIluria= iluriaProduct.idProduct
            val price= iluriaProduct.price!!.replace(',', '.')
            product.price= (price.toFloat()*100).toInt()
            product.disponibility= iluriaProduct.disponibility
            product.imageUrl= iluriaProduct.image
            product.installment= iluriaProduct.installment
            product.linkProduct= iluriaProduct.linkProduct
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
        ){
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
        fun sendNotification(context: Context, title: String, body: String) {
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
            val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_notification_large)
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(context.getColor(R.color.colorAccent))
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.new_notification))
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

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
    }
}