package io.github.diegoflassa.littledropsofrain.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.diegoflassa.littledropsofrain.data.entities.Product

//DFL - Classe de Acesso a dados. Aqui vc coloca as FORMAS DE ACESSAR os dados
@Dao
interface ProductDao {
    @get:Query("SELECT * FROM products")
    val all: LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE idProduct IN (:productIds)")
    fun loadAllByIds(productIds: IntArray?): List<Product>?

    @Query("SELECT * FROM products WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String?): Product?

    @Insert
    fun insertAll(vararg products: Product?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: Product)

    @Delete
    fun delete(product: Product?)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}