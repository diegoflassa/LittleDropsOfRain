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

@file:Suppress("unused")

package app.web.diegoflassa_site.littledropsofrain.data.old.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Keep
@Parcelize
data class User(
    var imageUrl: String? = null,
    var email: String? = null,
    var name: String? = null,
    var providerId: String? = null,
    var uid: String? = null,
    @field:JvmField var isAdmin: Boolean = false,
    @ServerTimestamp
    var creationDate: Timestamp? = Timestamp.now(),
    @ServerTimestamp
    var lastSeen: Timestamp? = Timestamp.now()
) :
    Parcelable {

    companion object {
        const val UID = "uid"
        private const val IMAGE_URL = "imageUrl"
        private const val EMAIL = "email"
        private const val NAME = "name"
        private const val PROVIDER_ID = "providerId"
        private const val IS_ADMIN = "isAdmin"
        private const val CREATION_DATE = "creationDate"
        private const val LAST_SEEN = "lastSeen"

        fun fromString(text: String): User {
            val user = User()
            val separated = text.split("-")
            user.name = separated[0].trim()
            user.email = separated[1].trim()
            return user
        }
    }

    constructor(map: Map<String, Any>) : this() {
        fromMap(map)
    }

    override fun equals(other: Any?): Boolean {
        // self check
        if (this === other) return true
        // null check
        if (other == null) return false
        // type check and cast
        if (javaClass != other.javaClass) return false
        val user: User = other as User
        // field comparison
        return (
            Objects.equals(uid, user.uid) &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email)
            )
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (uid == null) 0 else uid.hashCode()
        result = prime * result + if (name == null) 0 else name.hashCode()
        result = prime * result + if (email == null) 0 else email.hashCode()
        return result
    }

    fun toMap(): Map<String, Any?> {
        val result: HashMap<String, Any?> = HashMap()
        result[UID] = uid
        result[IMAGE_URL] = imageUrl
        result[EMAIL] = email
        result[NAME] = name
        result[PROVIDER_ID] = providerId
        result[IS_ADMIN] = isAdmin
        result[CREATION_DATE] = creationDate
        result[LAST_SEEN] = lastSeen
        return result
    }

    private fun fromMap(map: Map<String, Any>) {
        uid = map[UID] as String?
        imageUrl = map[IMAGE_URL] as String?
        email = map[EMAIL] as String?
        name = map[NAME] as String?
        providerId = map[PROVIDER_ID] as String?
        isAdmin = map[IS_ADMIN] as Boolean
        creationDate = map[CREATION_DATE] as Timestamp?
        lastSeen = map[LAST_SEEN] as Timestamp?
    }

    override fun toString(): String {
        return "$name - $email"
    }
}
