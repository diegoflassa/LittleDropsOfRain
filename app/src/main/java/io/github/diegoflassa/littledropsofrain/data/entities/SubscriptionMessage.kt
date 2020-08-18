package io.github.diegoflassa.littledropsofrain.data.entities

data class SubscriptionMessage(
    var message : String,
    var topic : Topic,
    val messageContent : HashMap<String, String>){

    enum class Topic(private val topic: String){
        NEWS("News"),
        PROMOS("Promos"),
        UNKNOWN("Unknown");

        override fun toString(): String {
            return topic
        }
    }



}