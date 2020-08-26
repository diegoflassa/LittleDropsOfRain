package io.github.diegoflassa.littledropsofrain.interfaces

interface DataFailureListener<Exception> {
    fun onDataFailure(exception: Exception)
}