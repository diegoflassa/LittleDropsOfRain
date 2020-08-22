package io.github.diegoflassa.littledropsofrain.data

interface DataFailureListener<Exception> {
    fun onDataFailure(exception: Exception)
}