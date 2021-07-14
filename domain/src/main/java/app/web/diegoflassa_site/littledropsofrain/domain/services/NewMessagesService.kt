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
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.domain.R
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.Helper
import com.google.firebase.firestore.*

class NewMessagesService : JobService(), EventListener<QuerySnapshot> {

    companion object {
        const val JOB_ID = 456
    }

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mQuery: Query

    override fun onStartJob(params: JobParameters?): Boolean {
        initFirestore()
        setupListener()
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
    }

    private fun setupListener() {
        mQuery = mFirestore.collection(MessageDao.COLLECTION_PATH)
        mQuery.whereEqualTo(Message.FETCHED, false)
            .whereEqualTo(Message.TYPE, MessageType.MESSAGE.toString())
            .addSnapshotListener(this)
    }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        for (change in value?.documentChanges!!) {
            // Snapshot of the changed document
            when (change.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(change)
                // DocumentChange.Type.MODIFIED -> onDocumentModified(change)
                // DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
                else -> { // Do nothing
                }
            }
        }
    }

    private fun onDocumentAdded(change: DocumentChange) {
        val message = change.document.toObject(Message::class.java)
        Helper.showNotification(
            this,
            getString(R.string.new_message),
            message.message!!,
            false
        )
        message.uid = change.document.id
        message.fetched = true
        MessageDao.update(message)
    }
}
