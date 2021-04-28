package app.web.diegoflassa_site.littledropsofrain.parser

import androidx.annotation.Keep

@Keep
enum class ResponseType(private val type: String) {
    JSON("json"),
    XML("xml"),
    UNKNOWN("unknown");

    override fun toString(): String {
        return type
    }
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResponseFormat(val type: ResponseType = ResponseType.UNKNOWN)