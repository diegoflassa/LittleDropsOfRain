package app.web.diegoflassa_site.littledropsofrain.models

import android.net.Uri
import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.data.entities.TopicMessage

class TopicMessageViewState : LiveData<TopicMessageViewState>() {

    var text: String = ""
    var title: String = ""
    var body: String = ""
    var topics = HashSet<TopicMessage.Topic>()
    var imageUriFirestore: Uri? = null
    var imageUriLocal: Uri? = null

}

