package app.web.diegoflassa_site.littledropsofrain.adapters

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
import app.web.diegoflassa_site.littledropsofrain.databinding.RecyclerviewItemUserBinding
import app.web.diegoflassa_site.littledropsofrain.dialogs.LikesDialogFragment
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataFailureListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import com.squareup.picasso.Picasso
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
            RecyclerviewItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            Picasso.get().load(user!!.imageUrl).placeholder(R.drawable.image_placeholder)
                .into(binding.imgVwUser)

            binding.userName.text = resources.getString(R.string.rv_user_name, user.name)
            binding.userEmail.text = resources.getString(R.string.rv_user_email, user.email)
            val iconHeart = IconDrawable(itemView.context, SimpleLineIconsIcons.icon_heart)
            if (product.likes.contains(user.uid)) {
                iconHeart.setTint(Color.RED)
            }
            binding.imgVwLiked.setImageDrawable(iconHeart)
            binding.imgVwLiked.setOnClickListener {
                if (product.likes.contains(user.uid)) {
                    product.likes.remove(user.uid)
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