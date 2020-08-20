package io.github.diegoflassa.littledropsofrain.ui.facebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.github.diegoflassa.littledropsofrain.databinding.FragmentFacebookBinding
import io.github.diegoflassa.littledropsofrain.models.FacebookViewModel
import viewLifecycle


class FacebookFragment : Fragment() {

    companion object{
        fun newInstance() = FacebookFragment()
    }
    private lateinit var facebookViewModel: FacebookViewModel
    private var binding :FragmentFacebookBinding by viewLifecycle()
    private var facebookUrl = "https://www.facebook.com/m.andrea.littledrops/"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFacebookBinding.inflate(inflater, container, false)
        facebookViewModel =
            ViewModelProvider.NewInstanceFactory().create(FacebookViewModel::class.java)

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
        showProgressDialog()
        binding.webviewFacebook.loadUrl( facebookUrl)
        return binding.root
    }

    private fun showProgressDialog() {
        binding.facebookProgress.visibility = View.VISIBLE
    }

    fun hideProgressDialog(){
        binding.facebookProgress.visibility = View.GONE
    }
}