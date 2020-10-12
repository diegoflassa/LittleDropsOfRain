package app.web.diegoflassa_site.littledropsofrain.ui.send_message

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
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentSendMessageBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.*
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnUsersLoadedListener
import app.web.diegoflassa_site.littledropsofrain.models.SendMessageViewModel
import app.web.diegoflassa_site.littledropsofrain.models.SendMessageViewState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class SendMessageFragment : Fragment(),
    OnUsersLoadedListener {

    companion object {
        fun newInstance() = SendMessageFragment()
        val TAG = SendMessageFragment::class.simpleName
        const val ACTION_SEND_KEY = "ACTION_SEND"
        const val ACTION_SEND = "app.web.diegoflassa_site.littledropsofrain.action.ACTION_SEND"
        const val ACTION_REPLY_KEY = "ACTION_REPLY"
        const val ACTION_REPLY = "app.web.diegoflassa_site.littledropsofrain.action.ACTION_REPLY"
        const val KEY_MESSAGE = "message"
        var mSavedInstanceState: Bundle? = null
    }

    private val viewModel: SendMessageViewModel by viewModels()
    private var binding: FragmentSendMessageBinding by viewLifecycle()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var toggle: ActionBarDrawerToggle
    private var isStopped = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendMessageBinding.inflate(layoutInflater)
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(viewModel.viewState)
        })
        binding.btnSend.setOnClickListener {
            val callback = Callback(this)
            // Coroutine has multiple dispatchers suited for different type of workloads
            ioScope.launch {
                when (viewModel.viewState.sendMethod) {
                    SendMessageViewState.SendMethod.MESSAGE -> {
                        val message = Message()
                        message.replyUid = viewModel.viewState.replyUid
                        message.owners.add(viewModel.viewState.sender.email.toString())
                        if (viewModel.viewState.isUserAdmin) {
                            message.owners.add(viewModel.viewState.dest.email.toString())
                        }
                        message.replyUid = viewModel.viewState.replyUid
                        message.emailTo = viewModel.viewState.dest.email
                        message.message = binding.mltxtMessage.text.toString()


                        message.senderId = viewModel.viewState.sender.uid
                        message.sender = viewModel.viewState.sender.email
                        message.read = false
                        MessageDao.insert(message, callback)
                    }
                    SendMessageViewState.SendMethod.EMAIL -> {
                        val sendTos = ArrayList<String>()
                        sendTos.add(binding.spnrContacts.selectedItem.toString())
                        Helper.sendEmail(
                            requireContext(),
                            sendTos,
                            binding.edttxtTitle.text.toString(),
                            binding.mltxtMessage.text.toString()
                        )
                    }
                    SendMessageViewState.SendMethod.UNKNOWN -> {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.no_send_method_selected),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                runOnUiThread {
                    binding.btnSend.isEnabled = false
                    binding.mltxtMessage.text.clear()
                }
            }
        }
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        UserDao.loadAll(this)
        mSavedInstanceState = savedInstanceState
        handleBundle()
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setNavigationOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            toggle = ActionBarDrawerToggle(
                activity,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout?.addDrawerListener(toggle)
            if (drawerLayout != null)
                toggle.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
        setupRadioGroupSendMethods()
        setupViewForUser()
        binding.btnSend.isEnabled = false
        Log.d(TAG, "SendMessageFragment activity created!")
        return binding.root
    }

    override fun onStop() {
        isStopped= true
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        isStopped=false
    }

    override fun onDestroyView() {
        if (this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(isSafeToAccessViewModel()&&!isStopped) {
            viewModel.viewState.title = binding.edttxtTitle.text.toString()
            viewModel.viewState.body = binding.mltxtMessage.text.toString()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateUI(viewModel.viewState)
    }

    private fun setupViewForUser() {
        viewModel.viewState.sender = LoggedUser.userLiveData.value ?: User()
        viewModel.viewState.isUserAdmin =
            !(LoggedUser.userLiveData.value == null || !LoggedUser.userLiveData.value!!.isAdmin)
        binding.btnSend.isEnabled = true
        updateUI(viewModel.viewState)
    }

    private fun setupRadioGroupSendMethods() {
        val sendModes = activity?.resources?.getStringArray(R.array.send_modes_entries)
        val sendModesValues = activity?.resources?.getStringArray(R.array.send_modes_values)
        if (sendModes != null) {
            for ((index, sendMode) in sendModes.withIndex()) {
                val rdMode = RadioButton(requireContext())
                if (sendModesValues!![index] == SendMessageViewState.SendMethod.MESSAGE.toString())
                    rdMode.isSelected = true
                rdMode.text = sendMode
                rdMode.tag = sendModesValues[index]
                binding.rdGrpSendMethod.addView(rdMode)
            }
        }
        binding.rdGrpSendMethod.setOnCheckedChangeListener { radioGroup: RadioGroup, checkedId: Int ->
            val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
            val sendMethod = radioButton.tag as String
            when (SendMessageViewState.SendMethod.valueOf(sendMethod.toUpperCase(Locale.ROOT))) {
                SendMessageViewState.SendMethod.EMAIL -> {
                    binding.edttxtTitle.visibility = View.VISIBLE
                }
                SendMessageViewState.SendMethod.MESSAGE -> {
                    binding.edttxtTitle.visibility = View.GONE
                }
                else -> {
                    binding.edttxtTitle.visibility = View.GONE
                }
            }
            viewModel.viewState.sendMethod = SendMessageViewState.SendMethod.valueOf(
                sendMethod.toUpperCase(Locale.ROOT)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateUI(viewState: SendMessageViewState) {
        // Update the UI
        binding.edttxtTitle.visibility =
            if (viewState.sendMethod == SendMessageViewState.SendMethod.EMAIL) {
                View.VISIBLE
            } else {
                View.GONE
            }
        binding.edttxtTitle.setText(viewState.title)
        binding.mltxtMessage.setText(viewState.body)
        if (viewState.isUserAdmin && binding.spnrContacts.adapter != null) {
            val user = viewState.dest
            val dataAdapter: ArrayAdapter<User> =
                binding.spnrContacts.adapter as ArrayAdapter<User>
            val spinnerPosition: Int = dataAdapter.getPosition(user)
            binding.spnrContacts.setSelection(spinnerPosition)
        } else {
            binding.spnrContacts.setSelection(-1)
        }
        if (viewState.isUserAdmin) {
            for (view in binding.rdGrpSendMethod.children) {
                val radioButton = view as RadioButton
                radioButton.isChecked = (radioButton.tag == viewState.sendMethod.toString())
            }
        } else {
            for (view in binding.rdGrpSendMethod.children) {
                val radioButton = view as RadioButton
                if (radioButton.text == SendMessageViewState.SendMethod.MESSAGE.toString()) {
                    radioButton.isChecked = true
                    break
                }
            }
        }
        if (!viewState.isUserAdmin) {
            binding.spnrContacts.visibility = View.GONE
            binding.rdGrpSendMethod.visibility = View.GONE
        } else {
            if (binding.spnrContacts.adapter != null && !binding.spnrContacts.adapter.isEmpty)
                binding.spnrContacts.visibility = View.VISIBLE
            else {
                binding.spnrContacts.visibility = View.GONE
            }
            binding.rdGrpSendMethod.visibility = View.VISIBLE
        }
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.GONE
    }

    @Suppress("UNCHECKED_CAST")
    private fun setSelectedMessageSender() {
        if (mSavedInstanceState != null && (mSavedInstanceState?.getString(ACTION_REPLY_KEY) == ACTION_REPLY || mSavedInstanceState?.getString(
                ACTION_SEND_KEY
            ) == ACTION_SEND)
        ) {
            val message = mSavedInstanceState?.getParcelable<Message>(KEY_MESSAGE)
            if (message != null) {
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
        if (mSavedInstanceState != null) {
            if (mSavedInstanceState?.getString(ACTION_REPLY_KEY) == ACTION_SEND) {
                setSelectedMessageSender()
            } else if (mSavedInstanceState?.getString(ACTION_REPLY_KEY) == ACTION_REPLY) {
                val message = mSavedInstanceState?.getParcelable<Message>(KEY_MESSAGE)
                if (message != null) {
                    viewModel.viewState.replyUid = message.uid.toString()
                    viewModel.viewState.body = message.message.toString()
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

    private class Callback(var fragment: SendMessageFragment) :
        OnDataChangeListener<DocumentReference> {
        override fun onDataChanged(item: DocumentReference) {
            fragment.binding.btnSend.isEnabled = true
            Toast.makeText(
                fragment.requireContext(),
                "Message sent successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onUsersLoaded(users: List<User>) {
        if(isSafeToAccessViewModel()&&!isStopped) {
            val mutableUsers = ArrayList<User>(users)
            for (user in users) {
                if (user.email == LoggedUser.userLiveData.value?.email) {
                    mutableUsers.remove(user)
                }
            }
            val dataAdapter: ArrayAdapter<User> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, mutableUsers
            )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnrContacts.adapter = dataAdapter
            binding.spnrContacts.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        pos: Int,
                        id: Long
                    ) {
                        viewModel.viewState.dest =
                            (binding.spnrContacts.adapter.getItem(pos) as User)
                    }

                    override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                        viewModel.viewState.dest = User()
                    }
                }
            if (binding.spnrContacts.adapter.isEmpty) {
                binding.spnrContacts.visibility = View.GONE
                for (radio in binding.rdGrpSendMethod.children) {
                    if (radio.tag == SendMessageViewState.SendMethod.EMAIL.toString()) {
                        radio.isEnabled = false
                    } else if (radio.tag == SendMessageViewState.SendMethod.MESSAGE.toString()) {
                        viewModel.viewState.sendMethod = SendMessageViewState.SendMethod.MESSAGE
                        (radio as RadioButton).isChecked = true
                    }
                }
            } else {
                setSelectedMessageSender()
            }
        }
    }
}