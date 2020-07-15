package io.github.diegoflassa.littledropsofrain.ui.illuria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IlluriaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is illuria Fragment"
    }
    val text: LiveData<String> = _text
}