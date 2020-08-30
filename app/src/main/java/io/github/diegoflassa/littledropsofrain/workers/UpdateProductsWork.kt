package io.github.diegoflassa.littledropsofrain.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.github.diegoflassa.littledropsofrain.MainActivity
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.dao.ProductDao
import io.github.diegoflassa.littledropsofrain.helpers.Helper
import io.github.diegoflassa.littledropsofrain.xml.ProductParser

class UpdateProductsWork(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val appContext: Context = context

    // Notification channel ID.
    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"

    // Notification manager.
    private var mNotifyManager: NotificationManager? = null

    override fun doWork(): Result {

        // Create the notification channel.
        createNotificationChannel()

        // Set up the notification content intent to launch the app when
        // clicked.

        // Set up the notification content intent to launch the app when
        // clicked.
        val contentPendingIntent = PendingIntent.getActivity(
            appContext, 0, Intent(
                appContext,
                MainActivity::class.java
            ),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(appContext, PRIMARY_CHANNEL_ID)
            .setContentTitle(appContext.getString(R.string.worker_scheduled))
            .setContentText(appContext.getString(R.string.worker_running))
            .setContentIntent(contentPendingIntent)
            .setSmallIcon(R.drawable.ic_worker_running)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        mNotifyManager!!.notify(0, builder.build())

        var result= Result.success()
        try {
            val productParser = ProductParser()
            val products = productParser.parse()
            ProductDao.insertAll(Helper.iluriaProductToProduct(products), true)
        }catch (ex: Exception){
            result =  Result.retry()
        }
        return result

    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    private fun createNotificationChannel() {

        // Create a notification manager object.
        mNotifyManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                appContext.getString(R.string.woker_notification),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = appContext.getString(R.string.notification_worker_channel_description)
            mNotifyManager!!.createNotificationChannel(notificationChannel)
        }
    }
}