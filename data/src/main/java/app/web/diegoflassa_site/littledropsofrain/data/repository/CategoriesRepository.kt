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
import app.web.diegoflassa_site.littledropsofrain.data.dao.CategoriesDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductsDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnItemInsertedListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnTaskFinishedListener
import java.util.HashMap

@SuppressWarnings("deprecation", "removal")
class CategoriesRepository(private val categoriesDao: CategoriesDao) {

    private val tag: String? = CategoriesRepository::class.simpleName

    fun loadAll(listener: OnDataChangeListener<List<CategoryItem>>) {
        Log.i(tag, "loadAll")
        categoriesDao.loadAll(listener)
    }

    fun insertAll(
        categories: List<CategoryItem>,
        listener: OnItemInsertedListener<CategoryItem>? = null,
        finishListener: OnTaskFinishedListener<List<CategoryItem>>? = null
    ) {
        Log.i(tag, "insertAll")
        categoriesDao.insertAll(
            categories,
            listener,
            finishListener
        )
    }

    fun update(category: CategoryItem, checkForUrl: Boolean = true) {
        Log.i(tag, "insertAll")
        categoriesDao.update(category, checkForUrl)
    }

    fun delete(category: CategoryItem) {
        Log.i(tag, "insertAll")
        categoriesDao.delete(category)
    }

    fun deleteAll() {
        Log.i(tag, "insertAll")
        categoriesDao.deleteAll()
    }
}
