package app.web.diegoflassa_site.littledropsofrain.interfaces

interface OnDataFailureListener<Exception> {
    fun onDataFailure(exception: Exception)
}