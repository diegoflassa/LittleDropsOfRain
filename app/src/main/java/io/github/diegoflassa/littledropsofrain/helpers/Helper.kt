package io.github.diegoflassa.littledropsofrain.helpers

import com.google.firebase.auth.FirebaseUser
import io.github.diegoflassa.littledropsofrain.data.entities.IluriaProduct
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import io.github.diegoflassa.littledropsofrain.data.entities.User
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

        fun firebaseUserToUser(user : FirebaseUser) : User {
            val userFb = User()
            userFb.uid = user.uid
            userFb.name = user.displayName
            userFb.email = user.email
            userFb.imageUrl = user.photoUrl.toString()
            return userFb
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
            val price= iluriaProduct.price!!.replace(',','.')
            product.price= (price.toFloat()*100).toInt()
            product.disponibility= iluriaProduct.disponibility
            product.imageUrl= iluriaProduct.image
            product.installment= iluriaProduct.installment
            product.linkProduct= iluriaProduct.linkProduct
            return product
        }

        /**
         * Get price represented as dollar signs.
         */
        fun getPriceString(price: MutableList<Int>): String? {
            return when (price[0]) {
                in 0..5000 -> "$"
                in 5100..10000 -> "$$"
                in 10100..100000 -> "$$$"
                else -> "$$$"
            }
        }
    }
}