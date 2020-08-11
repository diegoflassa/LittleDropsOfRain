package io.github.diegoflassa.littledropsofrain.ui.iluria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IluriaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is iluria Fragment"
    }
    val text: LiveData<String> = _text
}