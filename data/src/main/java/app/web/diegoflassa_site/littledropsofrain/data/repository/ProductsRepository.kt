/*
 * Copyright 2021 The Little Drops of Rain Project
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
package app.web.diegoflassa_site.littledropsofrain.data.repository

import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductsDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnProductInsertedListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnTaskFinishedListener
import java.util.HashMap

@SuppressWarnings("deprecation", "removal")
class ProductsRepository(private val productsDao: ProductsDao) {

    private val tag: String? = ProductsRepository::class.simpleName

    fun loadMostLiked(
        user: User,
        listener: OnDataChangeListener<HashMap<Product, Int>>
    ) {
        Log.i(tag, "loadMostLiked")
        productsDao.loadMostLiked(user, listener)
    }

    fun loadMyLiked(
        user: User,
        listener: OnDataChangeListener<List<Product>>
    ) {
        Log.i(tag, "loadMyLiked")
        productsDao.loadMyLiked(user, listener)
    }

    fun loadAllPublished(listener: OnDataChangeListener<List<Product>>) {
        Log.i(tag, "loadAllPublished")
        productsDao.loadAllPublished(listener)
    }

    fun loadAll(listener: OnDataChangeListener<List<Product>>) {
        Log.i(tag, "loadAll")
        productsDao.loadAll(listener)
    }

    fun loadAllByIds(
        productIds: List<String>,
        listener: OnDataChangeListener<List<Product>>
    ) {
        Log.i(tag, "loadAllByIds")
        productsDao.loadAllByIds(productIds, listener)
    }

    fun findByTitle(
        title: String,
        listener: OnDataChangeListener<List<Product>>
    ) {
        Log.i(tag, "findByTitle")
        productsDao.findByTitle(title, listener)
    }

    fun insertAll(
        products: List<Product>,
        removeNotFoundInFirebase: Boolean = false,
        unpublishNotFoundInFirebase: Boolean = true,
        listener: OnProductInsertedListener? = null,
        finishListener: OnTaskFinishedListener<List<Product>>? = null
    ) {
        Log.i(tag, "insertAll")
        productsDao.insertAll(
            products,
            removeNotFoundInFirebase,
            unpublishNotFoundInFirebase,
            listener,
            finishListener
        )
    }
}
