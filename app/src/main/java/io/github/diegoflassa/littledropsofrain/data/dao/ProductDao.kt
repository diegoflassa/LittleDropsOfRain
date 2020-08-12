@file:Suppress("BlockingMethodInNonBlockingContext")

package io.github.diegoflassa.littledropsofrain.data.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import io.github.diegoflassa.littledropsofrain.data.DataChangeListener
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


//DFL - Classe de Acesso a dados. Aqui vc coloca as FORMAS DE ACESSAR os dados
object ProductDao {

    private val TAG: String? = ProductDao::class.simpleName
    const val COLLECTION_PATH: String = "products"
    private val db : FirebaseFirestore = Firebase.firestore
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private var storage = Firebase.storage

    fun loadAll(listener: DataChangeListener<List<Product>>){
        val products: MutableList<Product> = ArrayList()
        db.collection(COLLECTION_PATH).orderBy("idIluria", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
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
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadAllByIds(productIds: List<String>, listener: DataChangeListener<List<Product>>) {
        val products: MutableList<Product> = ArrayList()
        val itemsRef = db.collection(COLLECTION_PATH)
        itemsRef.get().addOnCompleteListener { task ->
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

    fun findByTitle(title: String, listener: DataChangeListener<List<Product>>) {
        val products: MutableList<Product> = ArrayList()
        db.collection(COLLECTION_PATH)
            .get()
            .addOnSuccessListener { result ->
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
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting messages: ", exception)
            }
    }

    fun insertAll(products: List<Product>) {
        for( product in products) {
            insert(product)
        }
    }

    fun insert(product: Product) {
        val data = product.toMap()
        if(product.uid==null){
            db.collection(COLLECTION_PATH).whereEqualTo("idIluria", product.idIluria).get().addOnSuccessListener { querySnapshot ->
                if(querySnapshot.isEmpty) {
                    db.collection(COLLECTION_PATH).add(data).addOnSuccessListener {
                        product.uid= it.id
                        if(!product.imageUrl?.startsWith("https://firebasestorage")!!)
                            insertBlob(product)
                        Log.i(TAG, "Product ${product.idIluria} inserted successfully")
                    }.addOnFailureListener {
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
            db.collection(COLLECTION_PATH).document(product.uid!!).set(data).addOnSuccessListener{
                Log.i(TAG, "[insert]Product ${product.uid} updated successfully")
            }.addOnFailureListener{
                Log.i(TAG, "Error updating product")
            }
        }
    }

    fun insertBlob(product: Product) {
        ioScope.launch {
            val reference = storage.reference.child("$COLLECTION_PATH/${product.uid}.jpg")
            val client = OkHttpClient()
            val request = Request.Builder().url(product.imageUrl!!).build()
            val response = client.newCall(request).execute()
            reference.putStream(response.body().byteStream()).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    reference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        product.imageUrl = task.result.toString()
                        update(product, false)
                        Log.d(TAG, "[insertBlob]Image successfully saved for product ${product.uid} at ${product.imageUrl}")
                    } else {
                        // Handle failures
                    }
                    response.body().close()
                }
        }
    }

    fun update( product: Product, checkForUrl: Boolean = true ) {
        val data = product.toMap()
        db.collection(COLLECTION_PATH).document(product.uid.toString()).set(data).addOnSuccessListener{
            if(checkForUrl&&(!product.imageUrl?.startsWith("https://firebasestorage")!!))
                insertBlob(product)
            Log.d(TAG, "[update]Product ${product.uid} updated successfully")
        }.addOnFailureListener{
        }
    }

    fun delete(product: Product) {
        db.collection(COLLECTION_PATH).document(product.uid.toString()).delete().addOnSuccessListener {
            val reference = storage.reference
            reference.child("$COLLECTION_PATH/${product.uid}.jpg").delete()
            Log.i(TAG, "Message ${product.uid} deleted successfully")
        }
    }

    fun deleteAll() {
        val itemsRef = db.collection(COLLECTION_PATH)
        itemsRef.get().addOnCompleteListener { task ->
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