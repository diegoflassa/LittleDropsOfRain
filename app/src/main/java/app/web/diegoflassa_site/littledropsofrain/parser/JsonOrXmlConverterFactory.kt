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

package app.web.diegoflassa_site.littledropsofrain.parser

import androidx.annotation.Nullable
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.lang.reflect.Type

class JsonOrXmlConverterFactory : Converter.Factory() {
    private val xmlFactory: Converter.Factory = JacksonConverterFactory.create(XmlMapper())
    private val jsonFactory: Converter.Factory = GsonConverterFactory.create()

    @Nullable
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation?>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            if (annotation is ResponseFormat) {
                val value = annotation.type
                if (ResponseType.JSON == value) {
                    return jsonFactory.responseBodyConverter(type, annotations, retrofit)
                } else if (ResponseType.XML == value) {
                    return xmlFactory.responseBodyConverter(type, annotations, retrofit)
                }
            }
        }
        return null
    }

    companion object {
        fun create(): JsonOrXmlConverterFactory {
            return JsonOrXmlConverterFactory()
        }
    }
}
