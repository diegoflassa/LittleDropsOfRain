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