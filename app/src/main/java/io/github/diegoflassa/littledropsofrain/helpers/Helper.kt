package io.github.diegoflassa.littledropsofrain.helpers

import io.github.diegoflassa.littledropsofrain.data.entities.IluriaProduct
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import java.text.SimpleDateFormat
import java.util.*

class Helper {

    companion object{

        fun getDateTime(date: Date): String? {
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                sdf.format(date)
            } catch (e: Exception) {
                e.toString()
            }
        }

        fun iluriaProductToProduct(iluriaProducts : List<IluriaProduct>) : List<Product>{
            val productsList : MutableList<Product> = ArrayList(iluriaProducts.size)
            for(iluriaProduct in iluriaProducts)
                productsList.add( iluriaProductToProduct(iluriaProduct) )
            return productsList
        }

        private fun iluriaProductToProduct(iluriaProduct : IluriaProduct) : Product{
            val product = Product()
            product.title= iluriaProduct.title
            val st = StringTokenizer(iluriaProduct.category, ">")
            while(st.hasMoreTokens()){
                product.categories.add(st.nextToken().trim())
            }
            product.idIluria= iluriaProduct.idProduct
            product.price= iluriaProduct.price
            product.disponibility= iluriaProduct.disponibility
            product.imageUrl= iluriaProduct.image
            product.installment= iluriaProduct.installment
            product.linkProduct= iluriaProduct.linkProduct
            return product
        }
    }
}