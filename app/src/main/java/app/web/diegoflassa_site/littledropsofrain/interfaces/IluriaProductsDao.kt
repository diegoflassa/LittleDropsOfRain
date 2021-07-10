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

package app.web.diegoflassa_site.littledropsofrain.interfaces

import app.web.diegoflassa_site.littledropsofrain.data.entities.IluriaProducts
import app.web.diegoflassa_site.littledropsofrain.parser.ResponseFormat
import app.web.diegoflassa_site.littledropsofrain.parser.ResponseType
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface IluriaProductsDao {
    @GET("/xml/buscape/?user=7C36F628368750071BFF6FF1FCBF56F5E5BB31A40DD39535")
    @ResponseFormat(ResponseType.XML)
    fun getAll(): Observable<IluriaProducts>
}