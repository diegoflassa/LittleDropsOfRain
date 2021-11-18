package app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeIluriaViewModel @Inject constructor() : ViewModel() {
    private var _carouselItems = MutableLiveData<List<Any>>(listOf())
    val carouselItems: LiveData<List<Any>> = _carouselItems
    fun getCarouselItems() {
    }
}