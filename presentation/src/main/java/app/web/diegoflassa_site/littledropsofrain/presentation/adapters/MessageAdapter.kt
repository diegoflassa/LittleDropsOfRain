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

package app.web.diegoflassa_site.littledropsofrain.presentation.adapters

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.databinding.RecyclerviewItemMessageBinding
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.presentation.MyApplication
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.send_message.SendMessageFragment
import coil.load
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
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

    @ExperimentalStdlibApi
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

        @ExperimentalStdlibApi
        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnMessageSelectedListener?
        ) {
            val message: Message? = snapshot.toObject(Message::class.java)
            if (message != null) {
                message.uid = snapshot.id

                if (message.creationDate != null) {
                    binding.msgCreationDate.text =
                        Helper.getDateTime(message.creationDate!!.toDate())
                }
                binding.msgSender.text = message.emailSender
                binding.msgTo.text = message.emailTo
                binding.msgMessage.setText(message.message)
                if (message.read != null)
                    binding.msgRead.isChecked = message.read!!
                binding.msgRead.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
                    ioScope.launch {
                        message.read = checked
                        MessageDao.update(message)
                    }
                }
                itemView.setOnClickListener {
                    replyMessage(it, message)
                }
                binding.msgImage.visibility = View.GONE
                binding.btnViewAsNotification.visibility = View.GONE

                // Click listener
                itemView.setOnClickListener { listener?.onMessageSelected(snapshot) }
                when (MessageType.valueOf(message.type?.uppercase(Locale.ROOT)!!)) {
                    MessageType.MESSAGE -> {
                        when {
                            message.emailSender == app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser.userLiveData.value?.email -> {
                                itemView.background = ContextCompat.getDrawable(
                                    MyApplication.getContext(),
                                    android.R.color.white
                                )
                            }
                            message.emailTo == app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser.userLiveData.value?.email -> {
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
                            binding.msgImage.load(message.imageUrl) {
                                placeholder(R.drawable.image_placeholder)
                                error(R.drawable.image_placeholder)
                            }
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
                        // Do nothing
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
                    (message.emailSender != app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser.userLiveData.value?.email)
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
                    val builder = AlertDialog.Builder(mContext)
                    builder.setMessage(mContext.getString(R.string.remove_message_confirmation))
                        .setCancelable(false)
                        .setPositiveButton(mContext.getString(R.string.yes)) { _, _ ->
                            ioScope.launch {
                                MessageDao.delete(message)
                            }
                        }
                        .setNegativeButton(mContext.getString(R.string.no)) { dialog, _ ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }
        }

        private fun replyMessage(view: View, message: Message) {
            val messageToEdit = Message()
            messageToEdit.replyUid = message.uid
            messageToEdit.senderId = message.senderId
            messageToEdit.emailSender = app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser.userLiveData.value?.email
            messageToEdit.sender = binding.msgSender.text.toString()
            messageToEdit.message = binding.msgMessage.text.toString()
            val bundle = Bundle()
            bundle.putString(SendMessageFragment.ACTION_REPLY_KEY, SendMessageFragment.ACTION_REPLY)
            bundle.putParcelable(SendMessageFragment.KEY_MESSAGE, messageToEdit)
            view.findNavController().navigate(R.id.send_message_fragment, bundle)
        }
    }
}
