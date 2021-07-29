/*
 * Copyright 2021 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.my_liked_products

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentMyLikedProductsBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.adapters.MyLikedProductsAdapter
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.ProductsFilterDialog.ProductsFilterDialogFragment
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.ProductsFilterDialog.ProductsFilters
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.lang.ref.WeakReference

class MyLikedProductsFragment :
    Fragment(),
    ActivityResultCallback<Int>,
    View.OnClickListener,
    ProductsFilterDialogFragment.FilterListener,
    DialogInterface.OnDismissListener,
    MyLikedProductsAdapter.OnProductSelectedListener {

    val viewModel: MyLikedProductsViewModel by stateViewModel()
    var binding: FragmentMyLikedProductsBinding by viewLifecycle()
    private lateinit var mAdapter: WeakReference<MyLikedProductsAdapter>
    private lateinit var mFirestore: FirebaseFirestore
    var mFilterDialog: ProductsFilterDialogFragment? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private var mQuery: Query? = null

    companion object {
        private val TAG = MyLikedProductsFragment::class.simpleName
        const val LIMIT = 10000
        fun newInstance() = MyLikedProductsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyLikedProductsBinding.inflate(inflater, container, false)
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
        onFilter(viewModel.filters)
        mAdapter.get()!!.startListening()

        binding.filterBar.isEnabled = false

        Log.i(TAG, "$TAG activity successfully created>")
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateUI()
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
        updateUI()
    }

    private fun updateUI() {
        // Update the UI
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
        viewModel.filters = ProductsFilters.default
        onFilter(viewModel.filters)
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
        query.whereArrayContainsAny(
            Product.LIKES,
            listOf(app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser.userLiveData.value?.uid!!)
        )

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereArrayContainsAny(Product.CATEGORIES, filters.categories.toList())
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            val price0 = filters.price?.first
            val price1 = filters.price?.second
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
        viewModel.filters = filters
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
        onFilter(viewModel.filters)

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
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter =
            WeakReference(object : MyLikedProductsAdapter(
                this@MyLikedProductsFragment,
                mQuery,
                this@MyLikedProductsFragment
            ) {
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
            val user =
                app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser.userLiveData.value
            if (user != null) {
                val userFb = User()
                userFb.uid = user.uid
                userFb.name = user.name
                userFb.email = user.email
                UserDao.insertOrUpdate(userFb)
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
        if (productParsed != null) {
            i.data = Uri.parse(productParsed.linkProduct)
            startActivity(i)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        binding.filterBar.isEnabled = true
    }
}
