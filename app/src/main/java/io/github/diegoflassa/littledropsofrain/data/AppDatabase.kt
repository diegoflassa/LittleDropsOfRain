package io.github.diegoflassa.littledropsofrain.data

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.User

//DFL - Represents your APPs database.
@Database(
    entities = [User::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao?
}