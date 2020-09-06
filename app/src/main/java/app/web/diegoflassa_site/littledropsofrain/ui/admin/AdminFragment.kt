package app.web.diegoflassa_site.littledropsofrain.ui.admin

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
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.adapters.MessageAdapter
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentAdminBinding
import app.web.diegoflassa_site.littledropsofrain.fragments.AllMessagesFilterDialogFragment
import app.web.diegoflassa_site.littledropsofrain.fragments.AllMessagesFilters
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.AdminViewModel
import app.web.diegoflassa_site.littledropsofrain.models.AdminViewState
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import java.lang.ref.WeakReference


class AdminFragment : Fragment(),
    MessageAdapter.OnMessageSelectedListener,
    AllMessagesFilterDialogFragment.FilterListener,
    View.OnClickListener {

    private var isStopped: Boolean = false
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var mAdapter: WeakReference<MessageAdapter>
    var binding : FragmentAdminBinding by viewLifecycle()
    private var mFilterDialog: AllMessagesFilterDialogFragment? = null
    private lateinit var mFirestore: FirebaseFirestore
    private var mQuery: Query? = null

    companion object {
        const val KEY_ALL_MESSAGES: String = "Admin - All Messages"
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
        viewModel.viewState.observe(viewLifecycleOwner, {
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

        binding.filterBarAllMessages.setOnClickListener(this)
        binding.buttonClearFilterAllMessages.setOnClickListener(this)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE

        // Filter Dialog
        mFilterDialog =
            AllMessagesFilterDialogFragment(
                this@AdminFragment
            )
        mFilterDialog?.filterListener = this

        showLoadingScreen()
        initFirestore()
        initRecyclerView()
        handleBundle()
        return binding.root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        mFilterDialog = null
    }

    private fun handleBundle(){
        if(AdminFragmentArgs.fromBundle(requireArguments()).who != KEY_ALL_MESSAGES) {
            val filter = AllMessagesFilters.default
            filter.emailSender = AdminFragmentArgs.fromBundle(requireArguments()).who
            viewModel.viewState.filters =filter
            onFilter(viewModel.viewState.filters)
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
        super.onStop()
        isStopped = true
        mAdapter.get()?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.filter_bar_all_messages -> onFilterClicked()
            R.id.button_clear_filter_all_messages -> onClearFilterClicked()
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

    override fun onFilter(filters : AllMessagesFilters) {

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
        binding.textCurrentSearchAllMessages.text = HtmlCompat.fromHtml(filters.getSearchDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textCurrentSortByAllMessages.text = filters.getOrderDescription(requireContext())

        // Save filters
        viewModel.viewState.filters = filters
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
        binding.filterBarAllMessages.isEnabled = false
        // Show the dialog containing filter options
        mFilterDialog?.show(parentFragmentManager, AllMessagesFilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        mFilterDialog?.resetFilters()
        viewModel.viewState.filters= AllMessagesFilters.default
        onFilter(viewModel.viewState.filters)
    }

    override fun onMessageSelected(message: DocumentSnapshot?) {
        Log.d(TAG, "Message ${message?.id} selected")
    }

}