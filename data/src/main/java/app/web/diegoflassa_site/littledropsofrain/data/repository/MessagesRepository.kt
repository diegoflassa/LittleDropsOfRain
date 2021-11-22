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
package app.web.diegoflassa_site.littledropsofrain.data.repository

import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessagesDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataFailureListener
import com.google.firebase.firestore.DocumentReference
import java.util.*

class MessagesRepository(private val messagesDao: MessagesDao) {

    private val tag: String? = MessagesRepository::class.simpleName

    fun loadAll(listener: OnDataChangeListener<List<Message>>) {
        Log.i(tag, "loadAll")
        messagesDao.loadAll(listener)
    }

    fun loadAllByIds(
        messageIds: List<String>,
        listener: OnDataChangeListener<List<Message>>
    ) {
        Log.i(tag, "loadAllByIds")
        messagesDao.loadAllByIds(messageIds, listener)
    }

    fun findByContent(
        content: String,
        listener: OnDataChangeListener<List<Message>>
    ) {
        Log.i(tag, "findByContent")
        messagesDao.findByContent(content, listener)
    }

    fun findByCreationDate(
        date: Date,
        listener: OnDataChangeListener<List<Message>>
    ) {
        Log.i(tag, "findByCreationDate")
        messagesDao.findByCreationDate(date, listener)
    }

    fun findByRead(
        read: Boolean,
        listener: OnDataChangeListener<List<Message>>
    ) {
        Log.i(tag, "findByRead")
        messagesDao.findByRead(read, listener)
    }

    fun insertAll(
        messages: List<Message>,
        onSuccessListener: OnDataChangeListener<DocumentReference>? = null,
        onFailureListener: OnDataFailureListener<Exception>? = null
    ) {
        Log.i(tag, "insertAll")
        messagesDao.insertAll(messages, onSuccessListener, onFailureListener)
    }

    fun insert(
        message: Message,
        onSuccessListener: OnDataChangeListener<DocumentReference>? = null,
        onFailureListener: OnDataFailureListener<Exception>? = null
    ) {
        Log.i(tag, "insert")
        messagesDao.insert(message, onSuccessListener, onFailureListener)
    }

    fun update(
        message: Message,
        onSuccessListener: OnDataChangeListener<Void?>? = null,
        onFailureListener: OnDataFailureListener<Exception>? = null
    ) {
        Log.i(tag, "update")
        messagesDao.update(message, onSuccessListener, onFailureListener)
    }

    fun delete(message: Message?) {
        Log.i(tag, "delete")
        messagesDao.delete(message)
    }

    fun deleteAll() {
        Log.i(tag, "deleteAll")
        messagesDao.deleteAll()
    }
}
