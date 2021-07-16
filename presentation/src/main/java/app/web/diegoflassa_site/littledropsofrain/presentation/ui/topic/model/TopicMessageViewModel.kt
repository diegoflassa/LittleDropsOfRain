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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.topic.model

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.data.entities.TopicMessage

class TopicMessageViewModel(state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SAVE_STATE_KEY_TITLE = "TOPIC_SAVE_STATE_KEY_TITLE"
        private const val SAVE_STATE_KEY_BODY = "TOPIC_SAVE_STATE_KEY_BODY"
        private const val SAVE_STATE_KEY_TOPICS = "TOPIC_SAVE_STATE_KEY_TOPICS"
        private const val SAVE_STATE_KEY_IMAGE_URI_FIRESTORE = "TOPIC_SAVE_STATE_KEY_IMAGE_URI_FIRESTORE"
        private const val SAVE_STATE_KEY_IMAGE_URI_LOCAL = "TOPIC_SAVE_STATE_KEY_URI_LOCAL"
    }

    private val savedStateHandle = state
    private var mTitle: String = ""
    private var mBody: String = ""
    private var mTopics: HashSet<TopicMessage.Topic> = HashSet()
    private var mImageUriFirestore: Uri? = null
    private var mImageUriLocal: Uri? = null

    init {
        savedStateHandle.set(SAVE_STATE_KEY_TITLE, mTitle)
        savedStateHandle.set(SAVE_STATE_KEY_BODY, mBody)
        savedStateHandle.set(SAVE_STATE_KEY_TOPICS, mTopics)
        savedStateHandle.set(SAVE_STATE_KEY_IMAGE_URI_FIRESTORE, mImageUriFirestore)
        savedStateHandle.set(SAVE_STATE_KEY_IMAGE_URI_LOCAL, mImageUriLocal)
    }

    var title: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_TITLE)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_TITLE, value)
        }

    var body: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_BODY)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_BODY, value)
        }

    var topics: HashSet<TopicMessage.Topic>
        get(): HashSet<TopicMessage.Topic> {
            return savedStateHandle.get(SAVE_STATE_KEY_TOPICS)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_TOPICS, value)
        }

    var imageUriFirestore: Uri?
        get(): Uri? {
            return savedStateHandle.get(SAVE_STATE_KEY_IMAGE_URI_FIRESTORE)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_IMAGE_URI_FIRESTORE, value)
        }

    var imageUriLocal: Uri?
        get(): Uri? {
            return savedStateHandle.get(SAVE_STATE_KEY_IMAGE_URI_LOCAL)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_IMAGE_URI_LOCAL, value)
        }
}
