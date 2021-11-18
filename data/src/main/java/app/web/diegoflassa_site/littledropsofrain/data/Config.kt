package app.web.diegoflassa_site.littledropsofrain.data

import splitties.resources.appStr

object Config {
    val BASE_URL_API_DEVELOP by lazy { appStr(R.string.API_URL_API_DEBUG) }
    val BASE_URL_API_RELEASE by lazy { appStr(R.string.API_URL_API_RELEASE) }
}
