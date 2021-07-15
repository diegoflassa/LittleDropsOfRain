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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.topic

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.FilesDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.TopicMessage
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedFailureListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedListener
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentSendTopicMessageBinding
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.isSafeToAccessViewModel
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.runOnUiThread
import app.web.diegoflassa_site.littledropsofrain.presentation.contracts.CropImageResultContract
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.topic.model.TopicMessageViewModel
import coil.load
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
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class SendTopicMessageFragment :
    Fragment(),
    OnFileUploadedListener,
    OnFileUploadedFailureListener,
    ActivityResultCallback<Uri?> {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var getContentLauncher: ActivityResultLauncher<String>
    private lateinit var cropImageLauncher: ActivityResultLauncher<Pair<Uri, Pair<Float, Float>>>
    private var isStopped = false
    private var messageSent = false

    companion object {
        var TAG = SendTopicMessageFragment::class.simpleName
        fun newInstance() = SendTopicMessageFragment()
    }

    val viewModel: TopicMessageViewModel by stateViewModel()
    private var binding: FragmentSendTopicMessageBinding by viewLifecycle()

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendTopicMessageBinding.inflate(inflater, container, false)
        viewModel.viewStateLiveData.observe(
            viewLifecycleOwner
        ) {
            updateUI(it)
        }
        getTopics()
        binding.imgVwNotificationImage.visibility = View.GONE
        cropImageLauncher = registerForActivityResult(CropImageResultContract(), this)
        getContentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it == null) {
                binding.imgVwNotificationImage.setImageDrawable(null)
                binding.imgVwNotificationImage.visibility = View.GONE
                binding.fabSelectImage.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        android.R.drawable.ic_menu_gallery,
                        activity?.theme
                    )
                )
            } else {
                val data = Pair(it, CropImageResultContract.ASPECT_RATIO_RECTANGLE)
                cropImageLauncher.launch(data)
            }
        }
        binding.fabSelectImage.setOnClickListener {
            if (binding.imgVwNotificationImage.drawable == null) {
                binding.fabSelectImage.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        android.R.drawable.ic_menu_close_clear_cancel,
                        activity?.theme
                    )
                )
                getContentLauncher.launch("image/*")
            } else {
                binding.fabSelectImage.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        android.R.drawable.ic_menu_gallery,
                        activity?.theme
                    )
                )
                binding.imgVwNotificationImage.visibility = View.GONE
                binding.imgVwNotificationImage.setImageDrawable(null)
                viewModel.viewState.imageUriFirestore = null
                if (viewModel.viewState.imageUriLocal != null) {
                    viewModel.viewState.imageUriLocal!!.toFile().delete()
                    viewModel.viewState.imageUriLocal = null
                }
            }
        }
        binding.fabSendTopicMessage.setOnClickListener {
            if (getSelectedTopics().isNotEmpty()) {
                sendMessage(
                    getSelectedTopics(),
                    viewModel.viewState.imageUriFirestore,
                    binding.edtTxtTitle.text.toString(),
                    binding.edtTxtMlMessage.text.toString()
                )
                messageSent = true
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_a_topic),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.fabPreviewTopicMessage.setOnClickListener {
            Helper.showNotification(
                requireContext(),
                viewModel.viewState.imageUriFirestore,
                binding.edtTxtTitle.text.toString(),
                binding.edtTxtMlMessage.text.toString()
            )
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
            if (drawerLayout != null)
                toggle.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
        Helper.requestReadExternalStoragePermission(requireActivity())

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        isStopped = false
        if (isSafeToAccessViewModel() && !isStopped) {
            viewModel.viewState.title = binding.edtTxtTitle.text.toString()
            viewModel.viewState.body = binding.edtTxtMlMessage.text.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        isStopped = false
    }

    override fun onStop() {
        if (!messageSent && viewModel.viewState.imageUriFirestore != null) {
            FilesDao.remove(viewModel.viewState.imageUriFirestore)
        }
        isStopped = true
        super.onStop()
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
            viewModel.viewState.title = binding.edtTxtTitle.text.toString()
            viewModel.viewState.body = binding.edtTxtMlMessage.text.toString()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateUI(viewModel.viewState)
    }

    private fun getTopics() {
        for (topic in TopicMessage.Topic.values()) {
            if (topic != TopicMessage.Topic.UNKNOWN) {
                val chip = Chip(requireContext())
                chip.text = topic.toTitle(requireContext())
                chip.isCheckable = true
                chip.setOnCheckedChangeListener { compoundButton: CompoundButton, state: Boolean ->
                    if (state) {
                        viewModel.viewState.topics.add(
                            TopicMessage.Topic.fromTitle(requireContext(), compoundButton.text.toString())
                        )
                    } else {
                        viewModel.viewState.topics.remove(
                            TopicMessage.Topic.fromTitle(requireContext(), compoundButton.text.toString())
                        )
                    }
                }
                binding.cpGrpTopics.addView(chip)
            }
        }
    }

    private fun updateUI(viewState: TopicMessageViewState?) {
        // Update the UI
        for (chip in binding.cpGrpTopics.children) {
            if (viewState?.topics?.contains(
                    TopicMessage.Topic.fromTitle(requireContext(), (chip as Chip).text.toString())
                )!!
            ) {
                (chip as Chip).isChecked = true
            }
        }
        binding.imgVwNotificationImage.load(viewModel.viewState.imageUriLocal) {
            placeholder(R.drawable.image_placeholder)
        }
        if (viewModel.viewState.imageUriLocal != null) {
            binding.imgVwNotificationImage.visibility = View.VISIBLE
        } else {
            binding.imgVwNotificationImage.visibility = View.GONE
        }
        binding.edtTxtTitle.setText(viewModel.viewState.title)
        binding.edtTxtMlMessage.setText(viewModel.viewState.body)
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.VISIBLE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
    }

    private fun getSelectedTopics(): Set<TopicMessage.Topic> {
        val ret = HashSet<TopicMessage.Topic>()
        val chipIds = binding.cpGrpTopics.checkedChipIds
        for (chipId in chipIds) {
            val chip = binding.cpGrpTopics.findViewById<Chip>(chipId)
            val topic = TopicMessage.Topic.fromTitle(requireContext(), chip.text.toString())
            ret.add(topic)
        }
        return ret
    }

    @ExperimentalStdlibApi
    private fun sendMessage(
        topics: Set<TopicMessage.Topic>,
        imageUri: Uri?,
        title: String,
        messageContent: String
    ) {
        ioScope.launch {
            if (topics.isNotEmpty()) {
                activity?.runOnUiThread {
                    binding.fabSendTopicMessage.isEnabled = false
                }
                val tokenValue = getAccessToken().tokenValue
                Log.d(TAG, "Got access token : $tokenValue")
                var condition = ""
                for (topic in topics) {
                    if (condition.isNotEmpty())
                        condition += " || "
                    condition += "'${topic.toString().lowercase(Locale.ROOT)}' in topics"
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

                        // get status code here
                        val statusCode = it.networkResponse.statusCode.toString()

                        // get response body and parse with appropriate encoding
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
                    }
                ) {

                    @Throws(AuthFailureError::class)
                    override fun getBody(): ByteArray {
                        val rawParameters: MutableMap<Any?, Any?> = Hashtable()
                        val parameters: MutableMap<Any?, Any?> = Hashtable()
                        val message: MutableMap<Any?, Any?> = Hashtable()
                        message["title"] = title
                        message["body"] = messageContent
                        message["imageUri"] = imageUri.toString()
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
            } else {
                Log.d(TAG, "No topic selected!")
            }
        }
    }

    private fun showLoadingScreen() {
        binding.fabPreviewTopicMessage.isEnabled = false
        binding.fabSendTopicMessage.isEnabled = false
        binding.topicMessageProgress.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        runOnUiThread {
            if (isSafeToAccessViewModel() && !isStopped) {
                binding.fabPreviewTopicMessage.isEnabled = true
                binding.fabSendTopicMessage.isEnabled = true
                binding.topicMessageProgress.visibility = View.GONE
            }
        }
    }

    private fun getAccessToken(): AccessToken {
        val resource =
            resources.openRawResource(R.raw.littledropsofrain_site_firebase_adminsdk_9dvd0_c718bc2981)
        val scopes: MutableList<String> = ArrayList()
        scopes.add("https://www.googleapis.com/auth/firebase.messaging")
        val credential = GoogleCredentials.fromStream(resource).createScoped(scopes)
        return credential.refreshAccessToken()
    }

    override fun onFileUploaded(local: Uri, remote: Uri) {
        FilesDao.remove(viewModel.viewState.imageUriFirestore)
        viewModel.viewState.imageUriFirestore = remote
        hideLoadingScreen()
        Toast.makeText(context, getString(R.string.file_upload_success), Toast.LENGTH_LONG).show()
    }

    override fun onFileUploadedFailure(file: Uri, exception: Exception?) {
        hideLoadingScreen()
        Toast.makeText(context, getString(R.string.file_upload_failure), Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(uri: Uri?) {
        binding.imgVwNotificationImage.visibility = View.VISIBLE
        showLoadingScreen()
        FilesDao.insert(uri!!, this, this)
        viewModel.viewState.imageUriLocal = uri
        binding.fabSelectImage.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                android.R.drawable.ic_menu_close_clear_cancel,
                activity?.theme
            )
        )
        binding.imgVwNotificationImage.load(viewModel.viewState.imageUriLocal) {
            placeholder(R.drawable.image_placeholder)
        }
    }
}
