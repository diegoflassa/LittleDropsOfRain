package io.github.diegoflassa.littledropsofrain.adapters

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.entities.Product


class ProductListAdapter internal constructor(
    private var context: Context
) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var products = emptyList<Product>() // Cached copy of products

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.picture)
        val productTitle: TextView = itemView.findViewById(R.id.title)
        val productDisponibility: TextView = itemView.findViewById(R.id.disponibility)
        val productPrice: TextView = itemView.findViewById(R.id.price)
        val productStoreLink: TextView = itemView.findViewById(R.id.store_link)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = products[position]
        Picasso.get().setIndicatorsEnabled(true)
        Picasso.get().load(current.image).placeholder(R.drawable.image_placeholder).into(holder.productImage)
        holder.productTitle.text = context.getString(R.string.rv_title, current.title)
        holder.productDisponibility.text = context.getString(R.string.rv_disponibility, current.disponibility)
        holder.productPrice.text = context.getString(R.string.rv_price, current.price)
        holder.productStoreLink.text = current.linkProduct
        holder.productStoreLink.isClickable = true
        holder.productStoreLink.movementMethod = LinkMovementMethod.getInstance()
        val link = context.getString(R.string.rv_store_link, current.linkProduct)
        holder.productStoreLink.text = HtmlCompat.fromHtml(link, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    internal fun setWords(words: List<Product?>) {
        this.products = words as List<Product>
        notifyDataSetChanged()
    }

    override fun getItemCount() = products.size
}