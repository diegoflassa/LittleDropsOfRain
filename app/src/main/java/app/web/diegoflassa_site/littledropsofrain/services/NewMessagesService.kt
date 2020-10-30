package app.web.diegoflassa_site.littledropsofrain.services

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
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
        private lateinit var mContext: Context

        fun setupListener(context: Context) {
            mContext = context
            val intent = Intent(context, NewMessagesService::class.java)
            intent.action = ACTION_SETUP_LISTENER
            val comp =
                ComponentName(context.packageName, NewMessagesService::class.java.name)
            intent.component = comp
            enqueueWork(context, comp, JOB_ID, intent)
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
                //DocumentChange.Type.MODIFIED -> onDocumentModified(change)
                //DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
                else -> {//Do nothing
                }
            }
        }
    }

    private fun onDocumentAdded(change: DocumentChange) {
        val message = change.document.toObject(Message::class.java)
        Helper.showNotification(
            mContext,
            mContext.getString(R.string.new_message),
            message.message!!,
            false
        )
        message.uid = change.document.id
        message.fetched = true
        MessageDao.update(message)
    }


}