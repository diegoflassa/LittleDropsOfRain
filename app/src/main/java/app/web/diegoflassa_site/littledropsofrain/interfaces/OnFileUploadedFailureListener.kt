package app.web.diegoflassa_site.littledropsofrain.interfaces

import android.net.Uri
import java.lang.Exception

interface OnFileUploadedFailureListener {
    fun onFileUploadedFailure(file: Uri, exception : Exception?)
}