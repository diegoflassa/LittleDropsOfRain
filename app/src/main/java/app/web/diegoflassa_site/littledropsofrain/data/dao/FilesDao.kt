package app.web.diegoflassa_site.littledropsofrain.data.dao

import android.net.Uri
import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnFileUploadedFailureListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnFileUploadedListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Suppress("UNUSED")
object  FilesDao {
    private val TAG: String? = FilesDao::class.simpleName
    const val CACHE_DIR: String = "ldor-cache"
    const val COLLECTION_PATH: String = "notificationImages"
    private var storage = Firebase.storage

    fun remove(image: Uri?): Task<Void>? {
        var task : Task<Void>? = null
        if(image!=null) {
            val reference = storage.reference.child("${COLLECTION_PATH}/${image.lastPathSegment}")
            task= reference.delete().addOnSuccessListener {
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

    fun insert(image: Uri, listener : OnFileUploadedListener? = null, failureListener : OnFileUploadedFailureListener? = null) {
        val storageRef = storage.reference.child("${COLLECTION_PATH}/${image.lastPathSegment}")
        val uploadTask = storageRef.putFile(image)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    failureListener?.onFileUploadedFailure(image, it)
                    Log.d(TAG,"[insert]Error inserting file ${image.lastPathSegment} at $image")
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri : Uri= task.result
                listener?.onFileUploaded(image, downloadUri)
                Log.d(TAG,"[insert]File successfully inserted for ${image.lastPathSegment} at $image")
            } else {
                failureListener?.onFileUploadedFailure(image, task.exception)
            }
        }.addOnFailureListener {
            failureListener?.onFileUploadedFailure(image, it)
        }
    }
}