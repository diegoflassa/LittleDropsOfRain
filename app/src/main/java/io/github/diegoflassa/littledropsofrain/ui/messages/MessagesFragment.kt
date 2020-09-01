package io.github.diegoflassa.littledropsofrain.ui.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
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
import io.github.diegoflassa.littledropsofrain.adapters.MessageAdapter
import io.github.diegoflassa.littledropsofrain.data.dao.MessageDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.databinding.FragmentMessagesBinding
import io.github.diegoflassa.littledropsofrain.fragments.MyMessagesFilterDialogFragment
import io.github.diegoflassa.littledropsofrain.fragments.MyMessagesFilters
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.models.MessagesViewModel
import io.github.diegoflassa.littledropsofrain.models.MessagesViewState
import java.lang.ref.WeakReference


class MessagesFragment : Fragment(),
    MessageAdapter.OnMessageSelectedListener,
    MyMessagesFilterDialogFragment.FilterListener,
    View.OnClickListener {

    private val viewModel: MessagesViewModel by viewModels()
    private lateinit var mAdapter: WeakReference<MessageAdapter>
    var binding : FragmentMessagesBinding by viewLifecycle()
    private lateinit var mFilterDialog: MyMessagesFilterDialogFragment
    private lateinit var mFirestore: FirebaseFirestore
    private var mQuery: Query? = null

    companion object {
        val TAG = MessagesFragment::class.simpleName
        const val LIMIT = 50
        fun newInstance() = MessagesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
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

        binding.filterBarMyMessages.setOnClickListener(this)
        binding.buttonClearFilterMyMessages.setOnClickListener(this)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE

        // Filter Dialog
        mFilterDialog = MyMessagesFilterDialogFragment(this@MessagesFragment)
        mFilterDialog.filterListener = this

        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        return binding.root
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
        mAdapter.get()?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.filter_bar_my_messages -> onFilterClicked()
            R.id.button_clear_filter_my_messages -> onClearFilterClicked()
        }
    }

    private fun updateUI(viewState: MessagesViewState) {
        // Update the UI
        viewState.text = ""
    }

     private fun showLoadingScreen(){
        binding.messagesProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen(){
        binding.messagesProgress.visibility = View.GONE
    }

    override fun onFilter(filters : MyMessagesFilters) {

        // Construct query basic query
        var query: Query = mFirestore.collection(MessageDao.COLLECTION_PATH)
        query.orderBy(Message.CREATION_DATE, Query.Direction.DESCENDING)
        val users = ArrayList<String>(1)
        users.add(FirebaseAuth.getInstance().currentUser?.email!!)
        query = query.whereArrayContainsAny(Message.OWNERS, users)

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
        binding.textCurrentSearchMyMessages.text = HtmlCompat.fromHtml(filters.getSearchDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textCurrentSortByMyMessages.text = filters.getOrderDescription(requireContext())

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
        binding.recyclerviewMessages.addItemDecoration(itemDecoration)

        if (mQuery == null) {
            Log.w(MainActivity.TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = WeakReference( object : MessageAdapter(mQuery, this@MessagesFragment) {
            override fun onDataChanged() {
                hideLoadingScreen()
                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    binding.recyclerviewMessages.visibility = View.GONE
                    binding.messagesViewEmpty.visibility = View.VISIBLE
                } else {
                    binding.recyclerviewMessages.visibility = View.VISIBLE
                    binding.messagesViewEmpty.visibility = View.GONE
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
        binding.recyclerviewMessages.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewMessages.adapter = mAdapter.get()
    }

    private fun onFilterClicked() {
        binding.filterBarMyMessages.isEnabled = false
        // Show the dialog containing filter options
        mFilterDialog.show(parentFragmentManager, MyMessagesFilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        mFilterDialog.resetFilters()
        viewModel.viewState.filters= MyMessagesFilters.default
        onFilter(viewModel.viewState.filters)
    }

    override fun onMessageSelected(message: DocumentSnapshot?) {
        Log.d(TAG, "Message ${message?.id} selected")
    }

}