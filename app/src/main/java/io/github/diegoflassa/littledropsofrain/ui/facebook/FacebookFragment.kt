package io.github.diegoflassa.littledropsofrain.ui.facebook

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
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.databinding.FragmentFacebookBinding
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.models.FacebookViewModel
import io.github.diegoflassa.littledropsofrain.models.FacebookViewState


class FacebookFragment : Fragment() {

    companion object{
        fun newInstance() = FacebookFragment()
    }
    private val facebookViewModel: FacebookViewModel by viewModels()
    private var binding :FragmentFacebookBinding by viewLifecycle()
    private var facebookUrl = "https://www.facebook.com/m.andrea.littledrops/"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFacebookBinding.inflate(inflater, container, false)
        facebookViewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })

        // set up the webview
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
        binding.webviewFacebook.loadUrl(facebookUrl)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI(facebookViewModel.viewState)
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