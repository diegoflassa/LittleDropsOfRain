package io.github.diegoflassa.littledropsofrain.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import io.github.diegoflassa.littledropsofrain.MyApplication
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.helpers.Helper
import io.github.diegoflassa.littledropsofrain.ui.send_message.SendMessageFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class MessageAdapter(query: Query?, private val mListener: OnMessageSelectedListener)
    : FirestoreAdapter<MessageAdapter.ViewHolder?>(query) {

    interface OnMessageSelectedListener {
        fun onMessageSelected(message: DocumentSnapshot?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.recyclerview_item_message, parent,false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val ioScope = CoroutineScope(Dispatchers.IO)
        private val creationDate: TextView = itemView.findViewById(R.id.msg_creation_date)
        private val sender: TextView = itemView.findViewById(R.id.msg_sender)
        private val edtMessage: EditText = itemView.findViewById(R.id.msg_message)
        private val read: SwitchMaterial = itemView.findViewById(R.id.msg_read)
        private val reply: ImageButton = itemView.findViewById(R.id.btn_reply)
        val delete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnMessageSelectedListener?
        ) {
            val message: Message? = snapshot.toObject(Message::class.java)
            message?.uid = snapshot.id

            if(message?.creationDate !=null) {
                creationDate.text = Helper.getDateTime(message.creationDate!!.toDate())
            }
            sender.text = message?.sender
            edtMessage.setText(message?.message)
            if(message?.read !=null)
                read.isChecked = message.read!!
            read.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
                ioScope.launch {
                    message?.read = checked
                    MessageDao.update(message!!)
                }
            }
            itemView.setOnClickListener {
                replyMessage(it, message!!)
            }

            // Click listener
            itemView.setOnClickListener { listener?.onMessageSelected(snapshot) }

            reply.isEnabled = (message?.emailSender != FirebaseAuth.getInstance().currentUser?.email)
            reply.setImageDrawable(IconDrawable(MyApplication.getContext(), SimpleLineIconsIcons.icon_action_undo))
            reply.setOnClickListener {
                replyMessage(it, message!!)
            }
            delete.setImageDrawable(IconDrawable(MyApplication.getContext(), SimpleLineIconsIcons.icon_trash))
            delete.setOnClickListener{
                MessageDao.delete(message)
            }
        }

        private fun replyMessage(view : View, message : Message){
            val messageToEdit = Message()
            messageToEdit.replyUid = message.uid
            messageToEdit.senderId = message.senderId
            messageToEdit.emailSender = FirebaseAuth.getInstance().currentUser?.email
            messageToEdit.sender = sender.text.toString()
            messageToEdit.message = edtMessage.text.toString()
            val bundle = Bundle()
            bundle.putString(SendMessageFragment.ACTION_REPLY_KEY, SendMessageFragment.ACTION_REPLY)
            bundle.putParcelable(SendMessageFragment.KEY_MESSAGE, messageToEdit)
            view.findNavController().navigate(R.id.sendMessageFragment, bundle)
        }
    }
}