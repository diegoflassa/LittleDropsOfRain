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

package app.web.diegoflassa_site.littledropsofrain.services

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.work.*
import app.web.diegoflassa_site.littledropsofrain.workers.UpdateProductsWork
import java.util.concurrent.TimeUnit

class SetupProductsUpdateWorkerService : JobIntentService() {

    companion object {
        const val ACTION_SETUP_WORKER = "ACTION_SETUP_WORKER"
        private const val JOB_ID = 0

        fun setupWorker(context: Context) {
            val intent = Intent(context, SetupProductsUpdateWorkerService::class.java)
            intent.action = ACTION_SETUP_WORKER
            val comp =
                ComponentName(
                    context.packageName,
                    SetupProductsUpdateWorkerService::class.java.name
                )
            intent.component = comp
            enqueueWork(context, comp, JOB_ID, intent)
        }

        fun stopRunningWorker(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(UpdateProductsWork.TAG)
        }
    }

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
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UpdateProductsWork.TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            myWorkRequest
        )
    }
}
