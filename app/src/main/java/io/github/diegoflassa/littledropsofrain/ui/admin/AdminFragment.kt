package io.github.diegoflassa.littledropsofrain.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import io.github.diegoflassa.littledropsofrain.MainActivity
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.activities.SendMessageActivity
import io.github.diegoflassa.littledropsofrain.adapters.MessageAdapter
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.dao.ProductDao
import io.github.diegoflassa.littledropsofrain.databinding.FragmentAdminBinding
import io.github.diegoflassa.littledropsofrain.helpers.Helper
import io.github.diegoflassa.littledropsofrain.ui.home.HomeFragment
import io.github.diegoflassa.littledropsofrain.xml.ProductParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


class AdminFragment : Fragment(),
    MessageAdapter.OnMessageSelectedListener {

    private lateinit var adminViewModel: AdminViewModel
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var mAdapter: MessageAdapter
    private lateinit var binding : FragmentAdminBinding
    private lateinit var mFirestore: FirebaseFirestore
    private var mQuery: Query? = null

    companion object {
        const val TAG = "AdminFragment"
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        adminViewModel =
            ViewModelProvider.NewInstanceFactory().create(AdminViewModel::class.java)
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.card_item_divider
            )!!)
        binding.btnReloadProducts.setOnClickListener {
            fetchProducts()
        }

        binding.sendGlobalMessage.setOnClickListener {
            senGlobalMessage()
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(context, SendMessageActivity::class.java))
        }

        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        return binding.root
    }

    private fun senGlobalMessage() {

        val url = "https://fcm.googleapis.com/fcm/send"
        val myReq: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { Toast.makeText(activity, "Bingo Success", Toast.LENGTH_SHORT).show() },
            Response.ErrorListener { Toast.makeText(activity, "Oops error", Toast.LENGTH_SHORT).show() }) {

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val rawParameters: MutableMap<Any?, Any?> = Hashtable()
                val dataValue : MutableMap<Any?, Any?> = HashMap()
                rawParameters["data"] = JSONObject(dataValue).toString()
                rawParameters["topic"] = "topics/news"
                return JSONObject(rawParameters).toString().toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            var YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE = "AAAA9E171CQ:APA91bEIxK6ptOXRXwKiI2XsHKkXKYP5CzM9HQdP56Lj1OcBK69HTkvTf0b1If7Zp0F1c1VbrnLopZrmDnM-fC3krTnO4BkXlZ2A2V_aCar4s0iK9KP1fa4bCrM2chdMj1_O7WAlOfBj"
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Authorization"] = "key=$YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE"
                return headers
            }
        }
        Volley.newRequestQueue(activity).add(myReq)
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
            )!!)
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
        Log.d(TAG,"Message ${message?.id} selected")
    }

}