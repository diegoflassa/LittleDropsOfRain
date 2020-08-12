package io.github.diegoflassa.littledropsofrain.data

import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.data.dao.IluriaProductDao
import io.github.diegoflassa.littledropsofrain.data.entities.IluriaProduct

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class IluriaProductRepository(private val iluriaProductDao: IluriaProductDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allProducts: LiveData<List<IluriaProduct>> = iluriaProductDao.all

    suspend fun insert(produtc: IluriaProduct) {
        iluriaProductDao.insert(produtc)
    }
}