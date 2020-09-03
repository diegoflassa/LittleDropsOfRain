package app.web.diegoflassa_site.littledropsofrain.ui.facebook

import android.annotation.SuppressLint
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentFacebookBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.FacebookViewModel
import app.web.diegoflassa_site.littledropsofrain.models.FacebookViewState


class FacebookFragment : Fragment() {

    companion object{
        fun newInstance() = FacebookFragment()
    }
    private val viewModel: FacebookViewModel by viewModels()
    private var binding :FragmentFacebookBinding by viewLifecycle()
    private var url = "https://www.facebook.com/m.andrea.littledrops/"

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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        showProgressDialog()
        binding.webviewFacebook.loadUrl(url)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }
    private fun updateUI(viewState: FacebookViewState) {
        // Update the UI
        viewState.text = ""
    }

    private fun showProgressDialog() {
        binding.facebookProgress.visibility = View.VISIBLE
    }

    fun hideProgressDialog(){
        binding.facebookProgress.visibility = View.GONE
    }
}