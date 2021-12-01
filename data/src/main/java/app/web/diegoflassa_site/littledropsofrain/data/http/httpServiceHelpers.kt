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

package app.web.diegoflassa_site.littledropsofrain.data.http

import android.annotation.SuppressLint
import app.web.diegoflassa_site.littledropsofrain.data.BuildConfig
import app.web.diegoflassa_site.littledropsofrain.data.Config
import app.web.diegoflassa_site.littledropsofrain.data.http.interceptors.TimeoutInterceptor
import app.web.diegoflassa_site.littledropsofrain.data.parser.JsonOrXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.lang.reflect.Type
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal fun getHttpClientBuilder(): OkHttpClient.Builder {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    val httpClient = OkHttpClient.Builder()
        // timeouts
        .readTimeout(2, TimeUnit.MINUTES)
        .connectTimeout(2, TimeUnit.MINUTES)
        .addInterceptor(TimeoutInterceptor())
        .addInterceptor(loggingInterceptor)
    httpClient.apply {
        if (BuildConfig.DEBUG) // if it is a debug build ignore ssl errors
            ignoreAllSSLErrors()
    }
    return httpClient
}

internal fun buildHttpClient(): OkHttpClient {
    return getHttpClientBuilder().build()
}

internal fun buildRetrofitClientApi(client: OkHttpClient): Retrofit {
    val baseUrl = if (BuildConfig.DEBUG) {
        Config.BASE_URL_API_DEVELOP
    } else {
        Config.BASE_URL_API_RELEASE
    }
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(nullOnEmptyConverterFactory)
        .addConverterFactory(JsonOrXmlConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
}

val nullOnEmptyConverterFactory = object : Converter.Factory() {
    fun converterFactory() = this
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ) = object :
        Converter<ResponseBody, Any?> {
        val nextResponseBodyConverter =
            retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

        override fun convert(value: ResponseBody) =
            if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
    }
}

fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {

    val naiveTrustManager = @SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
    }

    val insecureSocketFactory = SSLContext.getInstance("TLSv1").apply {
        val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
        init(null, trustAllCerts, SecureRandom())
    }.socketFactory

    sslSocketFactory(insecureSocketFactory, naiveTrustManager)
    hostnameVerifier { _, _ -> true }

    return this
}
