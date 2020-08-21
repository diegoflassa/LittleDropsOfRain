package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.fragments.Filters
import io.github.diegoflassa.littledropsofrain.ui.home.HomeFragment

class HomeViewModel : ViewModel() {

    var filters: Filters = Filters.default

    private val mViewState = MutableLiveData<HomeViewState>().apply {
        value?.text = "This is ${HomeFragment::class.simpleName} Fragment"
    }
    val viewState: LiveData<HomeViewState> = mViewState
}