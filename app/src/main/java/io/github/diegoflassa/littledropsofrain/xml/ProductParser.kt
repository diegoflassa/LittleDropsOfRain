package io.github.diegoflassa.littledropsofrain.xml

import android.util.Log
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import io.github.diegoflassa.littledropsofrain.data.entities.IluriaProduct
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream


class ProductParser {

    companion object {
        const val TAG = "ProductParser"
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

    var text: String? = null
    private var products = ArrayList<IluriaProduct>()
    fun parse(): List<IluriaProduct>{
        val client = OkHttpClient()
        val request: Request = Request.Builder()
                .url("http://admin.iluria.com/xml/buscape/?user=7C36F628368750071BFF6FF1FCBF56F5E5BB31A40DD39535")
                .build()
        val response : Response = client.newCall(request).execute()
        val inputStream: InputStream = ByteArrayInputStream(response.body().bytes())
        return parse(inputStream)
    }

    private fun parse(`is`: InputStream?): List<IluriaProduct> {
        try {
            val factory =
                XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(`is`, null)
            var eventType = parser.eventType
            var product = IluriaProduct("0")
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.START_TAG ->
                        if (tagname == PRODUTOS) {
                            Log.i(TAG, "Starting parsing products")
                        }else if (tagname == PRODUTO){
                            Log.i(TAG, "Created new product object")
                            product= IluriaProduct("0")
                        }
                    XmlPullParser.END_TAG ->
                        when (tagname) {
                            PRODUTOS -> {
                                Log.i(TAG, "Ending parsing products")
                            }
                            PRODUTO -> {
                                Log.i(TAG, "Added product ${product.idProduct} to the list")
                                products.add(product)
                            }
                            ID_PRODUTO -> {
                                product.idProduct= text.toString()
                                Log.i(TAG, "Setted product ${product.idProduct} to the object")
                            }
                            LINK_PRODUTO -> {
                                product.linkProduct= text
                                Log.i(TAG, "Setted product ${product.linkProduct} to the object")
                            }
                            TITULO -> {
                                product.title= text
                                Log.i(TAG, "Setted product ${product.title} to the object")
                            }
                            PRECO -> {
                                product.price= text
                                Log.i(TAG, "Setted product ${product.price} to the object")
                            }
                            PARCELAMENTO -> {
                                product.installment= text
                                Log.i(TAG, "Setted product ${product.installment} to the object")
                            }
                            DISPONIBILIDADE -> {
                                product.disponibility= text
                                Log.i(TAG, "Setted product ${product.disponibility} to the object")
                            }
                            IMAGEM -> {
                                product.image= text
                                Log.i(TAG, "Setted product ${product.image} to the object")
                            }
                            CATEGORIA -> {
                                product.category= text
                                Log.i(TAG, "Setted product ${product.category} to the object")
                            }
                        }
                        else -> {
                            Log.i(TAG,"Unknown tag : $tagname")
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
