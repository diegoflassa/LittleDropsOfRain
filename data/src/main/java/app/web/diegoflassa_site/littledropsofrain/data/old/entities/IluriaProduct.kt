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

package app.web.diegoflassa_site.littledropsofrain.data.old.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JacksonXmlRootElement(localName = "produto")
data class IluriaProduct(
    @JacksonXmlProperty(localName = "id_produto")
    var idProduct: String = "0",
    @JacksonXmlProperty(localName = "link_produto")
    var linkProduct: String? = null,
    @JacksonXmlProperty(localName = "titulo")
    var title: String? = null,
    @JacksonXmlProperty(localName = "preco")
    var price: String? = null,
    @JacksonXmlProperty(localName = "parcelamento")
    var installment: String? = null,
    @JacksonXmlProperty(localName = "disponibilidade")
    var disponibility: String? = null,
    @JacksonXmlProperty(localName = "imagem")
    var image: String? = null,
    @JacksonXmlProperty(localName = "categoria")
    var category: String? = null
) : Parcelable
