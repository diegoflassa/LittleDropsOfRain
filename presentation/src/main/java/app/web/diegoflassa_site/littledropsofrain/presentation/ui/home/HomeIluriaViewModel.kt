package app.web.diegoflassa_site.littledropsofrain.presentation.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeIluriaViewModel @Inject constructor() : ViewModel() {
    fun  getCarouselItems() : List<Any>{
        return listOf()
    }
}