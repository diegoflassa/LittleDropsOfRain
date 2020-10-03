package app.web.diegoflassa_site.littledropsofrain.interfaces

import android.net.Uri

interface OnFileUploadedFailureListener {
    fun onFileUploadedFailure(file: Uri, exception: Exception?)
}