package io.github.diegoflassa.littledropsofrain.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.diegoflassa.littledropsofrain.data.entities.User

//DFL - Classe de Acesso a dados. Aqui vc coloca as FORMAS DE ACESSAR os dados
@Dao
interface UserDao {
    @get:Query("SELECT * FROM user")
    val all: List<User?>?

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray?): List<User?>?

    @Query(
        "SELECT * FROM user WHERE first_name LIKE :first AND "
                + "last_name LIKE :last LIMIT 1"
    )
    fun findByName(
        first: String?,
        last: String?
    ): User?

    @Insert
    fun insertAll(vararg users: User?)

    @Delete
    fun delete(user: User?)
}