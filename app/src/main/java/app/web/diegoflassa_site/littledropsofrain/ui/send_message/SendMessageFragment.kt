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

package app.web.diegoflassa_site.littledropsofrain.ui.send_message

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.navigation.findNavController
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
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
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.util.*
import kotlin.collections.ArrayList

class SendMessageFragment :
    Fragment(),
    OnUsersLoadedListener {

    companion object {
        fun newInstance() = SendMessageFragment()
        private val TAG = SendMessageFragment::class.simpleName
        const val ACTION_SEND_KEY = "ACTION_SEND"
        const val ACTION_SEND = "app.web.diegoflassa_site.littledropsofrain.action.ACTION_SEND"
        const val ACTION_REPLY_KEY = "ACTION_REPLY"
        const val ACTION_REPLY = "app.web.diegoflassa_site.littledropsofrain.action.ACTION_REPLY"
        const val KEY_MESSAGE = "message"
        var mSavedInstanceState: Bundle? = null
    }

    val viewModel: SendMessageViewModel by stateViewModel()
    private var binding: FragmentSendMessageBinding by viewLifecycle()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var toggle: ActionBarDrawerToggle
    private var isStopped = false

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendMessageBinding.inflate(layoutInflater)
        viewModel.viewStateLiveData.observe(
            viewLifecycleOwner,
            {
                updateUI(viewModel.viewState)
            }
        )
        binding.btnSend.setOnClickListener {
            val callback = Callback(this)
            // Coroutine has multiple dispatchers suited for different type of workloads
            ioScope.launch {
                when (viewModel.viewState.sendMethod) {
                    SendMessageViewState.SendMethod.MESSAGE -> {
                        val message = Message()
                        message.replyUid = viewModel.viewState.replyUid
                        message.owners.add(viewModel.viewState.sender.email.toString())
                        message.replyUid = viewModel.viewState.replyUid
                        message.message = binding.mltxtMessage.text.toString()
                        if (!viewModel.viewState.dest.email.isNullOrEmpty()) {
                            message.emailTo = viewModel.viewState.dest.email.toString()
                            if (viewModel.viewState.isUserAdmin) {
                                message.owners.add(viewModel.viewState.dest.email.toString())
                            }
                        }
                        message.type = MessageType.MESSAGE.toString()
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
        binding.mltxtMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { /*Unused*/
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) { /*Unused*/
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnSend.isEnabled = binding.mltxtMessage.text.toString().trim().isNotEmpty()
            }
        })
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
        isStopped = true
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        isStopped = false
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
        if (isSafeToAccessViewModel() && !isStopped) {
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

    @ExperimentalStdlibApi
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
            when (SendMessageViewState.SendMethod.valueOf(sendMethod.uppercase(Locale.ROOT))) {
                SendMessageViewState.SendMethod.EMAIL -> {
                    binding.spnrContacts.visibility = View.VISIBLE
                    binding.edttxtTitle.visibility = View.VISIBLE
                }
                else -> {
                    binding.spnrContacts.visibility = View.GONE
                    binding.edttxtTitle.visibility = View.GONE
                }
            }
            viewModel.viewState.sendMethod = SendMessageViewState.SendMethod.valueOf(
                sendMethod.uppercase(Locale.ROOT)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateUI(viewState: SendMessageViewState) {
        // Update the UI
        if (viewState.sendMethod == SendMessageViewState.SendMethod.EMAIL) {
            binding.spnrContacts.visibility = View.VISIBLE
            binding.edttxtTitle.visibility = View.VISIBLE
        } else {
            binding.spnrContacts.visibility = View.GONE
            binding.edttxtTitle.visibility = View.GONE
        }
        binding.edttxtTitle.setText(viewState.title)
        binding.mltxtMessage.setText(viewState.body)
        updateUIForUserAdmin(viewState)
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.GONE
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateUIForUserAdmin(viewState: SendMessageViewState) {
        if (viewState.isUserAdmin && binding.spnrContacts.adapter != null) {
            val user = viewState.dest
            val dataAdapter: ArrayAdapter<User> =
                binding.spnrContacts.adapter as ArrayAdapter<User>
            val spinnerPosition: Int = dataAdapter.getPosition(user)
            binding.spnrContacts.setSelection(spinnerPosition)
            binding.spnrContacts.visibility = View.VISIBLE
        } else {
            binding.spnrContacts.setSelection(-1)
            binding.spnrContacts.visibility = View.GONE
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
        updateRadioGroupUserAdmin(viewState)
    }

    private fun updateRadioGroupUserAdmin(viewState: SendMessageViewState) {
        if (!viewState.isUserAdmin) {
            binding.rdGrpSendMethod.visibility = View.GONE
        } else {
            binding.rdGrpSendMethod.visibility = View.VISIBLE
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setSelectedMessageSender() {
        if (mSavedInstanceState != null && (
                    mSavedInstanceState?.getString(ACTION_REPLY_KEY) == ACTION_REPLY || mSavedInstanceState?.getString(
                        ACTION_SEND_KEY
                    ) == ACTION_SEND
                    )
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
        if (isSafeToAccessViewModel() && !isStopped) {
            val usersWithDefault = ArrayList<User>(users.size + 1)
            val user = User()
            user.name = getString(R.string.no_selection)
            user.email = getString(R.string.none)
            usersWithDefault.add(user)
            usersWithDefault.addAll(users)
            for (localUser in users) {
                if (localUser.email == LoggedUser.userLiveData.value?.email) {
                    addOnItemSelectedForSpinnerContacts()
                    checkIfSpinnerContactsIsEmpty()
                }
            }
        }
    }

    private fun checkIfSpinnerContactsIsEmpty() {
        if (binding.spnrContacts.adapter.isEmpty) {
            binding.spnrContacts.visibility = View.GONE
            for (radio in binding.rdGrpSendMethod.children) {
                if (radio.tag == SendMessageViewState.SendMethod.EMAIL.toString()) {
                    radio.isEnabled = false
                } else if (radio.tag == SendMessageViewState.SendMethod.MESSAGE.toString()) {
                    viewModel.viewState.sendMethod =
                        SendMessageViewState.SendMethod.MESSAGE
                    (radio as RadioButton).isChecked = true
                }
            }
        } else {
            setSelectedMessageSender()
        }
    }

    private fun addOnItemSelectedForSpinnerContacts() {
        binding.spnrContacts.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    viewModel.viewState.dest =
                        binding.spnrContacts.adapter.getItem(pos) as User
                }

                override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                    viewModel.viewState.dest = User()
                }
            }
    }
}