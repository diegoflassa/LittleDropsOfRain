package io.github.diegoflassa.littledropsofrain.interfaces

interface OnDataFailureListener<Exception> {
    fun onDataFailure(exception: Exception)
}