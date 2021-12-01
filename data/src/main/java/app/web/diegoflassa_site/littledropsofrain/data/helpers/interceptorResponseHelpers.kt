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

package app.web.diegoflassa_site.littledropsofrain.data.helpers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

val emptyRequestBody: ResponseBody =
    "".toResponseBody("application/json".toMediaTypeOrNull())

internal fun errorResponse(ex: Throwable, original: Request): Response {
    val message = ex.message ?: "Request error"
    val statusCode = 500
    return createResponse(
        statusCode,
        "[error-response] $message",
        original
    )
}

internal fun expiredTokenResponse(original: Request): Response {
    val statusCode = 401
    val message = "Por favor, entre novamente."
    return createResponse(
        statusCode,
        message,
        original
    )
}

internal fun tokenNotFoundResponse(original: Request): Response {
    val message = "Necessário login para acessar a função"
    val statusCode = 401
    return createResponse(
        statusCode,
        message,
        original
    )
}

internal fun unableToRefreshTokenResponse(original: Request): Response {
    val message = "Não foi possivel renovar o token de acesso, favor fazer login novamente"
    val statusCode = 401
    return createResponse(
        statusCode,
        message,
        original
    )
}

internal fun errorToRefreshTokenResponse(original: Request): Response {
    val message = "Não foi possivel renovar o token de acesso, favor tente novamente"
    val statusCode = 500
    return createResponse(
        statusCode,
        message,
        original
    )
}

internal fun noTokenResponse(original: Request): Response {
    val statusCode = 401
    val message = "Usuário precisa realizar autenticação"
    return createResponse(
        statusCode,
        message,
        original
    )
}

internal fun noConnectivityResponse(original: Request): Response {
    val message = "Você está sem conexão com internet."
    val statusCode = 503
    return createResponse(
        statusCode,
        message,
        original
    )
}

internal fun createResponse(
    statusCode: Int,
    message: String,
    original: Request
): Response {
    return Response.Builder()
        .code(statusCode)
        .protocol(Protocol.HTTP_2)
        .body(emptyRequestBody)
        .message(message)
        .request(original)
        .build()
}

@Suppress("unused")
internal fun noIdResponse(original: Request): Response {
    val statusCode = 403
    val message = "Não foi possível recuperar o Id do dispositivo"
    return createResponse(
        statusCode,
        message,
        original
    )
}
