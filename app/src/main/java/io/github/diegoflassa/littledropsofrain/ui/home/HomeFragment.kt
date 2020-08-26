package io.github.diegoflassa.littledropsofrain.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import io.github.diegoflassa.littledropsofrain.MainActivity
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.adapters.ProductAdapter
import io.github.diegoflassa.littledropsofrain.data.dao.ProductDao
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.databinding.FragmentHomeBinding
import io.github.diegoflassa.littledropsofrain.fragments.ProductsFilterDialogFragment
import io.github.diegoflassa.littledropsofrain.fragments.ProductsFilters
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.models.HomeViewModel
import io.github.diegoflassa.littledropsofrain.models.HomeViewState
import java.lang.ref.WeakReference


class HomeFragment : Fragment(), ActivityResultCallback<Int>,
    View.OnClickListener,
    ProductsFilterDialogFragment.FilterListener,
    ProductAdapter.OnProductSelectedListener {

    private val homeViewModel: HomeViewModel by viewModels()
    var binding: FragmentHomeBinding by viewLifecycle()
    private lateinit var mAdapter: WeakReference<ProductAdapter>
    private lateinit var mFirestore: FirebaseFirestore
    lateinit var mFilterDialog: ProductsFilterDialogFragment
    private var mQuery: Query? = null

    companion object{
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
        homeViewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })
        binding.filterBar.setOnClickListener(this)
        binding.buttonClearFilter.setOnClickListener(this)

        // Filter Dialog
        mFilterDialog =
            ProductsFilterDialogFragment(
                this@HomeFragment
            )
        mFilterDialog.filterListener = this

        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        binding.filterBar.isEnabled = false

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.VISIBLE

        Log.i(TAG,"$TAG activity successfully created>")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI(homeViewModel.viewState)
    }
    private fun updateUI(viewState: HomeViewState) {
        // Update the UI
        viewState.text = ""
    }

    private fun onFilterClicked() {
        binding.filterBar.isEnabled = false
        // Show the dialog containing filter options
        mFilterDialog.show(parentFragmentManager, ProductsFilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        mFilterDialog.resetFilters()
        homeViewModel.viewState.filters = ProductsFilters.default
        onFilter(homeViewModel.viewState.filters)
    }

    private fun showLoadingScreen(){
        binding.homeProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen(){
        binding.homeProgress.visibility = View.GONE
    }

    override fun onFilter(filters : ProductsFilters) {

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
            val price0=  filters.price?.get(0)
            val price1=  filters.price?.get(1)
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
        binding.textCurrentSearch.text = HtmlCompat.fromHtml(filters.getSearchDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textCurrentSortBy.text = filters.getOrderDescription(requireContext())

        // Save filters
        homeViewModel.viewState.filters = filters
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
        onFilter(homeViewModel.viewState.filters)

        // Start listening for Firestore updates
        mAdapter.get()?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.get()?.stopListening()
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

        mAdapter = WeakReference( object : ProductAdapter(this@HomeFragment, mQuery, this@HomeFragment) {
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
                    Snackbar.make(it,"Error: check logs for info.", Snackbar.LENGTH_LONG).show()
                }
            }
        })
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = mAdapter.get()
    }

    override fun onActivityResult(result : Int) {
        if (result == AppCompatActivity.RESULT_OK) {
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
        val i = Intent(Intent.ACTION_VIEW)
        val productParsed: Product? = product?.toObject(Product::class.java)
        i.data = Uri.parse(productParsed?.linkProduct)
        startActivity(i)
    }

}
