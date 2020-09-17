package app.web.diegoflassa_site.littledropsofrain.ui.users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.adapters.UsersAdapter
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentUsersBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.UsersViewModel
import app.web.diegoflassa_site.littledropsofrain.models.UsersViewState
import app.web.diegoflassa_site.littledropsofrain.ui.home.HomeFragment
import app.web.diegoflassa_site.littledropsofrain.ui.send_message.SendMessageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference


class UsersFragment : Fragment(),
    UsersAdapter.OnUserSelectedListener {

    companion object {
        val TAG = UsersFragment::class.simpleName
        fun newInstance() = UsersFragment()
    }
    private val viewModel: UsersViewModel by viewModels()
    var binding: FragmentUsersBinding by viewLifecycle()
    private lateinit var mAdapter: WeakReference<UsersAdapter>
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var toggle : ActionBarDrawerToggle
    private var mQuery: Query? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setNavigationOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            toggle = ActionBarDrawerToggle(
                activity,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout?.addDrawerListener(toggle)
            if(drawerLayout!=null)
                toggle.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        return binding.root
    }

    override fun onDestroyView(){
        if(this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()

        // Apply filters
        onFilter()

        // Start listening for Firestore updates
        mAdapter.get()?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.get()?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }
    private fun updateUI(viewState: UsersViewState) {
        // Update the UI
        viewState.text = ""
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.VISIBLE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
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
        mAdapter.get()?.setQuery(query)

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

        mAdapter = WeakReference( object : UsersAdapter(this@UsersFragment, mQuery, this@UsersFragment) {
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
        })
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = mAdapter.get()
    }

    override fun onUserSelected(user: DocumentSnapshot?) {
        val parsedUser = user?.toObject(User::class.java)
        showSendMessageTo(parsedUser!!)
   }

    private fun showSendMessageTo(user: User) {
        val message = Message()
        message.sender = user.email
        message.message = ""
        val bundle = Bundle()
        bundle.putString(SendMessageFragment.ACTION_SEND_KEY, SendMessageFragment.ACTION_SEND)
        bundle.putParcelable(SendMessageFragment.KEY_MESSAGE, message)
        findNavController().navigate(R.id.send_message_fragment, bundle)
    }

    fun showToastUnableToChangeUser() {
        Toast.makeText(
            requireContext(),
            getString(R.string.failure_changing_user),
            Toast.LENGTH_LONG
        ).show()
    }
}