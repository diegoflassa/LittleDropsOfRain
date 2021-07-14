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

package app.web.diegoflassa_site.littledropsofrain.data.dao

import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataFailureListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnUserFoundListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnUsersLoadedListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import java.lang.ref.WeakReference

@Suppress("UNUSED", "BlockingMethodInNonBlockingContext", "SameParameterValue")
object UserDao {

    private const val TAG: String = "UserDao"
    const val COLLECTION_PATH: String = "users"
    private val db: WeakReference<FirebaseFirestore> =
        WeakReference(FirebaseFirestore.getInstance())

    fun loadAll(listener: OnUsersLoadedListener): Task<QuerySnapshot>? {
        val users: MutableList<User> = ArrayList()
        return db.get()?.collection(COLLECTION_PATH)
            ?.get()
            ?.addOnSuccessListener { result ->
                var user: User
                for (document in result) {
                    user = document.toObject(User::class.java)
                    Log.d(TAG, "${document.id} => ${document.data}")
                    users.add(user)
                }
                listener.onUsersLoaded(users)
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting users:", exception)
            }
    }

    fun loadAllByIds(
        userIds: List<String>,
        listener: OnUsersLoadedListener
    ): Task<QuerySnapshot>? {
        val users: MutableList<User> = ArrayList()
        val itemsRef = db.get()?.collection(COLLECTION_PATH)?.whereIn(User.UID, userIds)
        return itemsRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var user: User
                for (document in task.result!!) {
                    user = document.toObject(User::class.java)
                    users.add(user)
                }
                listener.onUsersLoaded(users)
            } else {
                Log.d(TAG, "Error deleting documents: ", task.exception)
            }
        }
    }

    fun findByName(name: String?, listener: OnUsersLoadedListener): Task<QuerySnapshot>? {
        val users: MutableList<User> = ArrayList()
        return db.get()?.collection(COLLECTION_PATH)?.whereEqualTo("name", name)
            ?.get()
            ?.addOnSuccessListener { result ->
                var user: User
                for (document in result) {
                    user = document.toObject(User::class.java)
                    Log.d(TAG, "${document.id} => ${document.data}")
                    users.add(user)
                }
                listener.onUsersLoaded(users)
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting users: ", exception)
            }
    }

    fun findByEMail(email: String?, foundListener: OnUserFoundListener): Task<QuerySnapshot>? {
        var userFound: User? = null
        return db.get()?.collection(COLLECTION_PATH)?.whereEqualTo("email", email)
            ?.get()
            ?.addOnSuccessListener { result ->
                if (result.size() == 1) {
                    userFound = result.documents[0].toObject(User::class.java)!!
                    Log.d(TAG, "${result.documents[0].id} => ${result.documents[0].data}")
                }
                foundListener.onUserFound(userFound)
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting users: ", exception)
            }
    }

    fun insertAll(
        vararg users: User,
        onSuccessListener: OnDataChangeListener<Void?>? = null,
        onFailureListener: OnDataFailureListener<Exception>? = null
    ): ArrayList<Task<Void>?> {
        val tasks = ArrayList<Task<Void>?>()
        for (user in users) {
            tasks.add(insertOrUpdate(user, onSuccessListener, onFailureListener))
        }
        return tasks
    }

    fun insertOrUpdate(
        user: User,
        onSuccessListener: OnDataChangeListener<Void?>? = null,
        onFailureListener: OnDataFailureListener<Exception>? = null
    ): Task<Void>? {
        val data = user.toMap()
        return db.get()?.collection(COLLECTION_PATH)?.document(user.uid.toString())
            ?.set(data, SetOptions.merge())?.addOnSuccessListener {
                onSuccessListener?.onDataChanged(it)
            }?.addOnFailureListener {
                onFailureListener?.onDataFailure(it)
            }
    }

    fun delete(user: User?): Task<Void>? {
        return db.get()?.collection(COLLECTION_PATH)?.document(user?.uid.toString())?.delete()
            ?.addOnSuccessListener {
                Log.i(TAG, "User ${user?.uid} deleted successfully")
            }
    }

    fun deleteAll() {
        val itemsRef = db.get()?.collection(COLLECTION_PATH)
        itemsRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    itemsRef.document(document.id).delete()
                }
            } else {
                Log.d(TAG, "Error deleting documents: ", task.exception)
            }
        }
    }
}
