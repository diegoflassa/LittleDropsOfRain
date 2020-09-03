@file:Suppress("BlockingMethodInNonBlockingContext")

package app.web.diegoflassa_site.littledropsofrain.data.dao

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnProductInsertedListener
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit


//DFL - Classe de Acesso a dados. Aqui vc coloca as FORMAS DE ACESSAR os dados
object ProductDao {

    private val TAG: String? = ProductDao::class.simpleName
    const val COLLECTION_PATH: String = "products"
    private var db : WeakReference<FirebaseFirestore> = WeakReference(FirebaseFirestore.getInstance())
    private var storage = Firebase.storage

    fun loadAll(listener: OnDataChangeListener<List<Product>>){
        val products: MutableList<Product> = ArrayList()
        db.get()?.collection(COLLECTION_PATH)?.orderBy("idIluria", Query.Direction.DESCENDING)
            ?.get()
            ?.addOnSuccessListener { result ->
                var product : Product
                for (document in result) {
                    product = document.toObject(Product::class.java)
                    product.uid = document.id
                    //message = Message(document.data)
                    Log.d(TAG, "${document.id} => ${document.data}")
                    products.add(product)
                }
                listener.onDataLoaded(products)
            }
           ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadAllByIds(productIds: List<String>, listener: OnDataChangeListener<List<Product>>) {
        val products: MutableList<Product> = ArrayList()
        val itemsRef =db.get()?.collection(COLLECTION_PATH)
        itemsRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var product: Product
                for (document in task.result) {
                    if (productIds.contains(document.id)) {
                        product = document.toObject(Product::class.java)
                        product.uid = document.id
                        products.add(product)
                    }
                }
                listener.onDataLoaded(products)
            } else {
                Log.d(TAG, "Error deleting documents: ", task.exception)
            }
        }
    }

    fun findByTitle(title: String, listener: OnDataChangeListener<List<Product>>) {
        val products: MutableList<Product> = ArrayList()
       db.get()?.collection(COLLECTION_PATH)
            ?.get()
            ?.addOnSuccessListener { result ->
                var product : Product
                for (document in result) {
                    if(document.get("title").toString().contains(title)) {
                        product = document.toObject(Product::class.java)
                        product.uid = document.id
                        Log.d(TAG, "${document.id} => ${document.data}")
                        products.add(product)
                    }
                }
                listener.onDataLoaded(products)
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting messages: ", exception)
            }
    }

    fun insertAll(products: List<Product>, removeNotFoundInFirebase : Boolean= false, listener : OnProductInsertedListener? = null) {
        val hashProducts : MutableMap<String, Product> = HashMap<String, Product>(products.size)
        val hashTasksDocRef = HashMap<Product, Task<DocumentReference>> (products.size)
        val hashTasksAdd = HashMap<Product, Task<QuerySnapshot>> (products.size)
        val hashTasksSet = HashMap<Product, Task<Void>> (products.size)
        var taskInsertDocRef : Task<DocumentReference>?
        var taskInsertAdd : Task<QuerySnapshot>?
        var taskInsertSet : Task<Void>?
        for( product in products) {
            val data = product.toMap()
            if(product.uid==null){
                taskInsertAdd = db.get()?.collection(COLLECTION_PATH)?.whereEqualTo("idIluria", product.idIluria)?.get()?.addOnSuccessListener { querySnapshot ->
                    if(querySnapshot.isEmpty) {
                        taskInsertDocRef=  db.get()?.collection(COLLECTION_PATH)?.add(data)?.addOnSuccessListener {
                            product.uid= it.id
                            if(!product.imageUrl?.startsWith("https://firebasestorage")!!)
                                insertBlob(product)
                            listener?.onProductInserted(product)
                            Log.i(TAG, "Product ${product.idIluria} inserted successfully")
                        }?.addOnFailureListener {
                            Log.i(TAG, "Error inserting product")
                        }

                        hashTasksDocRef[product] = taskInsertDocRef!!
                    }else{
                        product.uid= querySnapshot.documents[0].id
                        insert(product)
                    }
                    hashProducts[product.uid!!] = product
                }
                hashTasksAdd[product] = taskInsertAdd!!
            }else{
                if(!product.imageUrl?.startsWith("https://firebasestorage")!!)
                    insertBlob(product)
                taskInsertSet = db.get()?.collection(COLLECTION_PATH)?.document(product.uid!!)?.set(data)?.addOnSuccessListener{
                    Log.i(TAG, "[insert]Product ${product.uid} updated successfully")
                    hashProducts[product.uid!!] = product
                    listener?.onProductInserted(product)
                }?.addOnFailureListener{
                    Log.i(TAG, "Error updating product")
                }
                hashTasksSet[product] = taskInsertSet!!
            }
        }

        val docRefTasksToWait: Array<Task<DocumentReference>> = hashTasksDocRef.values.toTypedArray()
        val addTasksToWait: Array<Task<QuerySnapshot>> = hashTasksAdd.values.toTypedArray()
        val getTasksToWait: Array<Task<Void>> = hashTasksSet.values.toTypedArray()

        Tasks.whenAll(*docRefTasksToWait)
        Tasks.whenAll(*addTasksToWait)
        Tasks.whenAll(*getTasksToWait)

        if(removeNotFoundInFirebase) {
            db.get()?.collection(COLLECTION_PATH)
                ?.get()
                ?.addOnSuccessListener { result ->
                    var product : Product
                    for (document in result) {
                        product = document.toObject(Product::class.java)
                        if(!hashProducts.containsKey(document.id)) {
                            delete(product)
                            removeBlob(product)
                        }
                    }
                }
                ?.addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun insert(product: Product, listener : OnProductInsertedListener? = null) {
        val data = product.toMap()
        if(product.uid==null){
           db.get()?.collection(COLLECTION_PATH)?.whereEqualTo("idIluria", product.idIluria)?.get()?.addOnSuccessListener { querySnapshot ->
                if(querySnapshot.isEmpty) {
                   db.get()?.collection(COLLECTION_PATH)?.add(data)?.addOnSuccessListener {
                        product.uid= it.id
                        if(!product.imageUrl?.startsWith("https://firebasestorage")!!)
                            insertBlob(product)
                       listener?.onProductInserted(product)
                        Log.i(TAG, "Product ${product.idIluria} inserted successfully")
                    }?.addOnFailureListener {
                        Log.i(TAG, "Error inserting product")
                    }
                }else{
                    product.uid= querySnapshot.documents[0].id
                    insert(product)
                }
            }
        }else{
            if(!product.imageUrl?.startsWith("https://firebasestorage")!!)
                insertBlob(product)
           db.get()?.collection(COLLECTION_PATH)?.document(product.uid!!)?.set(data)?.addOnSuccessListener{
                Log.i(TAG, "[insert]Product ${product.uid} updated successfully")
            }?.addOnFailureListener{
                Log.i(TAG, "Error updating product")
            }
        }
    }

    private fun removeBlob(product: Product) {
        var reference = storage.reference.child("$COLLECTION_PATH/${product.uid}.jpg")
        reference.delete().addOnSuccessListener {
            Log.d(TAG, "[insertBlob]Image successfully removed for product ${product.uid} at ${product.imageUrl}")
        }.addOnFailureListener {
            Log.d(TAG,"Error deleting image ${product.uid}")
        }.continueWith {
            reference = storage.reference.child("$COLLECTION_PATH/${product.uid}_200x200.jpg")
            reference.delete().addOnSuccessListener {
                Log.d(TAG,"[insertBlob]Image successfully removed for product ${product.uid}_200x200 at ${product.imageUrl}")
            }.addOnFailureListener {
                Log.d(TAG, "Error deleting image ${product.uid}")
            }
        }
    }

    private fun insertBlob(product: Product) {
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
                    Log.d(TAG, "[insertBlob]Image successfully saved for product ${product.uid} at ${product.imageUrl}")
                } else {
                    Log.d(TAG,"Unable to upload image ${product.uid}")
                }
                response.body().close()
            }
    }

    private fun update(product: Product, checkForUrl: Boolean = true ) {
        val data = product.toMap()
       db.get()?.collection(COLLECTION_PATH)?.document(product.uid.toString())?.set(data)?.addOnSuccessListener{
            if(checkForUrl&&(!product.imageUrl?.startsWith("https://firebasestorage")!!))
                insertBlob(product)
            Log.d(TAG, "[update]Product ${product.uid} updated successfully")
        }?.addOnFailureListener{
        }
    }

    fun delete(product: Product) {
       db.get()?.collection(COLLECTION_PATH)?.document(product.uid.toString())?.delete()?.addOnSuccessListener {
            val reference = storage.reference
            reference.child("$COLLECTION_PATH/${product.uid}.jpg").delete()
            Log.i(TAG, "Message ${product.uid} deleted successfully")
        }
    }

    fun deleteAll() {
        val itemsRef = db.get()?.collection(COLLECTION_PATH)
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