package app.web.diegoflassa_site.littledropsofrain.ui.facebook

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentFacebookBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.isSafeToAccessViewModel
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnKeyLongPressListener
import app.web.diegoflassa_site.littledropsofrain.models.FacebookViewModel
import app.web.diegoflassa_site.littledropsofrain.models.FacebookViewState
import com.google.android.material.bottomnavigation.BottomNavigationView


class FacebookFragment : Fragment(), OnKeyLongPressListener {

    companion object{
        fun newInstance() = FacebookFragment()
        private const val KEY_PREF_LAST_URL = "KEY_PREF_LAST_URL_FACEBOOK"
    }

    private var isStopped: Boolean = false
    private val viewModel: FacebookViewModel by viewModels()
    private var binding :FragmentFacebookBinding by viewLifecycle()
    private var url = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFacebookBinding.inflate(inflater, container, false)
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })

        url = getString(R.string.url_facebook)
        // set up the webview
        binding.webviewFacebook.settings.javaScriptEnabled = true
        binding.webviewFacebook.settings.domStorageEnabled = true
        binding.webviewFacebook.webViewClient = object: WebViewClient() {

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
                if(binding.webviewFacebook.canGoBack()){
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
        binding.webviewFacebook.loadUrl(url)
        saveCurrentUrl()
        return binding.root
    }

    override fun onSaveInstanceState(outState : Bundle){
        super.onSaveInstanceState(outState)
        if (isSafeToAccessViewModel() && !isStopped) {
            binding.webviewFacebook.saveState(outState)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState!=null) {
            binding.webviewFacebook.restoreState(savedInstanceState)
        }
    }

    override fun onStop(){
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
        updateUI(viewModel.viewState)
    }

    private fun updateUI(viewState: FacebookViewState) {
        // Update the UI
        viewState.text = ""
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.GONE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
    }

    private fun showProgressDialog() {
        binding.facebookProgress.visibility = View.VISIBLE
    }

    fun hideProgressDialog(){
        if (isSafeToAccessViewModel() && !isStopped) {
            binding.facebookProgress.visibility = View.GONE
        }
    }

    override fun keyLongPress(keyCode: Int, event: KeyEvent?) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            binding.webviewFacebook.reload()
        }
    }

    private fun saveCurrentUrl(){
        val prefs = requireContext().applicationContext.getSharedPreferences(
            requireContext().packageName,
            Activity.MODE_PRIVATE
        )
        val edit: SharedPreferences.Editor = prefs.edit()
        edit.putString(KEY_PREF_LAST_URL, binding.webviewFacebook.url)
        edit.apply()
    }
    
    private fun restoreCurrentUrl(){
        val prefs = requireContext().applicationContext.getSharedPreferences(
            requireContext().packageName,
            Activity.MODE_PRIVATE
        )
        val url = prefs.getString(KEY_PREF_LAST_URL, getString(R.string.url_facebook))
        if (!url.isNullOrEmpty()) {
            binding.webviewFacebook.loadUrl(url)
        }
    }
}

