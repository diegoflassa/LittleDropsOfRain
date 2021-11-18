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

package app.web.diegoflassa_site.littledropsofrain.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.domain.auth.FirebaseAuthLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Suppress("UNUSED")
@HiltViewModel
class MainActivityViewModel @Inject constructor(state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SAVE_STATE_KEY = "SAVE_STATE_KEY"
    }

    private val savedStateHandle = state
    var homeType = HomeType.UNKNOWN

    init {
        homeType = HomeType.HOME_ILURIA
    }

    var firebaseAuthLiveData: FirebaseAuthLiveData = FirebaseAuthLiveData()

}
