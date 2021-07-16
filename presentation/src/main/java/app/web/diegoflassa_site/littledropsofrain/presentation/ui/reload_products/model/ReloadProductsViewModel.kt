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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.reload_products.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ReloadProductsViewModel(state: SavedStateHandle) : ViewModel() {
    companion object {
        private const val SAVE_STATE_KEY_PROGRESS = "RELOAD_PRODUCTS_SAVE_STATE_KEY_PROGRESS"
        private const val SAVE_STATE_KEY_REMOVE_NOT_FOUND =
            "RELOAD_PRODUCTS_SAVE_STATE_KEY_REMOVE_NOT_FOUND"
        private const val SAVE_STATE_KEY_UNPUBLISH_NOT_FOUND =
            "RELOAD_PRODUCTS_SAVE_STATE_KEY_UNPUBLISH_NOT_FOUND"
    }

    private val savedStateHandle = state
    private var mProgress: StringBuilder = StringBuilder()
    private var mRemoveNotFoundProducts: Boolean = false
    private var mUnpublishNotFoundProducts: Boolean = true

    init {
        savedStateHandle.set(SAVE_STATE_KEY_PROGRESS, mProgress)
        savedStateHandle.set(SAVE_STATE_KEY_REMOVE_NOT_FOUND, mRemoveNotFoundProducts)
        savedStateHandle.set(SAVE_STATE_KEY_UNPUBLISH_NOT_FOUND, mUnpublishNotFoundProducts)
    }

    var progress: StringBuilder
        get(): StringBuilder {
            return savedStateHandle.get(SAVE_STATE_KEY_PROGRESS)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_PROGRESS, value)
        }

    var removeNotFoundProducts: Boolean
        get(): Boolean {
            return savedStateHandle.get(SAVE_STATE_KEY_REMOVE_NOT_FOUND)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_REMOVE_NOT_FOUND, value)
        }

    var unpublishNotFoundProducts: Boolean
        get(): Boolean {
            return savedStateHandle.get(SAVE_STATE_KEY_UNPUBLISH_NOT_FOUND)!!
        }
        set(value) {
            savedStateHandle.set(SAVE_STATE_KEY_UNPUBLISH_NOT_FOUND, value)
        }
}
