package io.github.diegoflassa.littledropsofrain.adapters

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import java.text.DecimalFormatSymbols

/**
 * RecyclerView adapter for a list of Restaurants.
 */
open class ProductAdapter(query: Query?, private val mListener: OnProductSelectedListener)
    : FirestoreAdapter<ProductAdapter.ViewHolder?>(query) {

    interface OnProductSelectedListener {
        fun onProductSelected(product: DocumentSnapshot?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.recyclerview_item_product, parent,false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.picture)
        private val productTitle: TextView = itemView.findViewById(R.id.title)
        private val productCategories: TextView = itemView.findViewById(R.id.categories)
        private val productChipCategories: ChipGroup = itemView.findViewById(R.id.chipCategories)
        private val productDisponibility: TextView = itemView.findViewById(R.id.disponibility)
        private val productPrice: TextView = itemView.findViewById(R.id.price)
        private val productStoreLink: TextView = itemView.findViewById(R.id.store_link)

        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnProductSelectedListener?
        ) {
            val product: Product? = snapshot.toObject(Product::class.java)
            product?.uid = snapshot.id
            val resources = itemView.resources

            // Load image
            Picasso.get().load(product?.imageUrl).placeholder(R.drawable.image_placeholder).into(productImage)
            productTitle.text = resources.getString(R.string.rv_title, product?.title)
            productCategories.text = if(!product?.categories.isNullOrEmpty()){resources.getString(R.string.rv_categories, product?.categories)}else{""}
            var chipCategory : Chip
            productChipCategories.removeAllViews()
            for(category in product?.categories!!) {
                if(!product.categories.isNullOrEmpty()) {
                    chipCategory = Chip(itemView.context)
                    chipCategory.isCheckable = true
                    chipCategory.text = category
                    productChipCategories.addView(chipCategory)
                }
            }
            productDisponibility.text = resources.getString(R.string.rv_disponibility,  product.disponibility)
            var priceStr= (product.price?.div(100)).toString()
            priceStr+= DecimalFormatSymbols.getInstance().decimalSeparator +"00"
            productPrice.text = resources.getString(R.string.rv_price, priceStr)
            val link = resources.getString(R.string.rv_store_link, product.linkProduct)
            productStoreLink.text = HtmlCompat.fromHtml(link, HtmlCompat.FROM_HTML_MODE_LEGACY)
            productStoreLink.isClickable = true
            productStoreLink.movementMethod = LinkMovementMethod.getInstance()

            // Click listener
            itemView.setOnClickListener { listener?.onProductSelected(snapshot) }
        }

    }

}