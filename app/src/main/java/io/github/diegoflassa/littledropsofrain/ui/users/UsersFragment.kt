package io.github.diegoflassa.littledropsofrain.ui.users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import io.github.diegoflassa.littledropsofrain.MainActivity
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.adapters.UsersAdapter
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.databinding.FragmentUsersBinding
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.models.UsersViewModel
import io.github.diegoflassa.littledropsofrain.models.UsersViewState
import io.github.diegoflassa.littledropsofrain.ui.home.HomeFragment

class UsersFragment : Fragment(),
    UsersAdapter.OnUserSelectedListener {

    companion object {
        val TAG = UsersFragment::class.simpleName
        fun newInstance() = UsersFragment()
    }
    private val usersViewModel: UsersViewModel by viewModels()
    var binding: FragmentUsersBinding by viewLifecycle()
    private lateinit var mAdapter: UsersAdapter
    private lateinit var mFirestore: FirebaseFirestore
    private var mQuery: Query? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        usersViewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })
        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        return binding.root
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

    override fun onResume() {
        super.onResume()
        updateUI(usersViewModel.viewState)
    }
    private fun updateUI(viewState: UsersViewState) {
        // Update the UI
        viewState.text = ""
    }

    fun showLoadingScreen(){
        binding.usersProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen(){
        binding.usersProgress.visibility = View.GONE
    }

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
    }

    private fun onFilter() {
        // Construct query basic query
        var query: Query = mFirestore.collection(UserDao.COLLECTION_PATH)
        query.orderBy("creationDate", Query.Direction.ASCENDING)
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
        query = query.limit(HomeFragment.LIMIT.toLong())

        // Update the query
        mQuery = query
        mAdapter.setQuery(query)

        // Set header
        //mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this)))
        //mCurrentSortByView.setText(filters.getOrderDescription(this))

        // Save filters
        //mViewModel.setFilters(filters)
    }

    private fun initRecyclerView() {
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.card_item_divider
            )!!
        )
        binding.recyclerview.addItemDecoration(itemDecoration)

        if (mQuery == null) {
            Log.w(MainActivity.TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = object : UsersAdapter(this@UsersFragment, mQuery, this@UsersFragment) {
            override fun onDataChanged() {
                hideLoadingScreen()
                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    binding.recyclerview.visibility = View.GONE
                    binding.usersViewEmpty.visibility = View.VISIBLE
                } else {
                    binding.recyclerview.visibility = View.VISIBLE
                    binding.usersViewEmpty.visibility = View.GONE
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

    override fun onUserSelected(user: DocumentSnapshot?) {
        Log.d(TAG, "User ${user?.id} selected")
    }

    fun showCantChangeUserToast() {
        Toast.makeText(requireContext(), getString(R.string.cant_change_user_admin), Toast.LENGTH_LONG).show()
    }

    fun showToastUnableToChangeUser() {
        Toast.makeText(requireContext(), getString(R.string.failure_changing_user), Toast.LENGTH_LONG).show()
    }
}