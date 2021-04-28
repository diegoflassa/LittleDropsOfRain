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

package app.web.diegoflassa_site.littledropsofrain.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.databinding.RecyclerviewItemProductBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.LoggedUser
import app.web.diegoflassa_site.littledropsofrain.ui.home.HomeFragment
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

open class ProductAdapter(
    private var homeFragment: HomeFragment,
    query: Query?,
    private val mListener: OnProductSelectedListener
) : FirestoreAdapter<ProductAdapter.ViewHolder?>(query) {

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
        return ViewHolder(homeFragment, binding.root)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(private var homeFragment: HomeFragment, itemView: View) :
        RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {
        val binding = RecyclerviewItemProductBinding.bind(itemView)
        private val ioScope = CoroutineScope(Dispatchers.IO)
        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnProductSelectedListener?
        ) {
            val product: Product? = snapshot.toObject(Product::class.java)
            product?.uid = snapshot.id
            val resources = itemView.resources

            // Load image
            binding.picture.load(product?.imageUrl) { placeholder(R.drawable.image_placeholder) }
            binding.title.text = resources.getString(R.string.rv_title, product?.title)
            var chipCategory: Chip
            binding.chipCategories.removeAllViews()
            for (category in product?.categories!!) {
                if (category.isNotEmpty()) {
                    chipCategory = Chip(itemView.context)
                    chipCategory.isCheckable = true
                    chipCategory.isChecked =
                        homeFragment.mFilterDialog?.categories!!.contains(category)
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
                IconDrawable(homeFragment.requireContext(), SimpleLineIconsIcons.icon_heart)
            if (LoggedUser.userLiveData.value != null) {
                if (product.likes.contains(LoggedUser.userLiveData.value?.uid!!)) {
                    heartIcon.color(Color.RED)
                }
                binding.imgVwLike.setOnClickListener {
                    if (product.likes.contains(LoggedUser.userLiveData.value?.uid!!)) {
                        product.likes.remove(LoggedUser.userLiveData.value?.uid!!)
                    } else {
                        product.likes.add(LoggedUser.userLiveData.value?.uid!!)
                    }
                    ioScope.launch {
                        ProductDao.update(product)
                    }
                }
            }
            binding.imgVwLike.setImageDrawable(heartIcon)
            binding.switchIsPublished.visibility = View.GONE
            binding.likesCount.text = product.likes.size.toString()
            // Click listener
            itemView.setOnClickListener { listener?.onProductSelected(snapshot) }
        }

        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            homeFragment.mFilterDialog?.onCheckedChanged(p0, p1)
            homeFragment.onFilter(homeFragment.mFilterDialog!!.filters)
        }
    }
}
