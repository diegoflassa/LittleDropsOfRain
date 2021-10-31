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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.instagram

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
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentInstagramBinding
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.isSafeToAccessViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.MainActivity
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.presentation.interfaces.OnKeyLongPressListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import java.io.IOException

@ExperimentalStdlibApi
class InstagramFragment : Fragment(), OnKeyLongPressListener {

    companion object {
        fun newInstance() = InstagramFragment()
        private const val PREFERENCES_NAME = "instagram_preferences"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = PREFERENCES_NAME,
        )
    }

    private var isStopped: Boolean = false
    private val keyPrefsLastURL = stringPreferencesKey("KEY_PREF_LAST_URL_INSTAGRAM")

    val viewModel: InstagramViewModel by viewModels()
    private var binding: FragmentInstagramBinding by viewLifecycle()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstagramBinding.inflate(inflater, container, false)

        // set up the webview
        binding.webviewInstagram.settings.javaScriptEnabled = true
        binding.webviewInstagram.settings.domStorageEnabled = true
        binding.webviewInstagram.webViewClient = object : WebViewClient() {

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
                if (binding.webviewInstagram.canGoBack()) {
                    binding.webviewInstagram.goBack()
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
        binding.webviewInstagram.loadUrl(viewModel.url)
        runBlocking {
            saveCurrentUrl()
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isSafeToAccessViewModel() && !isStopped) {
            binding.webviewInstagram.saveState(outState)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            binding.webviewInstagram.restoreState(savedInstanceState)
        }
    }

    override fun onStop() {
        isStopped = true
        (activity as MainActivity).mOnKeyLongPressListener = null
        binding.webviewInstagram.stopLoading()
        super.onStop()
    }

    @SuppressLint("ApplySharedPref")
    override fun onPause() {
        binding.webviewInstagram.pauseTimers()
        runBlocking {
            saveCurrentUrl()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        isStopped = false
        binding.webviewInstagram.resumeTimers()
        restoreCurrentUrl()
        updateUI()
    }

    private fun updateUI() {
        // Update the UI
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.GONE
    }

    private fun showProgressDialog() {
        binding.instagramProgress.visibility = View.VISIBLE
    }

    fun hideProgressDialog() {
        if (isSafeToAccessViewModel() && !isStopped) {
            binding.instagramProgress.visibility = View.GONE
        }
    }

    override fun keyLongPress(keyCode: Int, event: KeyEvent?) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            binding.webviewInstagram.reload()
        }
    }

    private fun saveCurrentUrl() {
        if (isSafeToAccessViewModel()) {
            CoroutineScope(Dispatchers.Main).launch {
                requireContext().dataStore.edit { settings ->
                    settings[keyPrefsLastURL] = binding.webviewInstagram.url.toString()
                }
            }
        }
    }

    private fun restoreCurrentUrl() {
        requireContext().dataStore.data
            .catch { exception ->
                // dataStore.data throws an IOException when an error is encountered when reading data
                if (exception is IOException) {
                    val url = getString(R.string.url_instagram)
                    binding.webviewInstagram.loadUrl(url)
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                // Get our show url value, defaulting to the resource if not set:
                val url = preferences[keyPrefsLastURL] ?: getString(R.string.url_instagram)
                binding.webviewInstagram.loadUrl(url)
            }
    }
}
