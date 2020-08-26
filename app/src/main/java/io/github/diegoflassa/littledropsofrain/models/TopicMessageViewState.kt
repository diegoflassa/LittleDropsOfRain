package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.data.entities.TopicMessage

class TopicMessageViewState : LiveData<TopicMessageViewState>(){

    var text : String = ""
    var title : String = ""
    var body : String = ""
    var topics = HashSet<TopicMessage.Topic>()

}

