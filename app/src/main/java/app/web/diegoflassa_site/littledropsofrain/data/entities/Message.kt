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

package app.web.diegoflassa_site.littledropsofrain.data.entities

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import app.web.diegoflassa_site.littledropsofrain.helpers.LoggedUser
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Keep
enum class MessageType(private val method: String) {
    MESSAGE("message"),
    NOTIFICATION("notification"),
    UNKNOWN("Unknown");

    override fun toString(): String {
        return method
    }
}

@Keep
@Parcelize
@Suppress("Unchecked_Cast")
data class Message(
    var uid: String? = null,
    var replyUid: String? = null,
    var imageUrl: String? = null,
    var owners: MutableList<String> = ArrayList(),
    var emailSender: String? = LoggedUser.userLiveData.value!!.email,
    var emailTo: String? = null,
    var sender: String? = LoggedUser.userLiveData.value!!.name,
    var senderId: String? = LoggedUser.userLiveData.value!!.uid,
    var message: String? = null,
    var _type: String? = MessageType.UNKNOWN.toString(),
    @ServerTimestamp
    var creationDate: Timestamp? = Timestamp.now(),
    var read: Boolean? = false,
    var fetched: Boolean? = false,
) : Parcelable {

    @IgnoredOnParcel
    var type = _type
        set(value) {
            fetched = (value != MessageType.MESSAGE.toString())
            field = value
        }

    companion object {
        const val UID = "uid"
        private const val REPLY_UID = "replyUid"
        private const val IMAGE_URL = "imageUrl"
        const val EMAIL_SENDER = "emailSender"
        const val EMAIL_TO = "emailTo"
        const val OWNERS = "owners"
        private const val SENDER = "sender"
        private const val SENDER_ID = "senderId"
        private const val MESSAGE = "message"
        const val TYPE = "type"
        const val CREATION_DATE = "creationDate"
        const val READ = "read"
        const val FETCHED = "fetched"
    }

    constructor(map: Map<String, Any>) : this() {
        fromMap(map)
    }

    fun toMap(): Map<String, Any?> {
        val result: HashMap<String, Any?> = HashMap()
        result[UID] = uid
        result[REPLY_UID] = replyUid
        result[IMAGE_URL] = imageUrl
        result[OWNERS] = owners
        result[EMAIL_SENDER] = emailSender
        result[EMAIL_TO] = emailTo
        result[SENDER] = sender
        result[SENDER_ID] = senderId
        result[MESSAGE] = message
        result[TYPE] = type
        result[CREATION_DATE] = creationDate
        result[READ] = read
        result[FETCHED] = fetched
        return result
    }

    private fun fromMap(map: Map<String, Any>) {
        uid = map[UID] as String?
        replyUid = map[REPLY_UID] as String?
        imageUrl = map[IMAGE_URL] as String?
        owners = map[OWNERS] as MutableList<String>
        emailSender = map[EMAIL_SENDER] as String?
        emailTo = map[EMAIL_TO] as String?
        sender = map[SENDER] as String?
        senderId = map[SENDER_ID] as String?
        message = map[MESSAGE] as String?
        _type = map[TYPE] as String?
        creationDate = map[CREATION_DATE] as Timestamp?
        read = map[READ] as Boolean?
        fetched = map[FETCHED] as Boolean?
    }

    fun getImageUrlAsUri(): Uri {
        return Uri.parse(imageUrl)
    }
}
