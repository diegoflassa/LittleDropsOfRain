package app.web.diegoflassa_site.littledropsofrain.interfaces

import app.web.diegoflassa_site.littledropsofrain.data.entities.Product

interface OnProductInsertedListener {
    fun onProductInserted(product: Product)
}