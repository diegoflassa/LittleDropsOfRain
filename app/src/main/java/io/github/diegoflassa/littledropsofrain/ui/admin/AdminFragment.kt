package io.github.diegoflassa.littledropsofrain.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
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
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import io.github.diegoflassa.littledropsofrain.MainActivity
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.adapters.MessageAdapter
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.databinding.FragmentAdminBinding
import io.github.diegoflassa.littledropsofrain.fragments.MessagesFilterDialogFragment
import io.github.diegoflassa.littledropsofrain.fragments.MessagesFilters
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.models.AdminViewModel
import io.github.diegoflassa.littledropsofrain.models.AdminViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.ref.WeakReference


class AdminFragment : Fragment(),
    MessageAdapter.OnMessageSelectedListener,
    MessagesFilterDialogFragment.FilterListener,
    View.OnClickListener {

    private val adminViewModel: AdminViewModel by viewModels()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var mAdapter: WeakReference<MessageAdapter>
    var binding : FragmentAdminBinding by viewLifecycle()
    private lateinit var mFilterDialog: MessagesFilterDialogFragment
    private lateinit var mFirestore: FirebaseFirestore
    private var mQuery: Query? = null

    companion object {
        val TAG = AdminFragment::class.simpleName
        const val LIMIT = 50
        fun newInstance() = AdminFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        adminViewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.card_item_divider
            )!!
        )

        val menu = binding.navBottomAdmin.menu
        menu.findItem(R.id.nav_reload_products).icon = IconDrawable(requireContext(), SimpleLineIconsIcons.icon_loop)
        menu.findItem(R.id.nav_send_topic_message).icon = IconDrawable(requireContext(), SimpleLineIconsIcons.icon_envelope)
        binding.navBottomAdmin.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_reload_products -> { onReloadProductsClicked() }
                R.id.nav_send_topic_message -> { onSendTopicMessageClicked() }
                else -> {}// Do nothing
            }
            true
        }

        binding.filterBarMessages.setOnClickListener(this)
        binding.buttonClearFilterMessages.setOnClickListener(this)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE

        // Filter Dialog
        mFilterDialog =
            MessagesFilterDialogFragment(
                this@AdminFragment
            )
        mFilterDialog.filterListener = this

        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Apply filters
        onFilter(adminViewModel.viewState.filters)

        // Start listening for Firestore updates
        mAdapter.get()?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.get()?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        updateUI(adminViewModel.viewState)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.filter_bar_messages -> onFilterClicked()
            R.id.button_clear_filter_messages -> onClearFilterClicked()
        }
    }

    private fun onSendTopicMessageClicked() {
        findNavController().navigate(AdminFragmentDirections.navSendTopicMessage())
    }

    private fun onReloadProductsClicked() {
        findNavController().navigate(AdminFragmentDirections.navReloadProducts())
    }

    private fun updateUI(viewState: AdminViewState) {
        // Update the UI
        viewState.text = ""
    }

     private fun showLoadingScreen(){
        binding.adminProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen(){
        binding.adminProgress.visibility = View.GONE
    }

    override fun onFilter(filters : MessagesFilters) {

        // Construct query basic query
        var query: Query = mFirestore.collection(MessageDao.COLLECTION_PATH)
        query.orderBy(Message.CREATION_DATE, Query.Direction.DESCENDING)

        // Category (equality filter)
        if (filters.hasRead()) {
            query = query.whereEqualTo(Message.READ, filters.read)
        }
        /*
        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.getCity())
        }
        */
        // Price (equality filter)
        if (filters.hasEMailSender()) {
            query = query.whereEqualTo(Message.EMAIL_SENDER, filters.emailSender!!)
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
        binding.textCurrentSearchMessages.text = HtmlCompat.fromHtml(filters.getSearchDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textCurrentSortByMessages.text = filters.getOrderDescription(requireContext())

        // Save filters
        adminViewModel.viewState.filters = filters
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

        mAdapter = WeakReference( object : MessageAdapter(mQuery, this@AdminFragment) {
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
        })
        binding.recyclerviewAdmin.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewAdmin.adapter = mAdapter.get()
    }

    private fun onFilterClicked() {
        binding.filterBarMessages.isEnabled = false
        // Show the dialog containing filter options
        mFilterDialog.show(parentFragmentManager, MessagesFilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        mFilterDialog.resetFilters()
        adminViewModel.viewState.filters= MessagesFilters.default
        onFilter(adminViewModel.viewState.filters)
    }

    override fun onMessageSelected(message: DocumentSnapshot?) {
        Log.d(TAG, "Message ${message?.id} selected")
    }

}