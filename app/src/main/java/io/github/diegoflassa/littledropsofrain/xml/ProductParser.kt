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
import java.util.concurrent.TimeUnit


class ProductParser(listener: OnParseProgress? = null) {

    interface OnParseProgress{
        fun onParseProgressChange(progress : String)
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

    private val mListener : OnParseProgress? = listener
    private val xmlIluriaSource= "http://admin.iluria.com/xml/buscape/?user=7C36F628368750071BFF6FF1FCBF56F5E5BB31A40DD39535"
    var text: String? = null
    private var products = ArrayList<IluriaProduct>()
    fun parse(): List<IluriaProduct>{
        val client = OkHttpClient()
        client.setConnectTimeout(30, TimeUnit.SECONDS) // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS)    // socket timeout
        val request: Request = Request.Builder()
                .url(xmlIluriaSource)
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
                            mListener?.onParseProgressChange("Starting parsing products")
                            Log.i(TAG, "Starting parsing products")
                        }else if (tagname == PRODUTO){
                            mListener?.onParseProgressChange("Created new product object")
                            Log.i(TAG, "Created new product object")
                            product= IluriaProduct()
                        }
                    XmlPullParser.END_TAG ->
                        when (tagname) {
                            PRODUTOS -> {
                                mListener?.onParseProgressChange("Ending parsing products")
                                Log.i(TAG, "Ending parsing products")
                            }
                            PRODUTO -> {
                                mListener?.onParseProgressChange("Added product ${product.idProduct} to the list")
                                Log.i(TAG, "Added product ${product.idProduct} to the list")
                                products.add(product)
                            }
                            ID_PRODUTO -> {
                                product.idProduct= text.toString()
                                mListener?.onParseProgressChange("Setted product ${product.idProduct} to the object")
                                Log.i(TAG, "Setted product ${product.idProduct} to the object")
                            }
                            LINK_PRODUTO -> {
                                product.linkProduct= text
                                mListener?.onParseProgressChange("Setted product ${product.linkProduct} to the object")
                                Log.i(TAG, "Setted product ${product.linkProduct} to the object")
                            }
                            TITULO -> {
                                product.title= text
                                mListener?.onParseProgressChange("Setted product ${product.title} to the object")
                                Log.i(TAG, "Setted product ${product.title} to the object")
                            }
                            PRECO -> {
                                product.price= text
                                mListener?.onParseProgressChange("Setted product ${product.price} to the object")
                                Log.i(TAG, "Setted product ${product.price} to the object")
                            }
                            PARCELAMENTO -> {
                                product.installment= text
                                mListener?.onParseProgressChange("Setted product ${product.installment} to the object")
                                Log.i(TAG, "Setted product ${product.installment} to the object")
                            }
                            DISPONIBILIDADE -> {
                                product.disponibility= text
                                mListener?.onParseProgressChange("Setted product ${product.disponibility} to the object")
                                Log.i(TAG, "Setted product ${product.disponibility} to the object")
                            }
                            IMAGEM -> {
                                product.image= text
                                mListener?.onParseProgressChange("Setted product ${product.image} to the object")
                                Log.i(TAG, "Setted product ${product.image} to the object")
                            }
                            CATEGORIA -> {
                                product.category= text
                                mListener?.onParseProgressChange("Setted product ${product.category} to the object")
                                Log.i(TAG, "Setted product ${product.category} to the object")
                            }
                        }
                        else -> {
                            Log.i(TAG,"Unknown tag : $tagname")
                            mListener?.onParseProgressChange("Unknown tag : $tagname")
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
