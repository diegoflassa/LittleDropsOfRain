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

package app.web.diegoflassa_site.littledropsofrain.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.repository.IluriaProductsRepository
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnProductInsertedListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnTaskFinishedListener
import app.web.diegoflassa_site.littledropsofrain.parser.ProductParser
import kotlinx.coroutines.*

@Suppress("DeferredResultUnused")
class UpdateProductsWork(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams),
    ProductParser.OnParseProgress,
    OnProductInsertedListener,
    OnTaskFinishedListener<List<Product>> {

    companion object {
        val TAG: String = UpdateProductsWork::class.java.simpleName
        private const val NOTIFICATION_ID = 424242

        // Notification channel ID.
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        const val KEY_IN_REMOVE_NOT_FOUND = "in_remove_not_found"
        const val KEY_IN_UNPUBLISH_NOT_FOUND = "unpublish_not_found"
        const val KEY_PROGRESS = "progress"
        const val KEY_PRODUCT = "product"
        const val KEY_PRODUCTS = "products"
    }

    private val maxRetries: Int = 5
    private var currentTry: Int = 0
    private val appContext: Context = context

    // Notification manager.
    private var mNotifyManager: NotificationManagerCompat? = null

    @SuppressLint("UnspecifiedImmutableFlag")
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        // Create the notification channel.
        createNotificationChannel()

        // Set up the notification content intent to launch the app when
        // clicked.
        val contentPendingIntent = PendingIntent.getActivity(
            appContext, 0,
            Intent(
                appContext,
                MainActivity::class.java
            ),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(appContext, PRIMARY_CHANNEL_ID)
            .setContentTitle(appContext.getString(R.string.worker_scheduled))
            .setContentText(appContext.getString(R.string.worker_running))
            .setContentIntent(contentPendingIntent)
            .setColor(appContext.getColor(R.color.colorAccent))
            .setSmallIcon(R.mipmap.ic_worker_running)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        val notification = builder.build()
        mNotifyManager!!.notify(NOTIFICATION_ID, notification)

        val removeNotFound = inputData.getBoolean(KEY_IN_REMOVE_NOT_FOUND, false)
        val unpublishNotFound = inputData.getBoolean(KEY_IN_UNPUBLISH_NOT_FOUND, true)

        var result = Result.success()
        try {
            val ipr = IluriaProductsRepository()
            val products = ipr.getAll().blockingFirst().products
            ProductDao.insertAll(
                Helper.iluriaProductToProduct(products),
                removeNotFound,
                unpublishNotFound,
                this@UpdateProductsWork,
                this@UpdateProductsWork
            )
        } catch (ex: Exception) {
            if (currentTry < maxRetries) {
                currentTry++
                Log.e(TAG, ex.toString())
                result = Result.retry()
            } else {
                currentTry = 0
                result = Result.failure()
            }
        }
        mNotifyManager!!.cancel(NOTIFICATION_ID)
        result
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    private fun createNotificationChannel() {

        // Create a notification manager object.
        mNotifyManager = NotificationManagerCompat.from(applicationContext)

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
            notificationChannel.description =
                appContext.getString(R.string.notification_worker_channel_description)
            mNotifyManager!!.createNotificationChannel(notificationChannel)
        }
    }

    @DelicateCoroutinesApi
    override fun onParseProgressChange(progress: String) {
        val update = workDataOf(KEY_PROGRESS to progress)
        GlobalScope.async(Dispatchers.Default) { setProgress(update) }
    }

    @DelicateCoroutinesApi
    override fun onProductInserted(product: Product) {
        val update = workDataOf(KEY_PRODUCT to product.uid)
        GlobalScope.async(Dispatchers.Default) { setProgress(update) }
    }

    @DelicateCoroutinesApi
    override fun onTaskFinished(param: List<Product>) {
        val update = workDataOf(KEY_PRODUCTS to param.size)
        GlobalScope.async(Dispatchers.Default) { setProgress(update) }
    }
}
