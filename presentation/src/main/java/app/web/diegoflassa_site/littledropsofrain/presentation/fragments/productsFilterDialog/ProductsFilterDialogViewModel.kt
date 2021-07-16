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

package app.web.diegoflassa_site.littledropsofrain.presentation.fragments.productsFilterDialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ProductsFilterDialogViewModel(state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SAVE_STATE_KEY_CATEGORIES = "PRODUCTS_FILTER_SAVE_STATE_KEY_CATEGORIES"
    }

    private val savedStateHandle = state

    private var mCategories: LinkedHashSet<String> = LinkedHashSet()

    init {
        savedStateHandle.set(SAVE_STATE_KEY_CATEGORIES, mCategories)
    }


    var categories: LinkedHashSet<String>
        get(): LinkedHashSet<String> {
            return savedStateHandle.get(SAVE_STATE_KEY_CATEGORIES)!!
        }
    set(value) {
        savedStateHandle.set(SAVE_STATE_KEY_CATEGORIES, value)
    }
}
