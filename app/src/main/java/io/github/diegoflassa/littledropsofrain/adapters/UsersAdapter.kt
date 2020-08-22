package io.github.diegoflassa.littledropsofrain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.DataChangeListener
import io.github.diegoflassa.littledropsofrain.data.DataFailureListener
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.ui.users.UsersFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class UsersAdapter(usersFragment : UsersFragment, query: Query?, private val mListener: OnUserSelectedListener)
    : FirestoreAdapter<UsersAdapter.ViewHolder?>(query), DataChangeListener<Void?>, DataFailureListener<Exception> {

    private var mIsUpdating : Boolean = false
    private val mUsersFragment : UsersFragment = usersFragment
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private var mOnCheckChangeListener : ((compoundButton : CompoundButton, checked : Boolean) -> Unit)? = { _: CompoundButton, _: Boolean -> }

    interface OnUserSelectedListener {
        fun onUserSelected(user: DocumentSnapshot?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.recyclerview_item_user, parent,false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getSnapshot(position), mListener)
        val listener: ((compoundButton : CompoundButton, checked : Boolean) -> Unit)? = { compoundButton: CompoundButton, checked: Boolean ->
            if(!mIsUpdating&&holder.adapterPosition>-1) {
                val user = getSnapshot(holder.adapterPosition).toObject(User::class.java)
                var canSwitchUser = false
                val canChangeUserToNonAdmin = canChangeUserToNonAdmin()
                if (!canChangeUserToNonAdmin) {
                    canSwitchUser = canSwitchUser(user?.name)
                }
                if (!checked && canChangeUserToNonAdmin || canSwitchUser || checked) {
                    user?.isAdmin = checked
                    mUsersFragment.showLoadingScreen()
                    mUsersFragment.binding.recyclerview.isEnabled = false
                    ioScope.launch {
                        mIsUpdating= true
                        UserDao.update(user!!, this@UsersAdapter, this@UsersAdapter)
                    }
                } else {
                    mUsersFragment.showCantChangeUserToast()
                    //compoundButton.setOnCheckedChangeListener(null)
                    compoundButton.isChecked = !checked
                    //compoundButton.setOnCheckedChangeListener(mOnCheckChangeListener)
                }
            }
        }
        mOnCheckChangeListener = listener
        //holder.setIsRecyclable(false);
        holder.userIsAdmin.setOnCheckedChangeListener(mOnCheckChangeListener)
    }

    private fun canSwitchUser(name: String?): Boolean {
        var ret= false
        for(userSnapshot in getSnapshots()){
            val user = userSnapshot.toObject(User::class.java)
            if(user?.isAdmin!!){
                if(user.name != name) {
                    ret = true
                    break
                }
            }
        }
        return ret
    }

    private fun canChangeUserToNonAdmin(): Boolean {
        var ret= false
        var adminCount = 0
        for(userSnapshot in getSnapshots()){
            val user = userSnapshot.toObject(User::class.java)
            if(user?.isAdmin!!){
                adminCount++
                if(adminCount>1){
                    ret= true
                    break
                }
            }
        }
        return ret
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val ioScope = CoroutineScope(Dispatchers.IO)
        private val userImage: ImageView = itemView.findViewById(R.id.user_picture)
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val userEmail: TextView = itemView.findViewById(R.id.user_email)
        private val buttonDelete: Button = itemView.findViewById(R.id.btn_delete_user)
        val userIsAdmin: SwitchMaterial = itemView.findViewById(R.id.user_is_admin)

        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnUserSelectedListener?
        ) {
            val user: User? = snapshot.toObject(User::class.java)
            user?.uid = snapshot.id
            val resources = itemView.resources

            // Load image
            Picasso.get().load(user?.imageUrl).placeholder(R.drawable.image_placeholder).into(userImage)
            userName.text = resources.getString(R.string.rv_user_name, user?.name)
            userEmail.text = resources.getString(R.string.rv_user_email, user?.email)
            userIsAdmin.text = resources.getString(R.string.rv_user_is_admin)
            userIsAdmin.isChecked = user?.isAdmin!!
            buttonDelete.setOnClickListener {
                ioScope.launch{
                    UserDao.delete(user)
                }
            }

            // Click listener
            itemView.setOnClickListener { listener?.onUserSelected(snapshot) }
        }
    }

    override fun onDataLoaded(item: Void?) {
        mUsersFragment.hideLoadingScreen()
        mUsersFragment.binding.recyclerview.isEnabled = true
        mIsUpdating = false
    }

    override fun onDataFailure(exception: Exception) {
        mUsersFragment.hideLoadingScreen()
        mUsersFragment.showToastUnableToChangeUser()
        mUsersFragment.binding.recyclerview.isEnabled = true
        mIsUpdating = false
    }
}