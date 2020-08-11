package io.github.diegoflassa.littledropsofrain.data.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="Products")
data class Product(
    @NonNull @PrimaryKey
    var idProduct : String,
    @field:ColumnInfo(name = "linkProduct")
    var linkProduct : String? = null,
    @field:ColumnInfo(name = "title")
    var title : String? = null,
    @field:ColumnInfo(name = "price")
    var price : String? = null,
    @field:ColumnInfo(name = "installment")
    var installment : String? = null,
    @field:ColumnInfo(name = "disponibility")
    var disponibility : String? = null,
    @field:ColumnInfo(name = "image")
    var image : String? = null,
    @field:ColumnInfo(name = "category")
    var category : String? = null
)