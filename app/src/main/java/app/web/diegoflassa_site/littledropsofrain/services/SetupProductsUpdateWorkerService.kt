package app.web.diegoflassa_site.littledropsofrain.services

import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.work.*
import app.web.diegoflassa_site.littledropsofrain.workers.UpdateProductsWork
import java.util.concurrent.TimeUnit

class SetupProductsUpdateWorkerService : JobIntentService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // Define service as sticky so that it stays in background
        return Service.START_STICKY
    }

    override fun onHandleWork(intent: Intent) {
        intent.apply {
            when (intent.action) {
                ACTION_SETUP_WORKER -> {
                    setupWorker()
                }
            }
        }
    }

    private fun setupWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val myWorkRequest = PeriodicWorkRequest.Builder(
            UpdateProductsWork::class.java,
            12,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(11, TimeUnit.HOURS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(UpdateProductsWork.TAG, ExistingPeriodicWorkPolicy.REPLACE, myWorkRequest)
    }

    companion object {
        const val ACTION_SETUP_WORKER = "ACTION_SETUP_WORKER"

        fun setupWorker(context: Context) {
            val intent = Intent(context, SetupProductsUpdateWorkerService::class.java)
            intent.action = ACTION_SETUP_WORKER
            context.startService(intent)
        }
    }

}