/*
 * Copyright 2020 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.data.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Products")
data class IluriaProduct(
    @NonNull @PrimaryKey
    var idProduct: String = "0",
    @field:ColumnInfo(name = "linkProduct")
    var linkProduct: String? = null,
    @field:ColumnInfo(name = "title")
    var title: String? = null,
    @field:ColumnInfo(name = "price")
    var price: String? = null,
    @field:ColumnInfo(name = "installment")
    var installment: String? = null,
    @field:ColumnInfo(name = "disponibility")
    var disponibility: String? = null,
    @field:ColumnInfo(name = "image")
    var image: String? = null,
    @field:ColumnInfo(name = "category")
    var category: String? = null
)
