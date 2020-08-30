package io.github.diegoflassa.littledropsofrain.ui.send_message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.databinding.FragmentSendMessageBinding
import io.github.diegoflassa.littledropsofrain.helpers.Helper
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.interfaces.OnDataChangeListener
import io.github.diegoflassa.littledropsofrain.interfaces.OnUserFoundListener
import io.github.diegoflassa.littledropsofrain.interfaces.OnUsersLoadedListener
import io.github.diegoflassa.littledropsofrain.models.SendMessageViewModel
import io.github.diegoflassa.littledropsofrain.models.SendMessageViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class SendMessageFragment : Fragment(), OnUserFoundListener,
    OnUsersLoadedListener {

    companion object {
        fun newInstance() = SendMessageFragment()
        val TAG = SendMessageFragment::class.simpleName
        const val ACTION_SEND_KEY = "ACTION_SEND"
        const val ACTION_SEND = "io.github.diegoflassa.littledropsofrain.action.ACTION_SEND"
        const val ACTION_REPLY_KEY = "ACTION_EDIT"
        const val ACTION_REPLY = "io.github.diegoflassa.littledropsofrain.action.ACTION_EDIT"
        const val KEY_MESSAGE = "message"
        const val KEY_TAG = R.array.send_modes_values
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
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(viewModel.viewState)
        })
        viewModel.viewState.title = binding.edttxtTitle.text.toString()
        viewModel.viewState.body = binding.mltxtMessage.text.toString()
        binding.btnSend.setOnClickListener {
            val callback = Callback(this)
            // Coroutine has multiple dispatchers suited for different type of workloads
            ioScope.launch {
                when(viewModel.viewState.sendMethod){
                    SendMessageViewState.SendMethod.MESSAGE ->{
                        val message = Message()
                        message.title = binding.edttxtTitle.text.toString()
                        message.message = binding.mltxtMessage.text.toString()
                        message.senderId = viewModel.viewState.sender.uid
                        message.sender = viewModel.viewState.sender.email
                        message.read = false
                        MessageDao.insert(message, callback)
                    }
                    SendMessageViewState.SendMethod.EMAIL ->{
                        val sendTos = ArrayList<String>()
                        sendTos.add(binding.spnrContacts.selectedItem.toString())
                        Helper.sendEmail(requireContext(), sendTos, binding.edttxtTitle.text.toString(), binding.mltxtMessage.text.toString())
                    }
                    SendMessageViewState.SendMethod.UNKNOWN ->{
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.no_send_method_selected),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
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
        setupRadioGroupSendMethods()
        setupViewForUser()
        binding.btnSend.isEnabled = false
        Log.d(TAG, "SendMessageFragment activity created!")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }

    private fun setupViewForUser() {
        if(FirebaseAuth.getInstance().currentUser!=null){
            val user = Helper.firebaseUserToUser(FirebaseAuth.getInstance().currentUser!!)
            UserDao.findByEMail(user.email, this)
        }else{
            onUserFound(null)
        }
    }

    private fun setupRadioGroupSendMethods() {
        val sendModes = activity?.resources?.getStringArray(R.array.send_modes_entries)
        val sendModesValues = activity?.resources?.getStringArray(R.array.send_modes_values)
        if (sendModes != null) {
            for ((index, sendMode) in sendModes.withIndex()) {
                val rdMode = RadioButton(requireContext())
                if(sendMode == SendMessageViewState.SendMethod.MESSAGE.toString())
                    rdMode.isSelected = true
                rdMode.text = sendMode
                rdMode.setTag(KEY_TAG, sendModesValues!![index])
                binding.rdGrpSendMethod.addView(rdMode)
            }
        }
        binding.rdGrpSendMethod.setOnCheckedChangeListener { radioGroup: RadioGroup, checkedId: Int ->
            val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
            val sendMethod = radioButton.getTag(KEY_TAG) as String
            viewModel.viewState.sendMethod = SendMessageViewState.SendMethod.valueOf(
                sendMethod.toUpperCase(
                    Locale.ROOT
                )
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateUI(viewState: SendMessageViewState) {
        // Update the UI
        binding.edttxtTitle.setText(viewState.title)
        binding.mltxtMessage.setText(viewState.body)
        if(viewState.isUserAdmin && binding.spnrContacts.adapter!=null) {
            val user = viewState.dest
            val dataAdapter: ArrayAdapter<User> =
                binding.spnrContacts.adapter as ArrayAdapter<User>
            val spinnerPosition: Int = dataAdapter.getPosition(user)
            binding.spnrContacts.setSelection(spinnerPosition)
        }else{
            binding.spnrContacts.setSelection(-1)
        }
        if(viewState.isUserAdmin) {
            for (view in binding.rdGrpSendMethod.children) {
                val radioButton = view as RadioButton
                radioButton.isChecked =
                    (radioButton.getTag(KEY_TAG) == viewState.sendMethod.toString())
            }
        }else{
            for (view in binding.rdGrpSendMethod.children) {
                val radioButton = view as RadioButton
                if(radioButton.text == SendMessageViewState.SendMethod.MESSAGE.toString()){
                    radioButton.isChecked
                    break
                }
            }
        }
        if(!viewState.isUserAdmin){
            binding.spnrContacts.visibility = View.GONE
            binding.rdGrpSendMethod.visibility = View.GONE
        }else{
            binding.spnrContacts.visibility = View.VISIBLE
            binding.rdGrpSendMethod.visibility = View.VISIBLE
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setSelectedMessageSender(){
        if(mSavedInstanceState!=null &&(mSavedInstanceState?.getString(ACTION_REPLY_KEY) == ACTION_REPLY||mSavedInstanceState?.getString(ACTION_SEND_KEY) == ACTION_SEND)) {
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
        if (mSavedInstanceState != null){
            if (mSavedInstanceState?.getString(ACTION_REPLY_KEY) == ACTION_SEND) {
                setSelectedMessageSender()
            } else if (mSavedInstanceState?.getString(ACTION_REPLY_KEY) == ACTION_REPLY) {
                val message = mSavedInstanceState?.getParcelable<Message>(KEY_MESSAGE)
                if (message != null) {
                    binding.edttxtTitle.setText(message.title)
                    binding.mltxtMessage.setText(message.message)
                }
            }
            setSelectedMessageSender()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(requireActivity())
        }
        return super.onOptionsItemSelected(item)
    }

    private class Callback( var fragment : SendMessageFragment) :
        OnDataChangeListener<DocumentReference> {
        override fun onDataLoaded(item: DocumentReference) {
            Toast.makeText(fragment.requireContext(),  "Message sent successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUserFound(user: User?) {
        viewModel.viewState.sender = user?: User()
        viewModel.viewState.isUserAdmin = !(user==null || !user.isAdmin)
        binding.btnSend.isEnabled = true
        updateUI(viewModel.viewState)
    }

    override fun onUsersLoaded(users: List<User>) {
        val dataAdapter: ArrayAdapter<User> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, users
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnrContacts.adapter= dataAdapter
        binding.spnrContacts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                viewModel.viewState.dest = (binding.spnrContacts.adapter.getItem(pos) as User)
            }
            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                viewModel.viewState.dest = User()
            }
        }
        setSelectedMessageSender()
    }
}