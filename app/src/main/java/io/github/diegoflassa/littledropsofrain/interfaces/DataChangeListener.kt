package io.github.diegoflassa.littledropsofrain.interfaces

interface DataChangeListener<T> {
    fun onDataLoaded(item: T)
}