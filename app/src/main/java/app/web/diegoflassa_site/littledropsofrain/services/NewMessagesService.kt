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
import android.content.Intent
import androidx.core.app.JobIntentService
import app.web.diegoflassa_site.littledropsofrain.MyApplication
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import com.google.firebase.firestore.*

class NewMessagesService : JobIntentService(), EventListener<QuerySnapshot> {

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mQuery: Query

    companion object {
        const val ACTION_SETUP_LISTENER = "ACTION_SETUP_LISTENER"
        private const val JOB_ID = 0

        fun setupListener() {
            val intent = Intent(MyApplication.getContext(), NewMessagesService::class.java)
            intent.action = ACTION_SETUP_LISTENER
            val comp =
                ComponentName(MyApplication.getContext().packageName, NewMessagesService::class.java.name)
            intent.component = comp
            enqueueWork(MyApplication.getContext(), comp, JOB_ID, intent)
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
                ACTION_SETUP_LISTENER -> {
                    initFirestore()
                    setupListener()
                }
            }
        }
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
            MyApplication.getContext(),
            MyApplication.getContext().getString(R.string.new_message),
            message.message!!,
            false
        )
        message.uid = change.document.id
        message.fetched = true
        MessageDao.update(message)
    }
}
