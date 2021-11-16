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

package app.web.diegoflassa_site.littledropsofrain.presentation.old.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.old.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.Product
import app.web.diegoflassa_site.littledropsofrain.databinding.RecyclerviewItemProductBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.old.ui.all_products.AllProductsFragment
import app.web.diegoflassa_site.littledropsofrain.presentation.old.ui.all_products.AllProductsFragmentDirections
import coil.load
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormatSymbols

@ExperimentalStdlibApi
open class AllProductsAdapter(
    private var allProductsFragment: AllProductsFragment,
    query: Query?,
    private val mListener: OnProductSelectedListener
) : FirestoreAdapter<AllProductsAdapter.ViewHolder?>(query) {

    interface OnProductSelectedListener {
        fun onProductSelected(product: DocumentSnapshot?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RecyclerviewItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(allProductsFragment, binding.root)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getSnapshot(position), mListener)
    }

    @ExperimentalStdlibApi
    class ViewHolder(private var allProductsFragment: AllProductsFragment, itemView: View) :
        RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {
        val binding = RecyclerviewItemProductBinding.bind(itemView)
        private val ioScope = CoroutineScope(Dispatchers.IO)
        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnProductSelectedListener?
        ) {
            val product: Product? = snapshot.toObject(Product::class.java)
            if (product != null) {
                product.uid = snapshot.id
                val resources = itemView.resources

                // Load image
                binding.picture.load(product.imageUrl) { placeholder(R.drawable.image_placeholder) }
                binding.title.text = resources.getString(R.string.rv_title, product.title)
                var chipCategory: Chip
                binding.chipCategories.removeAllViews()
                for (category in product.categories) {
                    if (category.isNotEmpty()) {
                        chipCategory = Chip(itemView.context)
                        chipCategory.isCheckable = true
                        chipCategory.isChecked =
                            allProductsFragment.mFilterDialog?.categories!!.contains(category)
                        chipCategory.text = category
                        chipCategory.setOnCheckedChangeListener(this)
                        binding.chipCategories.addView(chipCategory)
                    }
                }
                binding.disponibility.text =
                    resources.getString(R.string.rv_disponibility, product.disponibility)
                var priceStr = (product.price?.div(100)).toString()
                priceStr += DecimalFormatSymbols.getInstance().decimalSeparator + "00"
                binding.price.text = resources.getString(R.string.rv_price, priceStr)
                val heartIcon =
                    IconDrawable(
                        allProductsFragment.requireContext(),
                        SimpleLineIconsIcons.icon_heart
                    )
                if (product.likes.size > 0) {
                    heartIcon.color(Color.RED)
                    binding.imgVwLike.setOnClickListener {
                        allProductsFragment.findNavController().navigate(
                            AllProductsFragmentDirections.actionNavAllProductsToLikesDialogFragment(
                                product
                            )
                        )
                        /*
                LikesDialogFragment(product).show(
                    allProductsFragment.requireActivity().supportFragmentManager,
                    LikesDialogFragment.TAG
                )
                */
                    }
                }
                binding.imgVwLike.setImageDrawable(heartIcon)
                binding.switchIsPublished.isChecked = product.isPublished
                binding.switchIsPublished.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
                    product.isPublished = checked
                    ioScope.launch {
                        ProductDao.update(product)
                    }
                }
                binding.likesCount.text = product.likes.size.toString()

                // Click listener
                itemView.setOnClickListener { listener?.onProductSelected(snapshot) }
            }
        }

        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            allProductsFragment.mFilterDialog?.onCheckedChanged(p0, p1)
            allProductsFragment.onFilter(allProductsFragment.mFilterDialog!!.filters)
        }
    }
}
