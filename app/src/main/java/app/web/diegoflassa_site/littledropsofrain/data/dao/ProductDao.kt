package app.web.diegoflassa_site.littledropsofrain.data.dao

import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnProductInsertedListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnTaskFinishedListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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


//DFL - Classe de Acesso a dados. Aqui vc coloca as FORMAS DE ACESSAR os dados
@Suppress("UNUSED", "BlockingMethodInNonBlockingContext", "SameParameterValue")
object ProductDao {

    private val TAG: String? = ProductDao::class.simpleName
    private val ioScope = CoroutineScope(Dispatchers.IO)
    const val COLLECTION_PATH: String = "products"
    private var db: WeakReference<FirebaseFirestore> =
        WeakReference(FirebaseFirestore.getInstance())
    private var storage = Firebase.storage

    fun loadAll(listener: OnDataChangeListener<List<Product>>): Task<QuerySnapshot>? {
        val products: MutableList<Product> = ArrayList()
        return db.get()?.collection(COLLECTION_PATH)
            ?.orderBy(Product.ID_ILURIA, Query.Direction.DESCENDING)
            ?.get()
            ?.addOnSuccessListener { result ->
                var product: Product
                for (document in result) {
                    product = document.toObject(Product::class.java)
                    product.uid = document.id
                    //message = Message(document.data)
                    Log.d(TAG, "${document.id} => ${document.data}")
                    products.add(product)
                }
                listener.onDataChanged(products)
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadAllByIds(
        productIds: List<String>,
        listener: OnDataChangeListener<List<Product>>
    ): Task<QuerySnapshot>? {
        val products: MutableList<Product> = ArrayList()
        val itemsRef = db.get()?.collection(COLLECTION_PATH)
        return itemsRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var product: Product
                for (document in task.result) {
                    if (productIds.contains(document.id)) {
                        product = document.toObject(Product::class.java)
                        product.uid = document.id
                        products.add(product)
                    }
                }
                listener.onDataChanged(products)
            } else {
                Log.d(TAG, "Error deleting documents: ", task.exception)
            }
        }
    }

    fun findByTitle(
        title: String,
        listener: OnDataChangeListener<List<Product>>
    ): Task<QuerySnapshot>? {
        val products: MutableList<Product> = ArrayList()
        return db.get()?.collection(COLLECTION_PATH)
            ?.get()
            ?.addOnSuccessListener { result ->
                var product: Product
                for (document in result) {
                    if (document.get("title").toString().contains(title)) {
                        product = document.toObject(Product::class.java)
                        product.uid = document.id
                        Log.d(TAG, "${document.id} => ${document.data}")
                        products.add(product)
                    }
                }
                listener.onDataChanged(products)
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting messages: ", exception)
            }
    }

    fun insertAll(
        products: List<Product>,
        removeNotFoundInFirebase: Boolean = false,
        listener: OnProductInsertedListener? = null,
        finishListener: OnTaskFinishedListener<List<Product>>? = null
    ) {
        val hashProducts: MutableMap<String, Product> = HashMap<String, Product>(products.size)
        val listTasksAll = ArrayList<Task<*>>(products.size)
        var taskInsert: Task<*>?
        db.get()?.collection(COLLECTION_PATH)?.orderBy(Product.ID_ILURIA, Query.Direction.DESCENDING)
            ?.get()
            ?.addOnSuccessListener { result ->

                for (product in products) {
                    val data = product.toMap()
                    if (product.uid == null) {
                        var found = false
                        lateinit var productFS: Product
                        for (document in result) {
                            productFS = document.toObject(Product::class.java)
                            if (product.idIluria == productFS.idIluria) {
                                found = true
                                break
                            }
                        }
                        if (!found) {
                            taskInsert =
                                db.get()?.collection(COLLECTION_PATH)
                                    ?.add(data)
                                    ?.addOnSuccessListener {
                                        product.uid = it.id
                                        hashProducts[product.uid!!] = product
                                        if (!product.imageUrl?.startsWith("https://firebasestorage")!!)
                                            insertBlob(product)
                                        listener?.onProductInserted(product)
                                        Log.i(
                                            TAG,
                                            "Product ${product.idIluria} inserted successfully"
                                        )
                                    }?.addOnFailureListener {
                                        Log.i(TAG, "Error inserting product")
                                    }

                            listTasksAll.add(taskInsert!!)
                        } else {
                            product.uid = productFS.uid
                            hashProducts[product.uid!!] = product
                            taskInsert = insert(product, listener)
                            listTasksAll.add(taskInsert!!)
                        }
                    } else {
                        if (!product.imageUrl?.startsWith("https://firebasestorage")!!)
                            insertBlob(product)
                        taskInsert =
                            db.get()?.collection(COLLECTION_PATH)?.document(product.uid!!)
                                ?.set(data)
                                ?.addOnSuccessListener {
                                    Log.i(
                                        TAG,
                                        "[insert]Product ${product.uid} updated successfully"
                                    )
                                    hashProducts[product.uid!!] = product
                                    listener?.onProductInserted(product)
                                }?.addOnFailureListener {
                                    Log.i(TAG, "Error updating product")
                                }
                        listTasksAll.add(taskInsert!!)
                    }
                }

                Tasks.whenAll(*listTasksAll.toTypedArray()).addOnCompleteListener {
                    if (removeNotFoundInFirebase) {
                        db.get()?.collection(
                            COLLECTION_PATH
                        )?.get()
                            ?.addOnSuccessListener { result ->
                                var product: Product
                                for (document in result) {
                                    product = document.toObject(Product::class.java)
                                    if (!hashProducts.containsKey(document.id)) {
                                        delete(product)
                                        removeBlob(product)
                                    }
                                }
                            }
                            ?.addOnFailureListener { exception ->
                                Log.d(TAG, "Error getting documents: ", exception)
                            }
                    }
                    finishListener?.onTaskFinished(ArrayList(hashProducts.values))
                }
            }
    }

