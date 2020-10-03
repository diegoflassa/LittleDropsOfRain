package app.web.diegoflassa_site.littledropsofrain.ui.home

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.adapters.ProductAdapter
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentHomeBinding
import app.web.diegoflassa_site.littledropsofrain.fragments.ProductsFilterDialogFragment
import app.web.diegoflassa_site.littledropsofrain.fragments.ProductsFilters
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.HomeViewModel
import app.web.diegoflassa_site.littledropsofrain.models.HomeViewState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import java.lang.ref.WeakReference


class HomeFragment : Fragment(), ActivityResultCallback<Int>,
    View.OnClickListener,
    ProductsFilterDialogFragment.FilterListener,
    ProductAdapter.OnProductSelectedListener, DialogInterface.OnDismissListener {

    private val viewModel: HomeViewModel by viewModels()
    var binding: FragmentHomeBinding by viewLifecycle()
    private lateinit var mAdapter: WeakReference<ProductAdapter>
    private lateinit var mFirestore: FirebaseFirestore
    var mFilterDialog: ProductsFilterDialogFragment? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private var mQuery: Query? = null

    companion object {
        val TAG = HomeFragment::class.simpleName
        const val LIMIT = 50
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })
        binding.filterBar.setOnClickListener(this)
        binding.buttonClearFilter.setOnClickListener(this)

        // Filter Dialog
        mFilterDialog =
            ProductsFilterDialogFragment()
        mFilterDialog?.filterListener = this

        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        binding.filterBar.isEnabled = false

        Log.i(TAG, "$TAG activity successfully created>")
        return binding.root
    }

    override fun onDestroyView() {
        mFilterDialog = null
        if (this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }

    private fun updateUI(viewState: HomeViewState) {
        // Update the UI
        viewState.text = ""
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.GONE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.VISIBLE
    }

    private fun onFilterClicked() {
        binding.filterBar.isEnabled = false
        // Show the dialog containing filter options
        mFilterDialog?.show(parentFragmentManager, ProductsFilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        mFilterDialog?.resetFilters()
        viewModel.viewState.filters = ProductsFilters.default
        onFilter(viewModel.viewState.filters)
    }

    private fun showLoadingScreen() {
        binding.homeProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen() {
        binding.homeProgress.visibility = View.GONE
    }

    override fun onFilter(filters: ProductsFilters) {

        // Construct query basic query
        var query: Query = mFirestore.collection(ProductDao.COLLECTION_PATH)
        query.orderBy(Product.PRICE, Query.Direction.DESCENDING)

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereArrayContainsAny(Product.CATEGORIES, filters.categories.toList())
        }
        /*
        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.getCity())
        }
        */
        // Price (equality filter)
        if (filters.hasPrice()) {
            val price0 = filters.price?.get(0)
            val price1 = filters.price?.get(1)
            query = query.whereGreaterThanOrEqualTo(Product.PRICE, Integer.valueOf(price0!!))
                .whereLessThanOrEqualTo(Product.PRICE, Integer.valueOf(price1!!))
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.sortBy!!, filters.sortDirection!!)
        }
        // Limit items
        query = query.limit(LIMIT.toLong())

        // Update the query
        mQuery = query
        mAdapter.get()?.setQuery(query)
        showLoadingScreen()

        // Set header
        binding.textCurrentSearch.text =
            HtmlCompat.fromHtml(filters.getSearchDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textCurrentSortBy.text = filters.getOrderDescription(requireContext())

        // Save filters
        viewModel.viewState.filters = filters
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.filter_bar -> onFilterClicked()
            R.id.button_clear_filter -> onClearFilterClicked()
        }
    }

    override fun onStart() {
        super.onStart()

        // Apply filters
        onFilter(viewModel.viewState.filters)

        // Start listening for Firestore updates
        mAdapter.get()?.startListening()
    }

    override fun onStop() {
        mAdapter.get()?.stopListening()
        super.onStop()
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

        mAdapter =
            WeakReference(object : ProductAdapter(this@HomeFragment, mQuery, this@HomeFragment) {
                override fun onDataChanged() {
                    binding.filterBar.isEnabled = true
                    hideLoadingScreen()
                    // Show/hide content if the query returns empty.
                    if (itemCount == 0) {
                        binding.recyclerview.visibility = View.GONE
                        binding.homeViewEmpty.visibility = View.VISIBLE
                    } else {
                        binding.recyclerview.visibility = View.VISIBLE
                        binding.homeViewEmpty.visibility = View.GONE
                    }
                }

                override fun onError(e: FirebaseFirestoreException?) {
                    // Show a snackbar on errors
                    activity?.findViewById<View>(android.R.id.content)?.let {
                        Snackbar.make(it, "Error: check logs for info.", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            })
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = mAdapter.get()
    }

    override fun onActivityResult(result: Int) {
        if (result == AppCompatActivity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userFb = User()
                userFb.uid = user.uid
                userFb.name = user.displayName
                userFb.email = user.email
                UserDao.insert(userFb)
            }
            Toast.makeText(
                requireContext(),
                getString(R.string.log_in_successful),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(requireContext(), R.string.unable_to_log_in, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onProductSelected(product: DocumentSnapshot?) {
        val i = Intent(Intent.ACTION_VIEW)
        val productParsed: Product? = product?.toObject(Product::class.java)
        i.data = Uri.parse(productParsed?.linkProduct)
        startActivity(i)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        binding.filterBar.isEnabled = true
    }

}
