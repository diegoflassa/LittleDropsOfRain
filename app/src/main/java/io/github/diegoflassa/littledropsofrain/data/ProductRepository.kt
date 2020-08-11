package io.github.diegoflassa.littledropsofrain.data

import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.data.dao.ProductDao
import io.github.diegoflassa.littledropsofrain.data.entities.Product

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ProductRepository(private val productDao: ProductDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allProducts: LiveData<List<Product>> = productDao.all

    suspend fun insert(produtc: Product) {
        productDao.insert(produtc)
    }
}