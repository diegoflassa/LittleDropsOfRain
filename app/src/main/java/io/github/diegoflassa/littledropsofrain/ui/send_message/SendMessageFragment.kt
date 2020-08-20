package io.github.diegoflassa.littledropsofrain.ui.send_message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.DataChangeListener
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.databinding.FragmentSendMessageBinding
import io.github.diegoflassa.littledropsofrain.models.SendMessageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import viewLifecycle

class SendMessageFragment : Fragment(), DataChangeListener<List<User>> {

    companion object {
        fun newInstance() = SendMessageFragment()
        val TAG = SendMessageFragment::class.simpleName
        const val ACTION_SEND_KEY = "ACTION_SEND"
        const val ACTION_SEND = "io.github.diegoflassa.littledropsofrain.action.ACTION_SEND"
        const val ACTION_EDIT_KEY = "ACTION_EDIT"
        const val ACTION_EDIT = "io.github.diegoflassa.littledropsofrain.action.ACTION_EDIT"
        const val KEY_MESSAGE = "message"
        var mSavedInstanceState: Bundle? = null
    }

    private val viewModel: SendMessageViewModel by viewModels()
    private var binding : FragmentSendMessageBinding by viewLifecycle()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendMessageBinding.inflate(layoutInflater)
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
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        UserDao.loadAll(this)
        mSavedInstanceState= savedInstanceState
        handleBundle()
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setNavigationOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            val toggle = ActionBarDrawerToggle(
                activity,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout?.addDrawerListener(toggle)
            toggle.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
        Log.d(TAG, "SendMessageFragment activity created!")
        return binding.root
    }

    @Suppress("UNCHECKED_CAST")
    private fun setSelectedMessageSender(){
        if(mSavedInstanceState!=null && mSavedInstanceState?.getString(ACTION_EDIT_KEY) == ACTION_EDIT) {
            val message = mSavedInstanceState?.getParcelable<Message>(KEY_MESSAGE)
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

    private fun handleBundle() {
        if(mSavedInstanceState!=null && mSavedInstanceState?.getString(ACTION_EDIT_KEY) == ACTION_EDIT) {
            val message = mSavedInstanceState?.getParcelable<Message>(KEY_MESSAGE)
            if(message!=null) {
                binding.edttxtTitle.setText(message.title)
                binding.mltxtMessage.setText(message.message)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(requireActivity())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDataLoaded(item: List<User>) {
        val dataAdapter: ArrayAdapter<User> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, item
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnrContacts.adapter= dataAdapter
        setSelectedMessageSender()
    }

    private class Callback( var fragment : SendMessageFragment) :
        DataChangeListener<DocumentReference> {
        override fun onDataLoaded(item: DocumentReference) {
            Toast.makeText(fragment.requireContext(),  "Message sent successfully", Toast.LENGTH_SHORT).show()
            fragment.activity?.supportFragmentManager?.popBackStack()
        }
    }
}