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

package app.web.diegoflassa_site.littledropsofrain.data.parser

import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.data.entities.IluriaProduct
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class ProductParser(listener: OnParseProgress? = null) {

    interface OnParseProgress {
        fun onParseProgressChange(progress: String)
    }

    companion object {
        val TAG = ProductParser::class.simpleName
        const val PRODUTOS = "produtos"
        const val PRODUTO = "produto"
        const val ID_PRODUTO = "id_produto"
        const val LINK_PRODUTO = "link_produto"
        const val TITULO = "titulo"
        const val PRECO = "preco"
        const val PARCELAMENTO = "parcelamento"
        const val DISPONIBILIDADE = "disponibilidade"
        const val IMAGEM = "imagem"
        const val CATEGORIA = "categoria"
    }

    private val mListener: OnParseProgress? = listener
    private val xmlIluriaSource =
        "http://admin.iluria.com/xml/buscape/?user=7C36F628368750071BFF6FF1FCBF56F5E5BB31A40DD39535"
    var text: String? = null
    private var products = ArrayList<IluriaProduct>()
    fun parse(): List<IluriaProduct> {
        val client = OkHttpClient()
        client.setConnectTimeout(30, TimeUnit.SECONDS) // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS) // socket timeout
        val request: Request = Request.Builder()
            .url(xmlIluriaSource)
            .build()
        val response: Response = client.newCall(request).execute()
        val inputStream: InputStream = ByteArrayInputStream(response.body().bytes())
        return parse(inputStream)
    }

    private fun parse(istream: InputStream?): List<IluriaProduct> {
        try {
            val factory =
                XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(istream, null)
            var eventType = parser.eventType
            var product = IluriaProduct("0")
            val progressBuilder = StringBuilder()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.START_TAG ->
                        if (tagname == PRODUTOS) {
                            mListener?.onParseProgressChange("Starting parsing products")
                            Log.i(TAG, "Starting parsing products")
                        } else if (tagname == PRODUTO) {
                            progressBuilder.clear()
                            progressBuilder.append("Created new product object")
                            Log.i(TAG, "Created new product object")
                            product = IluriaProduct()
                        }
                    XmlPullParser.END_TAG ->
                        when (tagname) {
                            PRODUTOS -> {
                                progressBuilder.append("Ending parsing products")
                                mListener?.onParseProgressChange(progressBuilder.toString())
                                progressBuilder.clear()
                                Log.i(TAG, "Ending parsing products")
                            }
                            PRODUTO -> {
                                progressBuilder.append("Added product ${product.idProduct} to the list${System.lineSeparator()}")
                                mListener?.onParseProgressChange(progressBuilder.toString())
                                Thread.sleep(150)
                                Log.i(TAG, "Added product ${product.idProduct} to the list")
                                products.add(product)
                            }
                            ID_PRODUTO -> {
                                product.idProduct = text.toString()
                                progressBuilder.append("Setted product ${product.idProduct} to the object${System.lineSeparator()}")
                                Log.i(TAG, "Setted product ${product.idProduct} to the object")
                            }
                            LINK_PRODUTO -> {
                                product.linkProduct = text
                                progressBuilder.append("Setted product ${product.linkProduct} to the object${System.lineSeparator()}")
                                Log.i(TAG, "Setted product ${product.linkProduct} to the object")
                            }
                            TITULO -> {
                                product.title = text
                                progressBuilder.append("Setted product ${product.title} to the object${System.lineSeparator()}")
                                Log.i(TAG, "Setted product ${product.title} to the object")
                            }
                            PRECO -> {
                                product.price = text
                                progressBuilder.append("Setted product ${product.price} to the object${System.lineSeparator()}")
                                Log.i(TAG, "Setted product ${product.price} to the object")
                            }
                            PARCELAMENTO -> {
                                product.installment = text
                                progressBuilder.append("Setted product ${product.installment} to the object${System.lineSeparator()}")
                                Log.i(TAG, "Setted product ${product.installment} to the object")
                            }
                            DISPONIBILIDADE -> {
                                product.disponibility = text
                                progressBuilder.append("Setted product ${product.disponibility} to the object${System.lineSeparator()}")
                                Log.i(TAG, "Setted product ${product.disponibility} to the object")
                            }
                            IMAGEM -> {
                                product.image = text
                                progressBuilder.append("Setted product ${product.image} to the object${System.lineSeparator()}")
                                Log.i(TAG, "Setted product ${product.image} to the object")
                            }
                            CATEGORIA -> {
                                product.category = text
                                progressBuilder.append("Setted product ${product.category} to the object${System.lineSeparator()}")
                                Log.i(TAG, "Setted product ${product.category} to the object")
                            }
                        }
                    else -> {
                        Log.i(TAG, "Unknown tag : $tagname")
                        progressBuilder.append("Unknown tag : $tagname")
                    }
                }
                eventType = parser.next()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return products
    }
}
