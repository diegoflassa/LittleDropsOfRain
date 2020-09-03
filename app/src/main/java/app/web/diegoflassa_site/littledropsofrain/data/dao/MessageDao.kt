package app.web.diegoflassa_site.littledropsofrain.data.dao

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataFailureListener
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import java.lang.ref.WeakReference
import java.util.*


//DFL - Classe de Acesso a dados. Aqui vc coloca as FORMAS DE ACESSAR os dados
object MessageDao {

    private const val TAG: String = "MessageDao"
    const val COLLECTION_PATH: String = "messages"
    private val db : WeakReference<FirebaseFirestore> = WeakReference(FirebaseFirestore.getInstance())

    fun loadAll(listener: OnDataChangeListener<List<Message>>){
        val messages: MutableList<Message> = ArrayList()
       db.get()?.collection(COLLECTION_PATH)?.orderBy("creationDate", Query.Direction.DESCENDING)
           ?.get()
           ?.addOnSuccessListener { result ->
                var message : Message
                for (document in result) {
                    message = document.toObject(Message::class.java)
                    message.uid = document.id
                    //message = Message(document.data)
                    Log.d(TAG, "${document.id} => ${document.data}")
                    messages.add(message)
                }
                listener.onDataLoaded(messages)
            }
           ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadAllByIds(messageIds: List<String>, listener: OnDataChangeListener<List<Message>>) {
        val messages: MutableList<Message> = ArrayList()
        val itemsRef =db.get()?.collection(COLLECTION_PATH)
        itemsRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var message: Message
                for (document in task.result) {
                    if (messageIds.contains(document.id)) {
                        message = document.toObject(Message::class.java)
                        message.uid = document.id
                        messages.add(message)
                    }
                }
                listener.onDataLoaded(messages)
            } else {
                Log.d(TAG, "Error deleting documents: ", task.exception)
            }
        }
    }

    fun findByContent(content: String, listener: OnDataChangeListener<List<Message>>) {
        val messages: MutableList<Message> = ArrayList()
       db.get()?.collection(COLLECTION_PATH)
           ?.get()
           ?.addOnSuccessListener { result ->
                var message : Message
                for (document in result) {
                    if(document.get("message").toString().contains(content)) {
                        message = document.toObject(Message::class.java)
                        message.uid = document.id
                        Log.d(TAG, "${document.id} => ${document.data}")
                        messages.add(message)
                    }
                }
                listener.onDataLoaded(messages)
           }
           ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting messages: ", exception)
           }
    }

    fun findByCreationDate(date: Date, listener: OnDataChangeListener<List<Message>>) {
        val creationDate= Timestamp(date)
        val messages: MutableList<Message> = ArrayList()
       db.get()?.collection(COLLECTION_PATH)?.whereEqualTo("creationDate", creationDate)
           ?.get()
           ?.addOnSuccessListener { result ->
                var message : Message
                for (document in result) {
                    message = document.toObject(Message::class.java)
                    message.uid = document.id
                    Log.d(TAG, "${document.id} => ${document.data}")
                    messages.add(message)
                }
                listener.onDataLoaded(messages)
            }
           ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting messages: ", exception)
            }
    }

    fun findByRead(read: Boolean, listener: OnDataChangeListener<List<Message>>) {
        val messages: MutableList<Message> = ArrayList()
       db.get()?.collection(COLLECTION_PATH)?.whereEqualTo("read", read.toString())
           ?.get()
           ?.addOnSuccessListener { result ->
                var message : Message
                for (document in result) {
                    message = document.toObject(Message::class.java)
                    message.uid = document.id
                    Log.d(TAG, "${document.id} => ${document.data}")
                    messages.add(message)
                }
                listener.onDataLoaded(messages)
            }
           ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting messages: ", exception)
            }
    }

    fun insertAll(vararg messages: Message,
                  onSuccessListener: OnDataChangeListener<DocumentReference>? = null,
                  onFailureListener: OnDataFailureListener<Exception>? = null) {
        for( message in messages) {
            insert(message, onSuccessListener, onFailureListener)
        }
    }

    fun insert(
        message: Message,
        onSuccessListener: OnDataChangeListener<DocumentReference>? = null,
        onFailureListener: OnDataFailureListener<Exception>? = null
    ) {
        val data = message.toMap()
       db.get()?.collection(COLLECTION_PATH)?.add(data)?.addOnSuccessListener{
            onSuccessListener?.onDataLoaded(it)
        }?.addOnFailureListener{
            onFailureListener?.onDataFailure(it)
        }
    }

    fun update(
        message: Message,
        onSuccessListener: OnDataChangeListener<Void?>? = null,
        onFailureListener: OnDataFailureListener<Exception>? = null
    ) {
        val data = message.toMap()
       db.get()?.collection(COLLECTION_PATH)?.document(message.uid.toString())?.set(data)?.addOnSuccessListener{
            onSuccessListener?.onDataLoaded(it)
        }?.addOnFailureListener{
            onFailureListener?.onDataFailure(it)
        }
    }

    fun delete(message: Message?) {
       db.get()?.collection(COLLECTION_PATH)?.document(message?.uid.toString())?.delete()?.addOnSuccessListener {
            Log.i(TAG, "Message ${message?.uid} deleted successfully")
        }
    }

    fun deleteAll() {
        val itemsRef =db.get()?.collection(COLLECTION_PATH)
        itemsRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    itemsRef.document(document.id).delete()
                }
            } else {
                Log.d(TAG, "Error deleting documents: ", task.exception)
            }
        }
    }
}