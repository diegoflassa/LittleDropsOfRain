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

@file:Suppress("MemberVisibilityCanBePrivate")

package app.web.diegoflassa_site.littledropsofrain.data.dao

import android.net.Uri
import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedFailureListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

@Suppress("UNUSED")
object FilesDao {
    private val TAG: String? = FilesDao::class.simpleName
    const val CACHE_DIR: String = "ldor-cache"
    private const val COLLECTION_PATH: String = "notificationImages"
    private const val COLLECTION_PATH_USER_AVATAR: String = "userAvatars"
    private var storage = Firebase.storage

    fun remove(image: Uri?): Task<Void>? {
        var task: Task<Void>? = null
        if (image != null) {
            val reference: StorageReference =
                if (image.toString().contains(COLLECTION_PATH_USER_AVATAR)) {
                    storage.reference.child("$COLLECTION_PATH_USER_AVATAR/${image.lastPathSegment}")
                } else {
                    storage.reference.child("$COLLECTION_PATH/${image.lastPathSegment}")
                }
            task = reference.delete().addOnSuccessListener {
                Log.d(
                    TAG,
                    "[remove]File successfully removed for ${image.lastPathSegment} at $image"
                )
            }.addOnFailureListener {
                Log.d(TAG, "Error deleting file ${image.lastPathSegment}")
            }
        }
        return task
    }

    fun insert(
        image: Uri,
        listener: OnFileUploadedListener? = null,
        failureListener: OnFileUploadedFailureListener? = null,
        isUserAvatar: Boolean = false
    ) {
        val storageRef = if (isUserAvatar) {
            storage.reference.child("$COLLECTION_PATH_USER_AVATAR/${image.lastPathSegment}")
        } else {
            storage.reference.child("$COLLECTION_PATH/${image.lastPathSegment}")
        }
        val uploadTask = storageRef.putFile(image)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    failureListener?.onFileUploadedFailure(image, it)
                    Log.d(TAG, "[insert]Error inserting file ${image.lastPathSegment} at $image")
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri: Uri? = task.result
                if (downloadUri != null) {
                    listener?.onFileUploaded(image, downloadUri)
                    Log.d(
                        TAG,
                        "[insert]File successfully inserted for ${image.lastPathSegment} at $image"
                    )
                }
            } else {
                failureListener?.onFileUploadedFailure(image, task.exception)
            }
        }.addOnFailureListener {
            failureListener?.onFileUploadedFailure(image, it)
        }
    }
}
