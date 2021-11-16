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

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.old.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.User
import app.web.diegoflassa_site.littledropsofrain.data.old.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.old.interfaces.OnDataFailureListener
import app.web.diegoflassa_site.littledropsofrain.databinding.RecyclerviewItemLikeBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.old.fragments.LikesDialogFragment
import coil.load
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class LikesDialogAdapter(
    likesDialogFragment: LikesDialogFragment,
    query: Query?,
    product: Product
) : FirestoreAdapter<LikesDialogAdapter.ViewHolder?>(query),
    OnDataChangeListener<Void?>,
    OnDataFailureListener<Exception> {

    private val mProduct = product
    private val mLikesDialogFragment: LikesDialogFragment = likesDialogFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RecyclerviewItemLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(mProduct, getSnapshot(position))
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = RecyclerviewItemLikeBinding.bind(itemView)
        private val ioScope = CoroutineScope(Dispatchers.IO)

        fun bind(
            product: Product,
            snapshot: DocumentSnapshot,
        ) {
            val user: User? = snapshot.toObject(User::class.java)
            if (user != null) {
                user.uid = snapshot.id
                val resources = itemView.resources

                binding.imgVwUser.load(user.imageUrl) { placeholder(R.drawable.image_placeholder) }

                binding.userName.text = resources.getString(R.string.rv_user_name, user.name)
                binding.userEmail.text = resources.getString(R.string.rv_user_email, user.email)
                val iconHeart = IconDrawable(itemView.context, SimpleLineIconsIcons.icon_heart)
                if (product.likes.contains(user.uid)) {
                    iconHeart.color(Color.RED)
                }
                binding.imgVwLiked.setImageDrawable(iconHeart)
                binding.imgVwLiked.setOnClickListener {
                    if (product.likes.contains(user.uid)) {
                        val builder = AlertDialog.Builder(itemView.context)
                        builder.setMessage(
                            itemView.context.getString(
                                R.string.remove_like_from_user,
                                user.name
                            )
                        )
                            .setCancelable(false)
                            .setPositiveButton(itemView.context.getString(R.string.yes)) { _, _ ->
                                product.likes.remove(user.uid)
                                ioScope.launch {
                                    ProductDao.update(product)
                                }
                            }
                            .setNegativeButton(itemView.context.getString(R.string.no)) { dialog, _ ->
                                // Dismiss the dialog
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()
                    } else {
                        product.likes.add(user.uid!!)
                    }
                    ioScope.launch {
                        ProductDao.update(product)
                    }
                }
            }
        }
    }

    override fun onDataChanged(item: Void?) {
        mLikesDialogFragment.hideLoadingScreen()
        mLikesDialogFragment.binding.rcVwLikes.isEnabled = true
    }

    override fun onDataFailure(exception: Exception) {
        mLikesDialogFragment.hideLoadingScreen()
        mLikesDialogFragment.binding.rcVwLikes.isEnabled = true
    }
}
