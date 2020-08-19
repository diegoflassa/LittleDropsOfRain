package io.github.diegoflassa.littledropsofrain.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.helpers.Helper
import io.github.diegoflassa.littledropsofrain.ui.send_message.SendMessageFragment


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
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val title: TextView = itemView.findViewById(R.id.msg_title)
        private val creationDate: TextView = itemView.findViewById(R.id.msg_creation_date)
        private val sender: TextView = itemView.findViewById(R.id.msg_sender)
        private val edtMessage: EditText = itemView.findViewById(R.id.msg_message)
        private val read: SwitchMaterial = itemView.findViewById(R.id.msg_read)
        private val reply: Button = itemView.findViewById(R.id.btn_reply)
        val delete: Button = itemView.findViewById(R.id.btn_delete)

        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnMessageSelectedListener?
        ) {
            val message: Message? = snapshot.toObject(Message::class.java)
            message?.uid = snapshot.id
            val resources = itemView.resources

            title.text = resources.getString(R.string.msg_title, message?.title)
            if(message?.creationDate !=null) {
                creationDate.text = resources.getString(R.string.msg_creation_date, Helper.getDateTime(message.creationDate!!.toDate()))
            }
            sender.text = resources.getString(R.string.msg_sender, message?.sender)
            edtMessage.setText(message?.message)
            if(message?.read !=null)
                read.isChecked = message.read!!
            itemView.setOnClickListener(this)

            // Click listener
            itemView.setOnClickListener { listener?.onMessageSelected(snapshot) }
            reply.setOnClickListener(this)
            delete.setOnClickListener{
                MessageDao.delete(message)
            }
        }

        override fun onClick(v: View?) {
            val message = Message()
            message.sender = sender.text.toString()
            message.title = title.text.toString()
            message.message = edtMessage.text.toString()
            val bundle = Bundle()
            bundle.putString(SendMessageFragment.ACTION_EDIT_KEY, SendMessageFragment.ACTION_EDIT)
            bundle.putParcelable(SendMessageFragment.KEY_MESSAGE, message)
            v?.findNavController()?.navigate(R.id.sendMessageFragment, bundle)
        }
    }
}