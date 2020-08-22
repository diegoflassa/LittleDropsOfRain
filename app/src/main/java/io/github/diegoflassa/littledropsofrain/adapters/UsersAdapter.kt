package io.github.diegoflassa.littledropsofrain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class UsersAdapter(query: Query?, private val mListener: OnUserSelectedListener)
    : FirestoreAdapter<UsersAdapter.ViewHolder?>(query) {

    private val ioScope = CoroutineScope(Dispatchers.IO)

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
        holder.userIsAdmin.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            if(!checked&&canChangeUserToNonAdmin()||checked){
                val user = getSnapshot(position).toObject(User::class.java)
                user?.isAdmin = checked
                ioScope.launch {
                    UserDao.update(user!!)
                }
            }
        }
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
        RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {
        private val userImage: ImageView = itemView.findViewById(R.id.user_picture)
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val userEmail: TextView = itemView.findViewById(R.id.user_email)
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

            // Click listener
            itemView.setOnClickListener { listener?.onUserSelected(snapshot) }
        }

        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            //usersFragment.mFilterDialog.onCheckedChanged(p0, p1)
            //usersFragment.onFilter(homeFragment.mFilterDialog.filters)
        }

    }
}