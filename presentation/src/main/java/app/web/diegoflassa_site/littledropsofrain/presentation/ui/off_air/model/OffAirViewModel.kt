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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.off_air.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.off_air.OffAirFragment

class OffAirViewModel(state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SAVE_STATE_KEY_MSG_EN = "OFF_AIR_SAVE_STATE_KEY_MSG_EN"
        private const val SAVE_STATE_KEY_MSG_PT = "OFF_AIR_SAVE_STATE_KEY_MSG_PT"
    }

    private val savedStateHandle = state
    private var mMessageEn: String = ""
    private var mMessagePt: String = ""

    init {
        savedStateHandle.set(SAVE_STATE_KEY_MSG_EN, mMessageEn)
        savedStateHandle.set(SAVE_STATE_KEY_MSG_PT, mMessagePt)
    }

    val msgEnLiveData: MutableLiveData<String>
        get(): MutableLiveData<String> {
            return savedStateHandle.getLiveData(SAVE_STATE_KEY_MSG_EN)
        }
    val msgEn: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_MSG_EN)!!
        }

    val msgPtLiveData: MutableLiveData<String>
        get(): MutableLiveData<String> {
            return savedStateHandle.getLiveData(SAVE_STATE_KEY_MSG_PT)
        }
    val msgPt: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_MSG_PT)!!
        }
}
