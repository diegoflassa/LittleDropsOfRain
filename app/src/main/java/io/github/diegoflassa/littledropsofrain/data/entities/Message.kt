package io.github.diegoflassa.littledropsofrain.data.entities

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ServerTimestamp
import org.parceler.Parcel
import java.util.*


@Parcel(Parcel.Serialization.BEAN)
data class Message (
    var uid : String? = null,
    var emailSender : String? = FirebaseAuth.getInstance().currentUser!!.email,
    var sender : String? = FirebaseAuth.getInstance().currentUser!!.displayName,
    var senderId : String? = FirebaseAuth.getInstance().currentUser!!.uid,
    var title : String? = null,
    var message : String? = null,
    @ServerTimestamp
    var creationDate : Timestamp? = Timestamp.now(),
    var read : Boolean? = false
){

    companion object{
        private const val UID= "uid"
        private const val EMAIL_SENDER= "emailSender"
        private const val SENDER= "sender"
        private const val SENDER_ID= "senderId"
        private const val TITLE= "title"
        private const val MESSAGE= "message"
        private const val CREATION_DATE= "creationDate"
        private const val READ= "read"
    }

    constructor(map: Map<String, Any>) : this() {
        fromMap(map)
    }

    fun toMap(): Map<String, Any?> {
        val result: HashMap<String, Any?> = HashMap()
        result[UID] = uid
        result[EMAIL_SENDER] = emailSender
        result[SENDER] = sender
        result[SENDER_ID] = senderId
        result[TITLE] = title
        result[MESSAGE] = message
        result[CREATION_DATE] = creationDate
        result[READ] = read
        return result
    }

    private fun fromMap(map: Map<String, Any>){
        uid = map[UID] as String?
        emailSender = map[EMAIL_SENDER] as String?
        sender = map[SENDER] as String?
        senderId = map[SENDER_ID] as String?
        title = map[TITLE] as String?
        message = map[MESSAGE] as String?
        creationDate = map[CREATION_DATE] as Timestamp?
        read = map[READ] as Boolean?
    }
}