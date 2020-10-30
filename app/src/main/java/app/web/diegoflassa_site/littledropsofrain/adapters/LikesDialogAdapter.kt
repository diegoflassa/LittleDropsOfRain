package app.web.diegoflassa_site.littledropsofrain.adapters

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.RecyclerviewItemLikeBinding
import app.web.diegoflassa_site.littledropsofrain.dialogs.LikesDialogFragment
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataFailureListener
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
) : FirestoreAdapter<LikesDialogAdapter.ViewHolder?>(query), OnDataChangeListener<Void?>,
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
            user?.uid = snapshot.id
            val resources = itemView.resources

            binding.imgVwUser.load(user!!.imageUrl) { placeholder(R.drawable.image_placeholder) }

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

    override fun onDataChanged(item: Void?) {
        mLikesDialogFragment.hideLoadingScreen()
        mLikesDialogFragment.binding.rcVwLikes.isEnabled = true
    }

    override fun onDataFailure(exception: Exception) {
        mLikesDialogFragment.hideLoadingScreen()
        mLikesDialogFragment.binding.rcVwLikes.isEnabled = true
    }
}