package io.github.diegoflassa.littledropsofrain.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.activities.SendMessageActivity
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.helpers.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.parceler.Parcels


internal class MessageAdapter(
    val context: Context,
    messages: MutableList<Message>
) :
    RecyclerView.Adapter<MessageAdapter.AppViewHolder>() {
    private val data: MutableList<Message> = messages
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppViewHolder {
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.recyclerview_item_message, parent, false)
        return AppViewHolder(itemView, context)
    }

    override fun onBindViewHolder(
        holder: AppViewHolder,
        position: Int
    ) {
        holder.bind(data[position], context)
        holder.reply.setOnClickListener {
            context.startActivity(Intent(SendMessageActivity.ACTION_EDIT).putExtra(SendMessageActivity.KEY_MESSAGE, Parcels.wrap(data[position])))
        }
        holder.delete.setOnClickListener {
            ioScope.launch {
                MessageDao.delete(data[position])
                data.remove(data[position])
            }
            notifyDataSetChanged()
        }
        holder.read.setOnCheckedChangeListener { _, b ->
            data[position].read = b
            ioScope.launch {
                MessageDao.update(data[position])
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    internal class AppViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val title: TextView = itemView.findViewById(R.id.msg_title)
        private val creationDate: TextView = itemView.findViewById(R.id.msg_creation_date)
        private val sender: TextView = itemView.findViewById(R.id.msg_sender)
        private val edtMessage: EditText = itemView.findViewById(R.id.msg_message)
        val read: Switch = itemView.findViewById(R.id.msg_read)
        val reply: Button = itemView.findViewById(R.id.btn_reply)
        val delete: Button = itemView.findViewById(R.id.btn_delete)

        fun bind(message: Message, context : Context) {
            title.text = context.getString(R.string.msg_title, message.title)
            if(message.creationDate!=null) {
                creationDate.text = context.getString(R.string.msg_creation_date, Helper.getDateTime(message.creationDate!!.toDate()))
            }
            sender.text = context.getString(R.string.msg_sender, message.sender)
            edtMessage.setText(message.message)
            if(message.read!=null)
                read.isSelected = message.read!!
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val message = Message()
            message.sender = sender.text.toString()
            message.title = title.text.toString()
            message.message = edtMessage.text.toString()
            context.startActivity(Intent(SendMessageActivity.ACTION_EDIT).putExtra(SendMessageActivity.KEY_MESSAGE, Parcels.wrap(message)))
        }
    }

}