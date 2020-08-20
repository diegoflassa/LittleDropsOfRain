package io.github.diegoflassa.littledropsofrain.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import io.github.diegoflassa.littledropsofrain.adapters.MessageAdapter
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.dao.ProductDao
import io.github.diegoflassa.littledropsofrain.databinding.FragmentAdminBinding
import io.github.diegoflassa.littledropsofrain.helpers.Helper
import io.github.diegoflassa.littledropsofrain.models.AdminViewModel
import io.github.diegoflassa.littledropsofrain.ui.home.HomeFragment
import io.github.diegoflassa.littledropsofrain.ui.send_message.SendMessageFragment
import io.github.diegoflassa.littledropsofrain.xml.ProductParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class AdminFragment : Fragment(),
    MessageAdapter.OnMessageSelectedListener {

    private val adminViewModel: AdminViewModel by viewModels()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var mAdapter: MessageAdapter
    private var binding : FragmentAdminBinding by viewLifecycle()
    private lateinit var mFirestore: FirebaseFirestore
    private var mQuery: Query? = null

    companion object {
        val TAG = AdminFragment::class.simpleName
        fun newInstance() = AdminFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.card_item_divider
            )!!
        )
        binding.btnReloadProducts.setOnClickListener {
            fetchProducts()
        }

        binding.sendGlobalMessage.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionNavAdminToSendSubscriptionMessageFragment())
        }
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        fab?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(SendMessageFragment.ACTION_SEND_KEY, SendMessageFragment.ACTION_SEND)
            findNavController().navigate(R.id.action_nav_admin_to_sendMessageFragment, bundle)
        }

        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        return binding.root
    }

     private fun showLoadingScreen(){
        binding.adminProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen(){
        binding.adminProgress.visibility = View.GONE
    }

    private fun onFilter() {
        // Construct query basic query
        var query: Query = mFirestore.collection(MessageDao.COLLECTION_PATH)
        query.orderBy("creationDate", Query.Direction.DESCENDING)
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

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
    }

    private fun initRecyclerView() {
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.card_item_divider
            )!!
        )
        binding.recyclerviewAdmin.addItemDecoration(itemDecoration)

        if (mQuery == null) {
            Log.w(MainActivity.TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = object : MessageAdapter(mQuery, this@AdminFragment) {
            override fun onDataChanged() {
                hideLoadingScreen()
                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    binding.recyclerviewAdmin.visibility = View.GONE
                    binding.adminViewEmpty.visibility = View.VISIBLE
                } else {
                    binding.recyclerviewAdmin.visibility = View.VISIBLE
                    binding.adminViewEmpty.visibility = View.GONE
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
        binding.recyclerviewAdmin.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewAdmin.adapter = mAdapter
    }

    private fun fetchProducts(){
        val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        scheduler.scheduleAtFixedRate({
            ioScope.launch {
                val productParser = ProductParser()
                val products = productParser.parse()
                ProductDao.insertAll(Helper.iluriaProductToProduct(products))
            }
        }, 0, 12, TimeUnit.HOURS)
    }

    override fun onMessageSelected(message: DocumentSnapshot?) {
        Log.d(TAG, "Message ${message?.id} selected")
    }

}