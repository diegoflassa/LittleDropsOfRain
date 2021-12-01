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

package app.web.diegoflassa_site.littledropsofrain.data.http.interceptors

import app.web.diegoflassa_site.littledropsofrain.data.helpers.SpecificTimeout
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException

internal class TimeoutInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val tag = request.tag(Invocation::class.java)

        val timeout: SpecificTimeout =
            tag?.method()?.getAnnotation(SpecificTimeout::class.java) ?: return chain.proceed(
                request
            )

        return chain.withReadTimeout(timeout.duration, timeout.unit)
            .withConnectTimeout(timeout.duration, timeout.unit)
            .withWriteTimeout(timeout.duration, timeout.unit)
            .proceed(request)
    }
}
