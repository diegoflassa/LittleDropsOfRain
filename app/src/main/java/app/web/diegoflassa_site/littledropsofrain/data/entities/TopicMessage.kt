package app.web.diegoflassa_site.littledropsofrain.data.entities

import androidx.annotation.Keep
import app.web.diegoflassa_site.littledropsofrain.MyApplication
import app.web.diegoflassa_site.littledropsofrain.R

data class TopicMessage(
    var title: String,
    var message: String,
    var topic: Topic
) {

    @Keep
    @Suppress("UNUSED")
    enum class Topic(private val topic: String, private val stringId: Int) {
        NEWS_PT("News_PT", R.string.news_pt),
        NEWS_EN("News_EN", R.string.news_en),
        PROMOS_PT("Promos_PT", R.string.promos_pt),
        PROMOS_EN("Promos_EN", R.string.promos_en),
        UNKNOWN("Unknown", R.string.unknown);

        override fun toString(): String {
            return topic
        }

        private fun getStringId(): Int {
            return stringId
        }

        fun toTitle(): String {
            return MyApplication.getContext().getString(stringId)
        }

        companion object {
            fun fromTitle(title: String): Topic {
                return when (title) {
                    MyApplication.getContext().getString(NEWS_PT.getStringId()) -> NEWS_PT
                    MyApplication.getContext().getString(NEWS_EN.getStringId()) -> NEWS_EN
                    MyApplication.getContext().getString(PROMOS_PT.getStringId()) -> PROMOS_PT
                    MyApplication.getContext().getString(PROMOS_EN.getStringId()) -> PROMOS_EN
                    else -> UNKNOWN
                }
            }
        }
    }
}
