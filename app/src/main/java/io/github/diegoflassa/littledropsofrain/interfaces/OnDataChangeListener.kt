package io.github.diegoflassa.littledropsofrain.interfaces

interface OnDataChangeListener<T> {
    fun onDataLoaded(item: T)
}