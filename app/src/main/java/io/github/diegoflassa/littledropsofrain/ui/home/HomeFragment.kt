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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import io.github.diegoflassa.littledropsofrain.MainActivity
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.adapters.ProductAdapter
import io.github.diegoflassa.littledropsofrain.auth.AuthActivityResultContract
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.dao.ProductDao
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.databinding.FragmentHomeBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), ActivityResultCallback<Int>,
    ProductAdapter.OnProductSelectedListener {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mAdapter: ProductAdapter
    private lateinit var mFirestore: FirebaseFirestore
    private var mQuery: Query? = null
    private val LIMIT = 50

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

        initFirestore()
        initRecyclerView()

        Log.i(TAG,"$TAG activity successfully created>")
        return binding.root
    }

    fun onFilter() {
        // Construct query basic query
        var query: Query = mFirestore.collection(ProductDao.COLLECTION_PATH)

        /*
        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory())
        }

        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.getCity())
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            query = query.whereEqualTo("price", filters.getPrice())
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection())
        }
        */
        // Limit items
        query = query.limit(LIMIT.toLong())

        // Update the query
        mQuery = query
        mAdapter.setQuery(query)

        // Set header
        //mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this)))
        //mCurrentSortByView.setText(filters.getOrderDescription(this))

        // Save filters
        //mViewModel.setFilters(filters)
    }

    override fun onStart() {
        super.onStart()

        // Apply filters
        onFilter()

        // Start listening for Firestore updates
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    private fun logout(){
        AuthUI.getInstance().signOut(requireContext())
    }

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
    }

    private fun initRecyclerView() {
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(getDrawable(requireContext(), R.drawable.card_item_divider)!!)
        binding.recyclerview.addItemDecoration(itemDecoration)

        if (mQuery == null) {
            Log.w(MainActivity.TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = object : ProductAdapter(mQuery, this, this.requireContext()) {
            override fun onDataChanged() {
                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    //binding.recyclerview.visibility = View.GONE
                    //mEmptyView.setVisibility(View.VISIBLE)
                } else {
                    //binding.recyclerview.visibility = View.VISIBLE
                    //mEmptyView.setVisibility(View.GONE)
                }
            }

            override fun onError(e: FirebaseFirestoreException?) {
                // Show a snackbar on errors
                activity?.findViewById<View>(android.R.id.content)?.let {
                    Snackbar.make(
                        it,
                        "Error: check logs for info.", Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = mAdapter
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

    override fun onProductSelected(product: DocumentSnapshot?) {
        TODO("Not yet implemented")
    }
}