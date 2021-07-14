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

package app.web.diegoflassa_site.littledropsofrain.domain.services

import android.app.job.JobParameters
import android.app.job.JobService
import androidx.work.*
import app.web.diegoflassa_site.littledropsofrain.domain.workers.UpdateProductsWork
import java.util.concurrent.TimeUnit

class SetupProductsUpdateWorkerService : JobService() {

    companion object {
        const val JOB_ID = 123
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        setupWorker()
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
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
