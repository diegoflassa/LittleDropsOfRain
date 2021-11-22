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
import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnItemInsertedListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnTaskFinishedListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("UNUSED", "BlockingMethodInNonBlockingContext", "SameParameterValue")
object CategoriesDao {

    private val TAG: String? = CategoriesDao::class.simpleName
    private const val FIREBASE_STORAGE: String = "https://firebasestorage"
    private const val LDOR_SITE: String = "gs://littledropsofrain-site.appspot.com"
    private val ioScope = CoroutineScope(Dispatchers.IO)
    const val COLLECTION_PATH: String = "categories"
    private var db: WeakReference<FirebaseFirestore> =
        WeakReference(FirebaseFirestore.getInstance())
    private var storage = Firebase.storage

    fun loadAll(listener: OnDataChangeListener<List<CategoryItem>>): Task<QuerySnapshot>? {
        val categories: MutableList<CategoryItem> = ArrayList()
        return db.get()?.collection(COLLECTION_PATH)
            ?.get()
            ?.addOnSuccessListener { result ->
                var category: CategoryItem
                for (document in result) {
                    category = document.toObject(CategoryItem::class.java)
                    if (category.uid == null) {
                        category.uid = document.id
                    }
                    categories.add(category)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                listener.onDataChanged(categories)
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun insertAll(
        categories: List<CategoryItem>,
        listener: OnItemInsertedListener<CategoryItem>? = null,
        finishListener: OnTaskFinishedListener<List<CategoryItem>>? = null
    ) {
        val hashCategories: MutableMap<String, CategoryItem> = HashMap<String, CategoryItem>(categories.size)
        val listTasksAll = ArrayList<Task<*>>(categories.size)
        db.get()?.collection(COLLECTION_PATH)
            ?.orderBy(Product.ID_SOURCE, Query.Direction.DESCENDING)
            ?.get()
            ?.addOnSuccessListener { result ->

                for (category in categories) {
                    val data = category.toMap()
                    if (category.uid == null) {
                        productIdEqualsNull(
                            category,
                            result,
                            data,
                            listTasksAll,
                            hashCategories,
                            listener
                        )
                    } else {
                        productIdNotEqualsNull(
                            category,
                            data,
                            listTasksAll,
                            hashCategories,
                            listener
                        )
                    }
                }

                finishListener?.onTaskFinished(ArrayList(hashCategories.values))
            }
    }

    private fun productIdEqualsNull(
        category: CategoryItem,
        result: QuerySnapshot,
        data: Map<String, Any?>,
        listTasksAll: MutableList<Task<*>>,
        hashCategories: MutableMap<String, CategoryItem>,
        listener: OnItemInsertedListener<CategoryItem>?
    ) {
        val taskInsert: Task<*>?
        var found = false
        lateinit var categoryFS: CategoryItem
        for (document in result) {
            categoryFS = document.toObject(CategoryItem::class.java)
            if (categoryFS.uid == null) {
                categoryFS.uid = document.id
            }
            if (category.uid == categoryFS.uid) {
                found = true
                break
            }
        }
        if (!found) {
            taskInsert =
                db.get()?.collection(COLLECTION_PATH)
                    ?.add(data)
                    ?.addOnSuccessListener {
                        category.uid = it.id
                        hashCategories[category.uid!!] = category
                        if (!category.imageUrl?.startsWith(FIREBASE_STORAGE)!! || !category.imageUrl?.startsWith(
                                LDOR_SITE
                            )!!
                        )
                            insertBlob(category)
                        listener?.onItemInserted(category)
                        Log.i(
                            TAG,
                            "Product ${category.uid} inserted successfully"
                        )
                    }?.addOnFailureListener {
                        Log.i(TAG, "Error inserting product")
                    }

            listTasksAll.add(taskInsert!!)
        } else {
            category.uid = categoryFS.uid
            hashCategories[category.uid!!] = category
            taskInsert = insert(category, listener)
            listTasksAll.add(taskInsert)
        }
    }

    private fun productIdNotEqualsNull(
        category: CategoryItem,
        data: Map<String, Any?>,
        listTasksAll: MutableList<Task<*>>,
        hashCategories: MutableMap<String, CategoryItem>,
        listener: OnItemInsertedListener<CategoryItem>?
    ) {
        val taskInsert: Task<*>?
        if (!category.imageUrl?.startsWith(FIREBASE_STORAGE)!! || !category.imageUrl?.startsWith(
                LDOR_SITE
            )!!
        ) {
            insertBlob(category)
        }
        taskInsert =
            db.get()?.collection(COLLECTION_PATH)?.document(category.uid!!)
                ?.set(data)
                ?.addOnSuccessListener {
                    Log.i(
                        TAG,
                        "[insert]Product ${category.uid} updated successfully"
                    )
                    hashCategories[category.uid!!] = category
                    listener?.onItemInserted(category)
                }?.addOnFailureListener {
                    Log.i(TAG, "Error updating product")
                }
        listTasksAll.add(taskInsert!!)
    }

    private fun insert(category: CategoryItem, listener: OnItemInsertedListener<CategoryItem>? = null): Task<*> {
        val task: Task<*>
        val data = category.toMap()
        if (category.uid == null) {
            task =
                db.get()?.collection(COLLECTION_PATH)
                    ?.whereEqualTo(CategoryItem.UID, category.uid)
                    ?.get()?.addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            db.get()?.collection(COLLECTION_PATH)?.add(data)?.addOnSuccessListener {
                                category.uid = it.id
                                if (!category.imageUrl?.startsWith(FIREBASE_STORAGE)!! || !category.imageUrl?.startsWith(
                                        LDOR_SITE
                                    )!!
                                )
                                    insertBlob(category)
                                listener?.onItemInserted(category)
                                Log.i(TAG, "Product ${category.uid} inserted successfully")
                            }?.addOnFailureListener {
                                Log.i(TAG, "Error inserting product")
                            }
                        } else {
                            category.uid = querySnapshot.documents[0].id
                            insert(category)
                        }
                    }!!
        } else {
            if (!category.imageUrl?.startsWith(FIREBASE_STORAGE)!! || !category.imageUrl?.startsWith(
                    LDOR_SITE
                )!!
            )
                insertBlob(category)
            task = db.get()?.collection(COLLECTION_PATH)?.document(category.uid!!)?.set(data)
                ?.addOnSuccessListener {
                    Log.i(TAG, "[insert]Product ${category.uid} updated successfully")
                }?.addOnFailureListener {
                    Log.i(TAG, "Error updating product")
                }!!
        }
        return task
    }

    private fun removeBlob(product: Product): Task<Void> {
        val reference = storage.reference.child("$COLLECTION_PATH/${product.uid}.jpg")
        return reference.delete().addOnSuccessListener {
            Log.d(
                TAG,
                "[insertBlob]Image successfully removed for product ${product.uid} at ${product.imageUrl}"
            )
        }.addOnFailureListener {
            Log.d(TAG, "Error deleting image ${product.uid}")
        }
    }

    private fun insertBlob(category: CategoryItem) {
        ioScope.launch {
            val reference = storage.reference.child("$COLLECTION_PATH/${category.uid}.jpg")
            val client = OkHttpClient()
            client.setConnectTimeout(30, TimeUnit.SECONDS) // connect timeout
            client.setReadTimeout(30, TimeUnit.SECONDS) // socket timeout
            val request = Request.Builder().url(category.imageUrl!!).build()
            val response = client.newCall(request).execute()
            reference.putStream(response.body().byteStream()).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception.let {
                        throw it!!
                    }
                }
                reference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    category.imageUrl = task.result.toString()
                    update(category, false)
                    Log.d(
                        TAG,
                        "[insertBlob]Image successfully saved for product ${category.uid} at ${category.imageUrl}"
                    )
                } else {
                    Log.d(TAG, "Unable to upload image ${category.uid}")
                }
                response.body().close()
            }
        }
    }

    fun update(category: CategoryItem, checkForUrl: Boolean = true): Task<Void>? {
        val data = category.toMap()
        return db.get()?.collection(COLLECTION_PATH)?.document(category.uid.toString())?.set(data)
            ?.addOnSuccessListener {
                if (checkForUrl && (
                    !category.imageUrl?.startsWith(FIREBASE_STORAGE)!! || !category.imageUrl?.startsWith(
                            LDOR_SITE
                        )!!
                    )
                )
                    insertBlob(category)
                Log.d(
                    TAG, "[update]Product ${category.uid} updated successfully"
                )
            }?.addOnFailureListener {
            }
    }

    fun delete(category: CategoryItem): Task<Void>? {
        return db.get()?.collection(COLLECTION_PATH)?.document(category.uid.toString())?.delete()
            ?.addOnSuccessListener {
                val reference = storage.reference
                reference.child("$COLLECTION_PATH/${category.uid}.jpg").delete()
                Log.i(TAG, "Message ${category.uid} deleted successfully")
            }
    }

    fun deleteAll(): Task<QuerySnapshot>? {
        val itemsRef = db.get()?.collection(COLLECTION_PATH)
        return itemsRef?.get()?.addOnCompleteListener { task ->
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
