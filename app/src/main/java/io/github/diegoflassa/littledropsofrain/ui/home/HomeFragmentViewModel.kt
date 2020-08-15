package io.github.diegoflassa.littledropsofrain.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.Filters

class HomeFragmentViewModel : ViewModel() {

    var filters: Filters = Filters.default

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}