package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.data.entities.TopicMessage

class TopicMessageViewState : LiveData<TopicMessageViewState>(){

    var text : String = ""
    var title : String = ""
    var body : String = ""
    var topics = HashSet<TopicMessage.Topic>()

}

