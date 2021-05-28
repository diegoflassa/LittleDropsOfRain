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

package app.web.diegoflassa_site.littledropsofrain.ui.instagram

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
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentInstagramBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.isSafeToAccessViewModel
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnKeyLongPressListener
import app.web.diegoflassa_site.littledropsofrain.models.InstagramViewModel
import app.web.diegoflassa_site.littledropsofrain.models.InstagramViewState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.io.IOException

class InstagramFragment : Fragment(), OnKeyLongPressListener {

    companion object {
        fun newInstance() = InstagramFragment()
    }

    private var isStopped: Boolean = false
    private val keyPrefsLastURL = stringPreferencesKey("KEY_PREF_LAST_URL_INSTAGRAM")
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

    val viewModel: InstagramViewModel by stateViewModel()
    private var binding: FragmentInstagramBinding by viewLifecycle()
    private var url = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstagramBinding.inflate(inflater, container, false)
        viewModel.viewStateLiveData.observe(
            viewLifecycleOwner
        ) {
            updateUI(it)
        }

        url = getString(R.string.url_instagram)
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
        binding.webviewInstagram.loadUrl(url)
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
        updateUI(viewModel.viewState)
    }

    private fun updateUI(viewState: InstagramViewState) {
        // Update the UI
        viewState.text = ""
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
        ioScope.launch {
            requireContext().dataStore.edit { settings ->
                settings[keyPrefsLastURL] = binding.webviewInstagram.url.toString()
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