    private fun insert(product: Product, listener: OnProductInsertedListener? = null): Task<*> {
        val task: Task<*>
        val data = product.toMap()
        if (product.uid == null) {
            task =
                db.get()?.collection(COLLECTION_PATH)?.whereEqualTo(Product.ID_ILURIA, product.idIluria)
                    ?.get()?.addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        db.get()?.collection(COLLECTION_PATH)?.add(data)?.addOnSuccessListener {
                            product.uid = it.id
                            if (!product.imageUrl?.startsWith("https://firebasestorage")!!)
                                insertBlob(product)
                            listener?.onProductInserted(product)
                            Log.i(TAG, "Product ${product.idIluria} inserted successfully")
                        }?.addOnFailureListener {
                            Log.i(TAG, "Error inserting product")
                        }
                    } else {
                        product.uid = querySnapshot.documents[0].id
                        insert(product)
                    }
                }!!
        } else {
            if (!product.imageUrl?.startsWith("https://firebasestorage")!!)
                insertBlob(product)
            task = db.get()?.collection(COLLECTION_PATH)?.document(product.uid!!)?.set(data)
                ?.addOnSuccessListener {
                    Log.i(TAG, "[insert]Product ${product.uid} updated successfully")
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

    private fun insertBlob(product: Product) {
        ioScope.launch {
            val reference = storage.reference.child("$COLLECTION_PATH/${product.uid}.jpg")
            val client = OkHttpClient()
            client.setConnectTimeout(30, TimeUnit.SECONDS) // connect timeout
            client.setReadTimeout(30, TimeUnit.SECONDS)    // socket timeout
            val request = Request.Builder().url(product.imageUrl!!).build()
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
                    product.imageUrl = task.result.toString()
                    update(product, false)
                    Log.d(
                        TAG,
                        "[insertBlob]Image successfully saved for product ${product.uid} at ${product.imageUrl}"
                    )
                } else {
                    Log.d(TAG, "Unable to upload image ${product.uid}")
                }
                response.body().close()
            }
        }
    }

    private fun update(product: Product, checkForUrl: Boolean = true): Task<Void>? {
        val data = product.toMap()
        return db.get()?.collection(COLLECTION_PATH)?.document(product.uid.toString())?.set(data)
            ?.addOnSuccessListener {
                if (checkForUrl && (!product.imageUrl?.startsWith("https://firebasestorage")!!))
                    insertBlob(product)
                Log.d(TAG, "[update]Product ${product.uid} updated successfully")
            }?.addOnFailureListener {
        }
    }

    fun delete(product: Product): Task<Void>? {
        return db.get()?.collection(COLLECTION_PATH)?.document(product.uid.toString())?.delete()
            ?.addOnSuccessListener {
                val reference = storage.reference
                reference.child("$COLLECTION_PATH/${product.uid}.jpg").delete()
                Log.i(TAG, "Message ${product.uid} deleted successfully")
            }
    }

    fun deleteAll(): Task<QuerySnapshot>? {
        val itemsRef = db.get()?.collection(COLLECTION_PATH)
        return itemsRef?.get()?.addOnCompleteListener { task ->
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