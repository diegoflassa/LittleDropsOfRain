package app.web.diegoflassa_site.littledropsofrain.ui.topic

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.entities.TopicMessage
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentSendTopicMessageBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.TopicMessageViewModel
import app.web.diegoflassa_site.littledropsofrain.models.TopicMessageViewState
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet


class SendTopicMessageFragment : Fragment() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var toggle : ActionBarDrawerToggle

    companion object {
        var TAG = SendTopicMessageFragment::class.simpleName
        fun newInstance() = SendTopicMessageFragment()
    }

    private val viewModel: TopicMessageViewModel by viewModels()
    private var binding : FragmentSendTopicMessageBinding by viewLifecycle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendTopicMessageBinding.inflate(inflater, container, false)
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })
        for(topic in TopicMessage.Topic.values()) {
            if(topic != TopicMessage.Topic.UNKNOWN) {
                val chip = Chip(requireContext())
                chip.text = topic.toString()
                chip.isCheckable = true
                chip.setOnCheckedChangeListener { compoundButton: CompoundButton, state: Boolean ->
                    if (state) {
                        viewModel.viewState.topics.add(
                            TopicMessage.Topic.valueOf(
                                compoundButton.text.toString().toUpperCase(Locale.ROOT)
                            )
                        )
                    } else {
                        viewModel.viewState.topics.remove(
                            TopicMessage.Topic.valueOf(
                                compoundButton.text.toString().toUpperCase(Locale.ROOT)
                            )
                        )
                    }
                }
                binding.cpGrpTopics.addView(chip)
            }
        }
        binding.fabSendTopicMessage.setOnClickListener {
            if(getSelectedTopics().isNotEmpty()) {
                sendMessage(
                    getSelectedTopics(),
                    binding.edtTxtTitle.text.toString(),
                    binding.edtTxtMlMessage.text.toString()
                )
            }else{
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_a_topic),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.fabPreviewTopicMessage.setOnClickListener {
            Helper.showNotification(requireContext(), binding.edtTxtTitle.text.toString(), binding.edtTxtMlMessage.text.toString())
        }
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
            if(drawerLayout!=null)
                toggle.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
        return binding.root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        if(this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
    }

    override fun onSaveInstanceState(outState : Bundle){
        super.onSaveInstanceState(outState)
        viewModel.viewState.title = binding.edtTxtTitle.text.toString()
        viewModel.viewState.body = binding.edtTxtMlMessage.text.toString()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateUI(viewModel.viewState)
    }

    private fun updateUI(viewState: TopicMessageViewState?){
        // Update the UI
        for(chip in binding.cpGrpTopics.children) {
            if(viewState?.topics?.contains(
                    TopicMessage.Topic.valueOf((chip as Chip).text.toString().toUpperCase(Locale.ROOT))
                )!!) {
                (chip as Chip).isChecked = true
            }
        }
        binding.edtTxtTitle.setText(viewModel.viewState.title)
        binding.edtTxtMlMessage.setText(viewModel.viewState.body)
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.VISIBLE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
    }

    private fun getSelectedTopics(): Set<TopicMessage.Topic>{
        val ret= HashSet<TopicMessage.Topic>()
        val chipIds = binding.cpGrpTopics.checkedChipIds
        for(chipId in chipIds){
            val chip = binding.cpGrpTopics.findViewById<Chip>(chipId)
            ret.add(
                TopicMessage.Topic.valueOf(
                    chip.text.toString().toUpperCase(Locale.ROOT)
                )
            )
        }
        return ret
    }

    private fun sendMessage(topics: Set<TopicMessage.Topic>, title: String, messageContent: String) {
        ioScope.launch {
            if(topics.isNotEmpty()) {
                activity?.runOnUiThread {
                    binding.fabSendTopicMessage.isEnabled = false
                }
                val tokenValue = getAccessToken()?.tokenValue
                Log.d(TAG, "Got access token : $tokenValue")
                var condition = ""
                for (topic in topics) {
                    if(condition.isNotEmpty())
                        condition+= " || "
                    condition+= "'${topic.toString().toLowerCase(Locale.ROOT)}' in topics"
                }
                val url = getString(R.string.url_push_message)
                val myReq: StringRequest = object : StringRequest(
                    Method.POST, url,
                    Response.Listener {
                        Toast.makeText(
                            requireContext(),
                            "Message sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        activity?.runOnUiThread {
                            binding.fabSendTopicMessage.isEnabled = true
                        }
                    },
                    Response.ErrorListener {
                        var body = ""

                        //get status code here
                        val statusCode = it.networkResponse.statusCode.toString()

                        //get response body and parse with appropriate encoding
                        if (it.networkResponse.data != null) {
                            body = String(it.networkResponse.data, Charset.defaultCharset())
                        }
                        Toast.makeText(
                            requireContext(),
                            "Error sending message: $statusCode - $body",
                            Toast.LENGTH_LONG
                        ).show()
                        activity?.runOnUiThread {
                            binding.fabSendTopicMessage.isEnabled = true
                        }

                    }) {

                    @Throws(AuthFailureError::class)
                    override fun getBody(): ByteArray {
                        val rawParameters: MutableMap<Any?, Any?> = Hashtable()
                        val parameters: MutableMap<Any?, Any?> = Hashtable()
                        val message: MutableMap<Any?, Any?> = Hashtable()
                        message["title"] = title
                        message["body"] = messageContent
                        parameters["condition"] = condition
                        parameters["data"] = message
                        rawParameters["message"] = parameters
                        return JSONObject(rawParameters).toString().toByteArray()
                    }

                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }

                    var YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE = tokenValue

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers =
                            HashMap<String, String>()
                        headers["Authorization"] =
                            "Bearer $YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE"
                        return headers
                    }
                }
                Volley.newRequestQueue(activity).add(myReq)

            }else{
                Log.d(TAG, "No topic selected!")
            }
        }
    }

    private fun getAccessToken() : AccessToken? {
        val resource = resources.openRawResource(R.raw.littledropsofrain_site_firebase_adminsdk_9dvd0_c718bc2981)
        val scopes : MutableList<String> = ArrayList<String>()
        scopes.add("https://www.googleapis.com/auth/firebase.messaging")
        val credential = GoogleCredentials.fromStream(resource).createScoped(scopes)
        return credential.refreshAccessToken()
    }

}