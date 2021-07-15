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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.instagram.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.presentation.MyApplication
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.instagram.InstagramFragment

class InstagramViewModel(state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SAVE_STATE_KEY_URL = "INSTAGRAM_SAVE_STATE_KEY_URL"
    }

    private val savedStateHandle = state
    private var mUrl = MyApplication.getContext().getString(R.string.url_instagram)

    init {
        savedStateHandle.set(SAVE_STATE_KEY_URL, mUrl)
    }

    val urlLiveData: MutableLiveData<String>
        get(): MutableLiveData<String> {
            return savedStateHandle.getLiveData(SAVE_STATE_KEY_URL)
        }
    val url: String
        get(): String {
            return savedStateHandle.get(SAVE_STATE_KEY_URL)!!
        }
}
