package io.github.diegoflassa.littledropsofrain.ui.subscription

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.chip.Chip
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.entities.SubscriptionMessage
import io.github.diegoflassa.littledropsofrain.databinding.FragmentSendSubsctiptionMessageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet


class SendSubscriptionMessageFragment : Fragment() {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    companion object {
        var TAG = SendSubscriptionMessageFragment::class.simpleName
        fun newInstance() = SendSubscriptionMessageFragment()
    }

    private val viewModel: SendSubsctiptionMessageViewModel by viewModels()
    private lateinit var binding : FragmentSendSubsctiptionMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendSubsctiptionMessageBinding.inflate(inflater, container, false)
        for(topic in SubscriptionMessage.Topic.values()) {
            if(topic != SubscriptionMessage.Topic.UNKNOWN) {
                val chip = Chip(requireContext())
                chip.text = topic.toString()
                chip.isCheckable = true
                binding.cpGrpTopics.addView(chip)
            }
        }
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Subscription Message"
        binding.btnSend.setOnClickListener {
            sendMessage(getSelectedTopics(), binding.edtTxtTitle.text.toString(), binding.edtTxtMlMessage.text.toString())
        }
        return binding.root
    }

    private fun getSelectedTopics(): Set<SubscriptionMessage.Topic>{
        val ret= HashSet<SubscriptionMessage.Topic>()
        val chipIds = binding.cpGrpTopics.checkedChipIds
        for(chipId in chipIds){
            val chip = binding.cpGrpTopics.findViewById<Chip>(chipId)
            ret.add(SubscriptionMessage.Topic.valueOf(chip.text.toString().toUpperCase()))
        }
        return ret
    }

    private fun sendMessage(topics: Set<SubscriptionMessage.Topic>, title :String, messageContent:String) {
        ioScope.launch {
            for (topic in topics) {
                val url = "https://content-fcm.googleapis.com/v1/projects/littledropsofrain-72ae8/messages:send"
                val myReq: StringRequest = object : StringRequest(
                    Method.POST, url,
                    Response.Listener {
                        Toast.makeText(
                            activity,
                            "Message sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    Response.ErrorListener {
                        Toast.makeText(activity, "Error sending message", Toast.LENGTH_SHORT).show()
                    }) {

                    @Throws(AuthFailureError::class)
                    override fun getBody(): ByteArray {
                        val rawParameters: MutableMap<Any?, Any?> = Hashtable()
                        val parameters: MutableMap<Any?, Any?> = Hashtable()
                        val message: MutableMap<Any?, Any?> = Hashtable()
                        message["title"] = title
                        message["body"] = messageContent
                        parameters["topic"] = topic.toString()
                        parameters["notification"] = message
                        rawParameters["message"] = parameters
                        return JSONObject(rawParameters).toString().toByteArray()
                    }

                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }

                    var YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE = getAccessToken()?.tokenValue

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
            }
        }
    }

    private fun getAccessToken() : AccessToken? {
        val resource = resources.openRawResource(R.raw.littledropsofrain_72ae8_firebase_adminsdk_jsxhc_7bdfe4ab91)
        val scopes : MutableList<String> = ArrayList<String>()
        scopes.add("https://www.googleapis.com/auth/firebase.messaging")
        val credential = GoogleCredentials.fromStream(resource).createScoped(scopes)
        val accessToken= credential.refreshAccessToken()
        Log.d(TAG, "Got access token : ${accessToken.tokenValue}")
        return accessToken
    }

}