package io.github.diegoflassa.littledropsofrain.helpers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import io.github.diegoflassa.littledropsofrain.R
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
        fun getPriceString(price: Int): String? {
            return when (price) {
                in 0..5000 -> "$"
                in 5100..10000 -> "$$"
                in 10100..100000 -> "$$$"
                else -> "$$$"
            }
        }

        fun sendEmail(context : Context, sendTos : ArrayList<String>, subject : String = "", body : String = ""){
            val inlinedSendTos = sendTos.joinToString { it }
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$inlinedSendTos?subject:$subject?body:$body"))
            intent.putExtra(Intent.EXTRA_EMAIL, ArrayList(sendTos))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, body)
            try {
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.email_send)))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.email_no_clients_installed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}