package app.web.diegoflassa_site.littledropsofrain.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.contracts.AuthActivityResultContract
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentAuthenticationProxyBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import com.firebase.ui.auth.AuthUI
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 * Use the [AuthenticationProxyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AuthenticationProxyFragment : Fragment(), ActivityResultCallback<Int> {

    var binding: FragmentAuthenticationProxyBinding by viewLifecycle()
    private lateinit var mAuth: FirebaseAuth

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AutheticationProxyFragment.
         */
        @JvmStatic
        fun newInstance() = AuthenticationProxyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAuthenticationProxyBinding.inflate(inflater, container, false)
        binding.authenticationProgress.visibility = View.VISIBLE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser==null) {
            registerForActivityResult(AuthActivityResultContract(), this).launch(null)
        }else {
            logout()
            findNavController().navigate(R.id.nav_home)
        }
        return binding.root
    }

    private fun logout() {
        AuthUI.getInstance().signOut(requireContext())
    }

    override fun onActivityResult(result: Int) {
        if (result == AppCompatActivity.RESULT_OK) {
            // Successfully signed in
            Toast.makeText(requireContext(), getString(R.string.log_in_successful), Toast.LENGTH_LONG).show()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(requireContext(), getString(R.string.unable_to_log_in), Toast.LENGTH_LONG).show()
        }
        findNavController().navigate(R.id.nav_home)
    }
}