/*
 * Copyright 2021 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.data.entities

import android.content.Context
import androidx.annotation.Keep
import app.web.diegoflassa_site.littledropsofrain.data.R

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
        ADMINS("Admins", R.string.admins),
        UNKNOWN("Unknown", R.string.unknown);

        override fun toString(): String {
            return topic
        }

        private fun getStringId(): Int {
            return stringId
        }

        fun toTitle(context: Context): String {
            return context.getString(stringId)
        }

        companion object {
            fun fromTitle(context: Context, title: String): Topic {
                return when (title) {
                    context.getString(NEWS_PT.getStringId()) -> NEWS_PT
                    context.getString(NEWS_EN.getStringId()) -> NEWS_EN
                    context.getString(PROMOS_PT.getStringId()) -> PROMOS_PT
                    context.getString(PROMOS_EN.getStringId()) -> PROMOS_EN
                    context.getString(ADMINS.getStringId()) -> ADMINS
                    else -> UNKNOWN
                }
            }
        }
    }
}
