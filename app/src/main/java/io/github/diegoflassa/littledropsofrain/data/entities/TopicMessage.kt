package io.github.diegoflassa.littledropsofrain.data.entities

import androidx.annotation.Keep

data class TopicMessage(
    var message : String,
    var topic : Topic,
    val messageContent : HashMap<String, String>){

    @Keep
    enum class Topic(private val topic: String){
        NEWS("News"),
        PROMOS("Promos"),
        UNKNOWN("Unknown");

        override fun toString(): String {
            return topic
        }
    }



}