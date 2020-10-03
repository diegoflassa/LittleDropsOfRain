package app.web.diegoflassa_site.littledropsofrain.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import app.web.diegoflassa_site.littledropsofrain.MyApplication
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.databinding.RecyclerviewItemMessageBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.ui.send_message.SendMessageFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


open class MessageAdapter(
    context: Context,
    query: Query?,
    private val mListener: OnMessageSelectedListener
) : FirestoreAdapter<MessageAdapter.ViewHolder?>(query) {

    private val mContext = context

    interface OnMessageSelectedListener {
        fun onMessageSelected(message: DocumentSnapshot?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RecyclerviewItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(mContext, binding.root)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(context: Context, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val mContext = context
        val binding = RecyclerviewItemMessageBinding.bind(itemView)
        private val ioScope = CoroutineScope(Dispatchers.IO)

        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnMessageSelectedListener?
        ) {
            val message: Message? = snapshot.toObject(Message::class.java)
            message?.uid = snapshot.id

            if (message?.creationDate != null) {
                binding.msgCreationDate.text = Helper.getDateTime(message.creationDate!!.toDate())
            }
            binding.msgSender.text = message?.emailSender
            binding.msgTo.text = message?.emailTo
            binding.msgMessage.setText(message?.message)
            if (message?.read != null)
                binding.msgRead.isChecked = message.read!!
            binding.msgRead.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
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
            when (MessageType.valueOf(message?.type?.toUpperCase(Locale.ROOT)!!)) {
                MessageType.MESSAGE -> {
                    binding.msgImage.visibility = View.GONE
                    binding.btnViewAsNotification.visibility = View.GONE
                    when {
                        message.emailSender == FirebaseAuth.getInstance().currentUser?.email -> {
                            itemView.background = ContextCompat.getDrawable(
                                MyApplication.getContext(),
                                android.R.color.white
                            )
                        }
                        message.emailTo == FirebaseAuth.getInstance().currentUser?.email -> {
                            itemView.background = ContextCompat.getDrawable(
                                MyApplication.getContext(),
                                R.color.colorMessageMine
                            )
                        }
                    }
                }
                MessageType.NOTIFICATION -> {
                    if (message.imageUrl != null) {
                        binding.msgImage.visibility = View.VISIBLE
                        Picasso.get().load(message.imageUrl)
                            .placeholder(R.drawable.image_placeholder)
                            .error(R.drawable.image_placeholder).into(binding.msgImage)
                    } else {
                        binding.msgImage.visibility = View.GONE
                    }
                    binding.btnViewAsNotification.visibility = View.VISIBLE
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.getContext(),
                        R.color.colorMessageNotification
                    )
                }
                MessageType.UNKNOWN -> {
                    // DO nothing
                }
            }

            binding.btnViewAsNotification.setImageDrawable(
                IconDrawable(
                    MyApplication.getContext(),
                    SimpleLineIconsIcons.icon_envelope_letter
                )
            )
            binding.btnViewAsNotification.setOnClickListener {
                Helper.showNotification(
                    mContext,
                    message.getImageUrlAsUri(),
                    "",
                    message.message!!,
                    false
                )
            }

            binding.btnReply.isEnabled =
                (message.emailSender != FirebaseAuth.getInstance().currentUser?.email)
            binding.btnReply.setImageDrawable(
                IconDrawable(
                    MyApplication.getContext(),
                    SimpleLineIconsIcons.icon_action_undo
                )
            )
            binding.btnReply.setOnClickListener {
                replyMessage(it, message)
            }

            binding.btnDelete.setImageDrawable(
                IconDrawable(
                    MyApplication.getContext(),
                    SimpleLineIconsIcons.icon_trash
                )
            )
            binding.btnDelete.setOnClickListener {
                MessageDao.delete(message)
            }
        }

        private fun replyMessage(view: View, message: Message) {
            val messageToEdit = Message()
            messageToEdit.replyUid = message.uid
            messageToEdit.senderId = message.senderId
            messageToEdit.emailSender = FirebaseAuth.getInstance().currentUser?.email
            messageToEdit.sender = binding.msgSender.text.toString()
            messageToEdit.message = binding.msgMessage.text.toString()
            val bundle = Bundle()
            bundle.putString(SendMessageFragment.ACTION_REPLY_KEY, SendMessageFragment.ACTION_REPLY)
            bundle.putParcelable(SendMessageFragment.KEY_MESSAGE, messageToEdit)
            view.findNavController().navigate(R.id.send_message_fragment, bundle)
        }
    }
}