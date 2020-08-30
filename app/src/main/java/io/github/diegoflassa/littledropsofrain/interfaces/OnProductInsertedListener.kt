package io.github.diegoflassa.littledropsofrain.interfaces

import io.github.diegoflassa.littledropsofrain.data.entities.Product

interface OnProductInsertedListener {
    fun onProductInserted(product: Product)
}