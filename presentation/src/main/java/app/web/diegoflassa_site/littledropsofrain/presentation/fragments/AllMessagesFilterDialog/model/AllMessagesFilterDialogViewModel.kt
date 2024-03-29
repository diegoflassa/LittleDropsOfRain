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

package app.web.diegoflassa_site.littledropsofrain.presentation.fragments.AllMessagesFilterDialog.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.AllMessagesFilterDialog.AllMessagesFilterDialogFragment
import javax.inject.Inject

// @HiltViewModel
class AllMessagesFilterDialogViewModel /*@Inject*/ constructor(state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SAVE_STATE_KEY = "SAVE_STATE_KEY"
    }

    private val savedStateHandle = state

    init {
        val viewState = AllMessagesFilterDialogViewState().apply {
            text = "This is ${AllMessagesFilterDialogFragment::class.simpleName} Fragment"
        }
        savedStateHandle.set(SAVE_STATE_KEY, viewState)
    }

    val viewState: AllMessagesFilterDialogViewState
        get(): AllMessagesFilterDialogViewState {
            return savedStateHandle.get(SAVE_STATE_KEY)!!
        }
}
