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

@file:SuppressWarnings("deprecation", "removal")

package app.web.diegoflassa_site.littledropsofrain.data.old.repository

import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.IluriaProducts
import app.web.diegoflassa_site.littledropsofrain.data.old.interfaces.IluriaProductsDao
import app.web.diegoflassa_site.littledropsofrain.data.old.parser.JsonOrXmlConverterFactory
import io.reactivex.rxjava3.core.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

@SuppressWarnings("deprecation", "removal")
class IluriaProductsRepository {

    private val tag: String? = IluriaProductsRepository::class.simpleName
    private val issuesBaseUrl = "http://admin.iluria.com"

    fun getAll(): Observable<IluriaProducts> {
        val okHttpClient = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(issuesBaseUrl)
            .addConverterFactory(JsonOrXmlConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        Log.i(tag, "IluriaProductsRepository.getAll")
        return retrofit.create(IluriaProductsDao::class.java).getAll()
    }
}
