package io.github.diegoflassa.littledropsofrain.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.google.firebase.firestore.DocumentReference
import io.github.diegoflassa.littledropsofrain.data.DataChangeListener
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.databinding.ActivitySendMesssageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SendMessageActivity : AppCompatActivity(), DataChangeListener<List<User>> {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var binding: ActivitySendMesssageBinding

    companion object {
        private const val TAG = "SendMessageActivity"
        const val ACTION_SEND = "io.github.diegoflassa.littledropsofrain.action.ACTION_SEND"
        const val ACTION_EDIT = "io.github.diegoflassa.littledropsofrain.action.ACTION_EDIT"
        const val KEY_MESSAGE = "message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMesssageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSend.setOnClickListener {
            val callback = Callback(this)
            // Coroutine has mutliple dispatchers suited for different type of workloads
            ioScope.launch {
                val message = Message()
                message.title = binding.edttxtTitle.text.toString()
                message.message = binding.mltxtMessage.text.toString()
                message.read = false
                MessageDao.insert(message, callback)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d(TAG, "SendMessageActivity activity created!")
        UserDao.loadAll(this)
        handleIntent()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setSelectedMessageSender(){
        if(intent.action == ACTION_EDIT) {
            val message = intent.extras?.getParcelable<Message>(KEY_MESSAGE)
            if(message!=null) {
                val user = User()
                var name = message.sender
                if (message.sender?.contains(":")!!) {
                    name = message.sender?.split(":")?.get(1)
                }
                user.name = name?.trim()
                user.uid = message.senderId
                user.email = message.emailSender
                val dataAdapter: ArrayAdapter<User> =
                    binding.spnrContacts.adapter as ArrayAdapter<User>
                val spinnerPosition: Int = dataAdapter.getPosition(user)
                binding.spnrContacts.setSelection(spinnerPosition)
            }
        }
    }

    private fun handleIntent() {
        if(intent.action == ACTION_EDIT) {
            val message = intent.extras?.getParcelable<Message>(KEY_MESSAGE)
            if(message!=null) {
                binding.edttxtTitle.setText(message.title)
                binding.mltxtMessage.setText(message.message)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDataLoaded(item: List<User>) {
        val dataAdapter: ArrayAdapter<User> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, item
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnrContacts.adapter= dataAdapter
        setSelectedMessageSender()
    }

    private class Callback( var activity : SendMessageActivity ) : DataChangeListener<DocumentReference> {
        override fun onDataLoaded(item: DocumentReference) {
            Toast.makeText(activity,  "Message sent successfully", Toast.LENGTH_SHORT).show()
            activity.finish()
        }
    }

}