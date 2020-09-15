package app.web.diegoflassa_site.littledropsofrain.interfaces

import android.net.Uri

interface OnFileUploadedListener {
    fun onFileUploaded(local: Uri, remote : Uri)
}