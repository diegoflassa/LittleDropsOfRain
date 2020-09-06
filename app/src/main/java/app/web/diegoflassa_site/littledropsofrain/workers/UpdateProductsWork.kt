package app.web.diegoflassa_site.littledropsofrain.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnProductInsertedListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnTaskFinishedListener
import app.web.diegoflassa_site.littledropsofrain.xml.ProductParser
import kotlinx.coroutines.*

class UpdateProductsWork(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams), ProductParser.OnParseProgress, OnProductInsertedListener,
    OnTaskFinishedListener<List<Product>> {

    companion object {
        private const val NOTIFICATION_ID = 424242
        // Notification channel ID.
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        const val KEY_IN_REMOVE_NOT_FOUND = "in_remove_not_found"
        const val KEY_PROGRESS = "progress"
        const val KEY_PRODUCT = "product"
        const val KEY_PRODUCTS = "products"
    }

    private val appContext: Context = context

    // Notification manager.
    private var mNotifyManager: NotificationManager? = null

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        // Create the notification channel.
        createNotificationChannel()
        
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

        val notification = builder.build()
        mNotifyManager!!.notify(NOTIFICATION_ID, notification)

        val removeNotFound = inputData.getBoolean(KEY_IN_REMOVE_NOT_FOUND, false)

        var result= Result.success()
        try {
            val productParser = ProductParser(this@UpdateProductsWork)
            val products = productParser.parse()
            ProductDao.insertAll(Helper.iluriaProductToProduct(products), removeNotFound, this@UpdateProductsWork, this@UpdateProductsWork)
        }catch (ex: Exception){
            result =  Result.retry()
        }
        mNotifyManager!!.cancel(NOTIFICATION_ID)
        result
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

    override fun onParseProgressChange(progress: String) {
        val update = workDataOf(KEY_PROGRESS to progress)
        val async = GlobalScope.async(Dispatchers.Default) { setProgress(update) }
        async.isActive
    }

    override fun onProductInserted(product: Product) {
        val update = workDataOf(KEY_PRODUCT to product.uid)
        val async = GlobalScope.async(Dispatchers.Default) { setProgress(update) }
        async.isActive
    }

    override fun onTaskFinished(param: List<Product>) {
        val update = workDataOf(KEY_PRODUCTS to param.size)
        val async = GlobalScope.async(Dispatchers.Default) { setProgress(update) }
        async.isActive
    }
}