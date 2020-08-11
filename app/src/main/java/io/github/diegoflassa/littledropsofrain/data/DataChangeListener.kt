package io.github.diegoflassa.littledropsofrain.data

interface DataChangeListener<T> {
    fun onDataLoaded(item: T)
}