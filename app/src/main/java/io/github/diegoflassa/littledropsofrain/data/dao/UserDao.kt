package io.github.diegoflassa.littledropsofrain.data.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.github.diegoflassa.littledropsofrain.interfaces.OnDataChangeListener
import io.github.diegoflassa.littledropsofrain.interfaces.OnDataFailureListener
import io.github.diegoflassa.littledropsofrain.interfaces.OnUserFoundListener
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.interfaces.OnUsersLoadedListener
import java.lang.ref.WeakReference
import java.util.*

object UserDao {

    private const val TAG: String = "UserDao"
    const val COLLECTION_PATH: String = "users"
    private val db : WeakReference<FirebaseFirestore> = WeakReference(FirebaseFirestore.getInstance())


    fun loadAll(listener: OnUsersLoadedListener){
        val users: MutableList<User> = ArrayList()
       db.get()?.collection(COLLECTION_PATH)
           ?.get()
           ?.addOnSuccessListener { result ->
                var user : User
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

    fun loadAllByIds(messageIds: List<String>, listener: OnUsersLoadedListener){
        val users: MutableList<User> = ArrayList()
        val itemsRef =db.get()?.collection(COLLECTION_PATH)
        itemsRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var user: User
                for (document in task.result) {
                    if (messageIds.contains(document.id)) {
                        user = document.toObject(User::class.java)
                        users.add(user)
                    }
                }
                listener.onUsersLoaded(users)
            } else {
                Log.d(TAG, "Error deleting documents: ", task.exception)
            }
        }
    }

    fun findByName(name: String?, listener: OnUsersLoadedListener) {
        val users: MutableList<User> = ArrayList()
        db.get()?.collection(COLLECTION_PATH)?.whereEqualTo("name", name)
           ?.get()
           ?.addOnSuccessListener { result ->
                var user : User
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


    fun findByEMail(email: String?, foundListener: OnUserFoundListener) {
       var userFound: User? = null
       db.get()?.collection(COLLECTION_PATH)?.whereEqualTo("email", email)
           ?.get()
           ?.addOnSuccessListener { result ->
                if(result.size()==1) {
                    userFound = result.documents[0].toObject(User::class.java)!!
                    Log.d(TAG, "${result.documents[0].id} => ${result.documents[0].data}")
                }
                foundListener.onUserFound(userFound)
            }
           ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting users: ", exception)
            }
    }

    fun insertAll(vararg users: User,
                  onSuccessListener: OnDataChangeListener<Void?>? = null,
                  onFailureListener: OnDataFailureListener<Exception>? = null) {
        for( user in users) {
            insert(user, onSuccessListener, onFailureListener)
        }
    }

    fun insert(user: User,
               onSuccessListener: OnDataChangeListener<Void?>? = null,
               onFailureListener: OnDataFailureListener<Exception>? = null
    ) {
        val data = user.toMap()
       db.get()?.collection(COLLECTION_PATH)?.document(user.uid.toString())?.set(data, SetOptions.merge())?.addOnSuccessListener{
            onSuccessListener?.onDataLoaded(it)
        }?.addOnFailureListener{
            onFailureListener?.onDataFailure(it)
        }
    }

    fun update(user: User,
               onSuccessListener: OnDataChangeListener<Void?>? = null,
               onFailureListener: OnDataFailureListener<Exception>? = null
    ) {
        val data = user.toMap()
        db.get()?.collection(COLLECTION_PATH)?.document(user.uid.toString())?.set(data, SetOptions.merge())?.addOnSuccessListener{
            onSuccessListener?.onDataLoaded(it)
        }?.addOnFailureListener{
            onFailureListener?.onDataFailure(it)
        }
    }

    fun delete(user: User?) {
       db.get()?.collection(COLLECTION_PATH)?.document(user?.uid.toString())?.delete()?.addOnSuccessListener {
            Log.i(TAG, "User ${user?.uid} deleted successfully")
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