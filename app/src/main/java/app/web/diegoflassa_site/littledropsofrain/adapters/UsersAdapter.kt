package app.web.diegoflassa_site.littledropsofrain.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import app.web.diegoflassa_site.littledropsofrain.MyApplication
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.RecyclerviewItemUserBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.LoggedUser
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataFailureListener
import app.web.diegoflassa_site.littledropsofrain.ui.send_message.SendMessageFragment
import app.web.diegoflassa_site.littledropsofrain.ui.users.UsersFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class UsersAdapter(
    usersFragment: UsersFragment,
    query: Query?,
    private val mListener: OnUserSelectedListener
) : FirestoreAdapter<UsersAdapter.ViewHolder?>(query), OnDataChangeListener<Void?>,
    OnDataFailureListener<Exception> {

    private val mUsersFragment: UsersFragment = usersFragment
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private var mOnCheckChangeListener: ((compoundButton: CompoundButton, checked: Boolean) -> Unit)? =
        { _: CompoundButton, _: Boolean -> }

    interface OnUserSelectedListener {
        fun onUserSelected(user: DocumentSnapshot?)
    }

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
        holder.bind(getSnapshot(position), mListener)
        val listener: ((compoundButton: CompoundButton, checked: Boolean) -> Unit)? =
            { _: CompoundButton, checked: Boolean ->
                val user = getSnapshot(holder.adapterPosition).toObject(User::class.java)
                user?.isAdmin = checked
                mUsersFragment.showLoadingScreen()
                mUsersFragment.binding.recyclerview.isEnabled = false
                ioScope.launch {
                    UserDao.update(user!!, this@UsersAdapter, this@UsersAdapter)
                }
            }
        mOnCheckChangeListener = listener
        holder.binding.userIsAdmin.setOnCheckedChangeListener(mOnCheckChangeListener)
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = RecyclerviewItemUserBinding.bind(itemView)
        private val ioScope = CoroutineScope(Dispatchers.IO)

        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnUserSelectedListener?
        ) {
            val user: User? = snapshot.toObject(User::class.java)
            user?.uid = snapshot.id
            val resources = itemView.resources

            // Load image
            Picasso.get().load(user?.imageUrl).placeholder(R.drawable.image_placeholder)
                .into(binding.userPicture)
            binding.userName.text = resources.getString(R.string.rv_user_name, user?.name)
            binding.userEmail.text = resources.getString(R.string.rv_user_email, user?.email)
            binding.userIsAdmin.text = resources.getString(R.string.rv_user_is_admin)
            binding.userIsAdmin.isChecked = user?.isAdmin!!
            binding.btnDeleteUser.setOnClickListener {
                ioScope.launch {
                    UserDao.delete(user)
                }
            }

            binding.btnReplyUser.isEnabled =
                (user.email != LoggedUser.userLiveData.value?.email)
            binding.btnReplyUser.setImageDrawable(
                IconDrawable(
                    MyApplication.getContext(),
                    SimpleLineIconsIcons.icon_envelope
                )
            )
            binding.btnReplyUser.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(
                    SendMessageFragment.ACTION_SEND_KEY,
                    SendMessageFragment.ACTION_SEND
                )
                itemView.findNavController().navigate(R.id.send_message_fragment, bundle)
            }

            binding.btnDeleteUser.setImageDrawable(
                IconDrawable(
                    MyApplication.getContext(),
                    SimpleLineIconsIcons.icon_trash
                )
            )

            binding.userIsAdmin.isEnabled =
                (user.email != LoggedUser.userLiveData.value?.email)
            binding.btnDeleteUser.isEnabled =
                (user.email != LoggedUser.userLiveData.value?.email)

            // Click listener
            if (user.email != LoggedUser.userLiveData.value?.email) {
                itemView.setOnClickListener { listener?.onUserSelected(snapshot) }
            } else {
                itemView.setOnClickListener {
                    Toast.makeText(
                        MyApplication.getContext(), MyApplication.getContext().getString(
                            R.string.cannot_send_message_to_yourself
                        ), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDataChanged(item: Void?) {
        mUsersFragment.hideLoadingScreen()
        mUsersFragment.binding.recyclerview.isEnabled = true
    }

    override fun onDataFailure(exception: Exception) {
        mUsersFragment.hideLoadingScreen()
        mUsersFragment.showToastUnableToChangeUser()
        mUsersFragment.binding.recyclerview.isEnabled = true
    }
}