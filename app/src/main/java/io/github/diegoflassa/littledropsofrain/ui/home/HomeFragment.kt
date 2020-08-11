package io.github.diegoflassa.littledropsofrain.ui.home

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.adapters.ProductListAdapter
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.models.ProductViewModel
import io.github.diegoflassa.littledropsofrain.models.ProductViewModelFactory
import io.github.diegoflassa.littledropsofrain.auth.AuthActivityResultContract
import io.github.diegoflassa.littledropsofrain.databinding.FragmentHomeBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), ActivityResultCallback<Int> {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var binding: FragmentHomeBinding

    companion object{
        const val TAG ="HomeFragment"
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel =
            ViewModelProvider.NewInstanceFactory().create(HomeViewModel::class.java)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textHome.text = it
        })
        binding.authButton.setOnClickListener {
            val mAuth = FirebaseAuth.getInstance()
            if(mAuth.currentUser==null){
                // Create and launch sign-in intent
                registerForActivityResult(AuthActivityResultContract(), this ).launch(null)
            }else{
                GlobalScope.launch {
                    val message = Message()
                    message.title = "Title title title"
                    message.message = "Aaaaaaaa"
                    MessageDao.insert( message )
                }
                logout()
                Toast.makeText(requireContext(), getString(R.string.user_logged_as, mAuth.currentUser!!.displayName), Toast.LENGTH_LONG).show()
            }
        }

        val adapter = ProductListAdapter(requireContext())
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(getDrawable(requireContext(), R.drawable.card_item_divider)!!)
        binding.recyclerview.addItemDecoration(itemDecoration)

        productViewModel = ViewModelProvider(this, ProductViewModelFactory(requireActivity().application)).get(
            ProductViewModel::class.java)
        productViewModel.allProducts.observe(viewLifecycleOwner, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.setWords(it) }
        })

        Log.i(TAG,"$TAG activity successfully created>")
        return binding.root
    }

    private fun logout(){
        AuthUI.getInstance().signOut(requireContext())
    }

    override fun onActivityResult(result : Int) {
        if (result == Activity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            if(user!=null) {
                val userFb = User()
                userFb.uid = user.uid
                userFb.name = user.displayName
                userFb.email = user.email
                UserDao.insert(userFb)
            }
            Toast.makeText(requireContext(), getString(R.string.log_in_successful), Toast.LENGTH_SHORT).show()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(requireContext(), R.string.unable_to_log_in, Toast.LENGTH_SHORT).show()
        }
    }
}