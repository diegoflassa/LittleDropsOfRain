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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.facebook

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentFacebookBinding
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.isSafeToAccessViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.MainActivity
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.presentation.interfaces.OnKeyLongPressListener
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.facebook.model.FacebookViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.io.IOException

class FacebookFragment : Fragment(), OnKeyLongPressListener {

    companion object {
        fun newInstance() = FacebookFragment()
    }

    private var isStopped: Boolean = false
    private val keyPrefsLastURL = stringPreferencesKey("KEY_PREF_LAST_URL_FACEBOOK")
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = requireContext().packageName,
        produceMigrations = {
            listOf(
                SharedPreferencesMigration(
                    requireContext(),
                    requireContext().packageName
                )
            )
        }
    )

    val viewModel: FacebookViewModel by stateViewModel()
    private var binding: FragmentFacebookBinding by viewLifecycle()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacebookBinding.inflate(inflater, container, false)
        viewModel.urlLiveData.observe(
            viewLifecycleOwner
        ) {
            updateUI(viewModel)
        }

        // set up the webview
        binding.webviewFacebook.settings.javaScriptEnabled = true
        binding.webviewFacebook.settings.domStorageEnabled = true
        binding.webviewFacebook.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideProgressDialog()
                super.onPageFinished(view, url)
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webviewFacebook.canGoBack()) {
                    binding.webviewFacebook.goBack()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        (activity as MainActivity).mOnKeyLongPressListener = this
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        showProgressDialog()
        binding.webviewFacebook.loadUrl(viewModel.url)
        saveCurrentUrl()
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isSafeToAccessViewModel() && !isStopped) {
            binding.webviewFacebook.saveState(outState)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            binding.webviewFacebook.restoreState(savedInstanceState)
        }
    }

    override fun onStop() {
        super.onStop()
        isStopped = true
        (activity as MainActivity).mOnKeyLongPressListener = null
        binding.webviewFacebook.stopLoading()
    }

    @SuppressLint("ApplySharedPref")
    override fun onPause() {
        binding.webviewFacebook.pauseTimers()
        saveCurrentUrl()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        isStopped = false
        binding.webviewFacebook.resumeTimers()
        restoreCurrentUrl()
        updateUI(viewModel)
    }

    private fun updateUI(viewState: FacebookViewModel) {
        // Update the UI
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.GONE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
    }

    private fun showProgressDialog() {
        binding.facebookProgress.visibility = View.VISIBLE
    }

    fun hideProgressDialog() {
        if (isSafeToAccessViewModel() && !isStopped) {
            binding.facebookProgress.visibility = View.GONE
        }
    }

    override fun keyLongPress(keyCode: Int, event: KeyEvent?) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            binding.webviewFacebook.reload()
        }
    }

    private fun saveCurrentUrl() {
        ioScope.launch {
            requireContext().dataStore.edit { settings ->
                settings[keyPrefsLastURL] = binding.webviewFacebook.url.toString()
            }
        }
    }

    private fun restoreCurrentUrl() {
        requireContext().dataStore.data
            .catch { exception ->
                // dataStore.data throws an IOException when an error is encountered when reading data
                if (exception is IOException) {
                    val url = getString(R.string.url_facebook)
                    binding.webviewFacebook.loadUrl(url)
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                // Get our show url value, defaulting to the resource if not set:
                val url = preferences[keyPrefsLastURL] ?: getString(R.string.url_facebook)
                binding.webviewFacebook.loadUrl(url)
            }
    }
}
