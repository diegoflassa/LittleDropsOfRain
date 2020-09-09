package app.web.diegoflassa_site.littledropsofrain.ui.instagram

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentInstagramBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.InstagramViewModel
import app.web.diegoflassa_site.littledropsofrain.models.InstagramViewState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class InstagramFragment : Fragment() {

    companion object{
        fun newInstance() = InstagramFragment()
        private const val KEY_PREF_LAST_URL = "KEY_PREF_LAST_URL_INSTAGRAM"
    }

    private var isStopped: Boolean = false
    private val viewModel: InstagramViewModel by viewModels()
    private var binding : FragmentInstagramBinding by viewLifecycle()
    private var url = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstagramBinding.inflate(inflater, container, false)
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })

        url = getString(R.string.url_instagram)
        // set up the webview
        binding.webviewInstagram.settings.javaScriptEnabled = true
        binding.webviewInstagram.settings.domStorageEnabled = true
        binding.webviewInstagram.webViewClient = object: WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
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
                if(binding.webviewInstagram.canGoBack()){
                    binding.webviewInstagram.goBack()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        showProgressDialog()
        binding.webviewInstagram.loadUrl(url)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        binding.webviewInstagram.saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState!=null) {
            binding.webviewInstagram.restoreState(savedInstanceState)
        }
    }

    override fun onStop(){
        super.onStop()
        isStopped = true
        binding.webviewInstagram.stopLoading()
    }

    @SuppressLint("ApplySharedPref")
    override fun onPause() {
        super.onPause()
        binding.webviewInstagram.pauseTimers()
        val prefs = requireContext().applicationContext.getSharedPreferences(
            requireContext().packageName,
            Activity.MODE_PRIVATE
        )
        val edit: SharedPreferences.Editor = prefs.edit()
        edit.putString(KEY_PREF_LAST_URL, binding.webviewInstagram.url)
        edit.commit()
    }

    override fun onResume() {
        super.onResume()
        binding.webviewInstagram.resumeTimers()
        val prefs = requireContext().applicationContext.getSharedPreferences(
            requireContext().packageName,
            Activity.MODE_PRIVATE
        )
        val url = prefs.getString(KEY_PREF_LAST_URL, "")
        if (!url.isNullOrEmpty()) {
            binding.webviewInstagram.loadUrl(url)
        }
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

    fun hideProgressDialog(){
        if(!isStopped) {
            binding.instagramProgress.visibility = View.GONE
        }
    }
}