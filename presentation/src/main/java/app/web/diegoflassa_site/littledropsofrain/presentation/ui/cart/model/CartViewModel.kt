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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.cart.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.data.entities.CartItem
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.AllProductsFilterDialog.AllProductsFilters

class CartViewModel(state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SAVE_STATE_KEY_FILTER = "SAVE_STATE_KEY_FILTERS"
        private const val SAVE_STATE_KEY_CART = "SAVE_STATE_KEY_CART"
    }

    private val savedStateHandle = state

    private var mFilters: AllProductsFilters = AllProductsFilters.default
    private var mCart: MutableList<CartItem> = ArrayList()

    init {
        savedStateHandle.set(SAVE_STATE_KEY_FILTER, mFilters)
        savedStateHandle.set(SAVE_STATE_KEY_CART, mCart)
    }

    val filtersLiveData: MutableLiveData<AllProductsFilters>
        get(): MutableLiveData<AllProductsFilters> {
            return savedStateHandle.getLiveData(SAVE_STATE_KEY_FILTER)
        }
    var filters: AllProductsFilters = AllProductsFilters.default
        get(): AllProductsFilters {
            return savedStateHandle.get(SAVE_STATE_KEY_FILTER)!!
        }

    val cartLiveData: MutableLiveData<MutableList<CartItem>>
        get(): MutableLiveData<MutableList<CartItem>> {
            return savedStateHandle.getLiveData(SAVE_STATE_KEY_CART)
        }
    val cart: MutableList<CartItem>
        get(): MutableList<CartItem> {
            return savedStateHandle.get(SAVE_STATE_KEY_CART)!!
        }
}
