package io.github.diegoflassa.littledropsofrain.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.diegoflassa.littledropsofrain.data.entities.IluriaProduct

//DFL - Classe de Acesso a dados. Aqui vc coloca as FORMAS DE ACESSAR os dados
@Dao
interface IluriaProductDao {
    @get:Query("SELECT * FROM products")
    val all: LiveData<List<IluriaProduct>>

    @Query("SELECT * FROM products WHERE idProduct IN (:productIds)")
    fun loadAllByIds(productIds: IntArray?): List<IluriaProduct>?

    @Query("SELECT * FROM products WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String?): IluriaProduct?

    @Insert
    fun insertAll(vararg iluriaProducts: IluriaProduct?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(iluriaProduct: IluriaProduct)

    @Delete
    fun delete(iluriaProduct: IluriaProduct?)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}