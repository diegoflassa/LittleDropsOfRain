package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.data.entities.SubscriptionMessage

class TopicMessageViewState : LiveData<TopicMessageViewState>(){

    lateinit var text : String
    lateinit var title : String
    lateinit var body : String
    lateinit var topics : MutableSet<SubscriptionMessage.Topic>

}

