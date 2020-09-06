package app.web.diegoflassa_site.littledropsofrain.ui.instagram

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
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentInstagramBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.InstagramViewModel
import app.web.diegoflassa_site.littledropsofrain.models.InstagramViewState


class InstagramFragment : Fragment() {

    companion object{
        fun newInstance() = InstagramFragment()
    }

    private var isStopped: Boolean = false
    private val viewModel: InstagramViewModel by viewModels()
    private var binding : FragmentInstagramBinding by viewLifecycle()
    private var url = "https://www.instagram.com/little_drops_of_rain"

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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        showProgressDialog()
        binding.webviewInstagram.loadUrl(url)
        return binding.root
    }

    override fun onStop(){
        super.onStop()
        isStopped = true
        binding.webviewInstagram.stopLoading()
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }
    private fun updateUI(viewState: InstagramViewState) {
        // Update the UI
        viewState.text = ""
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