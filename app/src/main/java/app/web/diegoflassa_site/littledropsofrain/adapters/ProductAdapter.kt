package app.web.diegoflassa_site.littledropsofrain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.databinding.RecyclerviewItemProductBinding
import app.web.diegoflassa_site.littledropsofrain.ui.home.HomeFragment
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import java.text.DecimalFormatSymbols

open class ProductAdapter(private var homeFragment: HomeFragment, query: Query?, private val mListener: OnProductSelectedListener)
    : FirestoreAdapter<ProductAdapter.ViewHolder?>(query) {

    interface OnProductSelectedListener {
        fun onProductSelected(product: DocumentSnapshot?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RecyclerviewItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(homeFragment, binding.root)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(private var homeFragment : HomeFragment, itemView: View) :
        RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {
        val binding = RecyclerviewItemProductBinding.bind(itemView)
        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnProductSelectedListener?
        ) {
            val product: Product? = snapshot.toObject(Product::class.java)
            product?.uid = snapshot.id
            val resources = itemView.resources

            // Load image
            Picasso.get().load(product?.imageUrl).placeholder(R.drawable.image_placeholder).into(binding.picture)
            binding.title.text = resources.getString(R.string.rv_title, product?.title)
            var chipCategory : Chip
            binding.chipCategories.removeAllViews()
            for(category in product?.categories!!) {
                if(category.isNotEmpty()) {
                    chipCategory = Chip(itemView.context)
                    chipCategory.isCheckable = true
                    chipCategory.isChecked = homeFragment.mFilterDialog?.categories!!.contains(category)
                    chipCategory.text = category
                    chipCategory.setOnCheckedChangeListener(this)
                    binding.chipCategories.addView(chipCategory)
                }
            }
            binding.disponibility.text = resources.getString(R.string.rv_disponibility,  product.disponibility)
            var priceStr= (product.price?.div(100)).toString()
            priceStr+= DecimalFormatSymbols.getInstance().decimalSeparator +"00"
            binding.price.text = resources.getString(R.string.rv_price, priceStr)

            // Click listener
            itemView.setOnClickListener { listener?.onProductSelected(snapshot) }
        }

        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            homeFragment.mFilterDialog?.onCheckedChanged(p0, p1)
            homeFragment.onFilter(homeFragment.mFilterDialog!!.filters)
        }

    }
}