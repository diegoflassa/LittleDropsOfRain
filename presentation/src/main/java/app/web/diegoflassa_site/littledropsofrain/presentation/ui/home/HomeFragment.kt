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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.Source
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentHomeBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.adapters.ProductAdapter
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.ProductsFilterDialog.ProductsFilterDialogFragment
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.ProductsFilterDialog.ProductsFilters
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.home.model.HomeViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.home.model.HomeViewState
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.off_air.OffAirFragment
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.lang.ref.WeakReference
import java.util.*

class HomeFragment :
    Fragment(),
    ActivityResultCallback<Int>,
    View.OnClickListener,
    ProductsFilterDialogFragment.FilterListener,
    ProductAdapter.OnProductSelectedListener,
    DialogInterface.OnDismissListener {

    val viewModel: HomeViewModel by stateViewModel()
    var binding: FragmentHomeBinding by viewLifecycle()
    private lateinit var mAdapter: WeakReference<ProductAdapter>
    private lateinit var mFirestore: FirebaseFirestore
    var mFilterDialog: ProductsFilterDialogFragment? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private var mQuery: Query? = null
    private var mCurrentLocation: Location? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mRequestingLocationUpdates = false
    private lateinit var mPermissionLauncher: ActivityResultLauncher<String>

    companion object {
        const val BRAZIL_MIN_LATITUDE_NORTH = -5.1618
        const val BRAZIL_MAX_LATITUDE_SOUTH = -33.4502
        const val BRAZIL_MIN_LONGITUDE_EAST = -34.4735
        const val BRAZIL_MAX_LONGITUDE_WEST = -73.5858
        const val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY"
        private val TAG = HomeFragment::class.simpleName
        const val LIMIT = 10000
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel.viewStateLiveData.observe(
            viewLifecycleOwner
        ) {
            updateUI(it)
        }
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

        val remoteConfig = Firebase.remoteConfig
        val isOffAir = remoteConfig.getBoolean(OffAirFragment.REMOTE_CONFIG_IS_OFF_AIR)
        if (isOffAir) {
            if (Locale.getDefault().language == "pt") {
                showOffAirScreen(
                    remoteConfig.getString(OffAirFragment.REMOTE_CONFIG_OFF_AIR_MESSAGE_PT)
                )
            } else {
                showOffAirScreen(
                    remoteConfig.getString(OffAirFragment.REMOTE_CONFIG_OFF_AIR_MESSAGE_EN)
                )
            }
        } else {
            hideOffAirScreen()
            showLoadingScreen()

            mRequestingLocationUpdates = true
            mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
        }
        initFirestore()
        binding.filterBar.isEnabled = false

        mPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    getLocation()
                } else {
                    hideOffAirScreen()
                    showLoadingScreen()
                    initRecyclerView()
                    // Start listening for Firestore updates
                    mAdapter.get()?.startListening()
                    // Apply filters
                    onFilter(viewModel.viewState.filters)
                }
            }
        Log.i(TAG, "$TAG activity successfully created>")
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateValuesFromBundle(savedInstanceState)
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY
            )
        }

        // Update UI to match restored state
        updateUI(viewModel.viewState)
    }

    override fun onDestroyView() {
        mFilterDialog = null
        if (this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateUI(viewModel.viewState)
    }

    override fun onResume() {
        super.onResume()
        if (mRequestingLocationUpdates) checkForLocationUpdatesPermissionAndStartUpdates()
    }

    private fun checkForLocationUpdatesPermissionAndStartUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            mPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            return
        } else {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mFusedLocationClient.lastLocation.addOnSuccessListener {
            mCurrentLocation = it
            initRecyclerView()
            // Start listening for Firestore updates
            mAdapter.get()?.startListening()
            // Apply filters
            onFilter(viewModel.viewState.filters)
        }
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

    private fun showOffAirScreen(message: String) {
        binding.homeViewOffAir.text = message
        binding.homeViewOffAir.visibility = View.VISIBLE
    }

    private fun hideOffAirScreen() {
        binding.homeViewOffAir.visibility = View.GONE
    }

    private fun showLoadingScreen() {
        binding.homeProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen() {
        binding.homeProgress.visibility = View.GONE
    }

    private fun isLocationInBrazil(location: Location): Boolean {
        return (location.latitude > BRAZIL_MAX_LATITUDE_SOUTH && location.latitude < BRAZIL_MIN_LATITUDE_NORTH) &&
            (location.longitude > BRAZIL_MAX_LONGITUDE_WEST && location.longitude < BRAZIL_MIN_LONGITUDE_EAST)
    }

    private fun getShopByGeoLocation(query: Query): Query {
        return if (mCurrentLocation != null) {
            return if (isLocationInBrazil(mCurrentLocation!!)) {
                query.whereEqualTo(
                    Product.IS_PUBLISHED_SOURCE,
                    (true.toString() + "_" + Source.ILURIA.toString())
                )
            } else {
                query.whereEqualTo(
                    Product.IS_PUBLISHED_SOURCE,
                    (true.toString() + "_" + Source.ETSY.toString())
                )
                query.whereEqualTo(
                    Product.IS_PUBLISHED_SOURCE,
                    (true.toString() + "_" + Source.ILURIA.toString())
                )
            }
        } else {
            query.whereEqualTo(
                Product.IS_PUBLISHED_SOURCE,
                (true.toString() + "_" + Source.ILURIA.toString())
            )
        }
    }

    override fun onFilter(filters: ProductsFilters) {

        // Construct query basic query
        var query: Query = mFirestore.collection(ProductDao.COLLECTION_PATH)
        query.whereEqualTo(Product.IS_PUBLISHED, true)
        // Note that this will override the previous whereEqualTo
        query = getShopByGeoLocation(query)
        query.orderBy(Product.PRICE, Query.Direction.DESCENDING)

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
        viewModel.viewState.filters = filters
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.filter_bar -> onFilterClicked()
            R.id.button_clear_filter -> onClearFilterClicked()
        }
    }

    override fun onStop() {
        if (this::mAdapter.isInitialized) {
            mAdapter.get()?.stopListening()
        }
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
            val user = app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser.userLiveData.value
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
            // sign-in flow using the back button.
            Log.d(TAG, "${getString(R.string.unable_to_log_in)} error code: $result")
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
