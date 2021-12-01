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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.helpers.DummyData

data class HomeIluriaState(
    var carouselItemsSpotlight: List<Product> = listOf(),
    var carouselItemsCategories: List<CategoryItem> = listOf(),
    var carouselItemsNewCollection: List<Product> = listOf(),
    var carouselItemsRecommended: List<Product> = listOf(),
    var isRefreshingSpotlight: MutableLiveData<Boolean> = MutableLiveData(false),
    var isRefreshingCategories: MutableLiveData<Boolean> = MutableLiveData(false),
    var isRefreshingNewCollection: MutableLiveData<Boolean> = MutableLiveData(false),
    var isRefreshingRecommended: MutableLiveData<Boolean> = MutableLiveData(false),
    var isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false),
) {
    companion object {
        fun getDummyData(): LiveData<HomeIluriaState> {
            val ret = MutableLiveData(HomeIluriaState())
            ret.value!!.carouselItemsSpotlight = DummyData.getSpotlightCarouselItems()
            ret.value!!.carouselItemsCategories = DummyData.getCategoriesCarouselItems()
            ret.value!!.carouselItemsNewCollection = DummyData.getNewCollectionCarouselItems()
            ret.value!!.carouselItemsRecommended = DummyData.getRecommendationsCarouselItems()
            return ret
        }
    }

    fun copy(state: HomeIluriaState) {
        carouselItemsSpotlight = state.carouselItemsSpotlight
        carouselItemsCategories = state.carouselItemsCategories
        carouselItemsNewCollection = state.carouselItemsNewCollection
        carouselItemsRecommended = state.carouselItemsRecommended
    }
}
