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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.all_messages.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.AllMessagesFilterDialog.AllMessagesFilters

class AllMessagesViewModel(state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SAVE_STATE_KEY_FILTERS = "ALL_MESSAGES_SAVE_STATE_KEY_FILTERS"
    }

    private val savedStateHandle = state
    private var mFilters: AllMessagesFilters = AllMessagesFilters.default

    init {
        savedStateHandle.set(SAVE_STATE_KEY_FILTERS, mFilters)
    }

    val filtersLiveData: MutableLiveData<AllMessagesFilters>
        get(): MutableLiveData<AllMessagesFilters> {
            return savedStateHandle.getLiveData(SAVE_STATE_KEY_FILTERS)
        }
    val filters: AllMessagesFilters
        get(): AllMessagesFilters {
            return savedStateHandle.get(SAVE_STATE_KEY_FILTERS)!!
        }
}
