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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.all_messages

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentAllMessagesBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.adapters.MessageAdapter
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.AllMessagesFilterDialog.AllMessagesFilterDialogFragment
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.AllMessagesFilterDialog.AllMessagesFilters
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.all_messages.model.AllMessagesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import java.lang.ref.WeakReference

// @AndroidEntryPoint
class AllMessagesFragment :
    Fragment(),
    MessageAdapter.OnMessageSelectedListener,
    AllMessagesFilterDialogFragment.FilterListener,
    View.OnClickListener,
    DialogInterface.OnDismissListener {

    private var isStopped: Boolean = false

    private lateinit var viewModel: AllMessagesViewModel // by stateViewModel()
    private lateinit var mAdapter: WeakReference<MessageAdapter>
    var binding: FragmentAllMessagesBinding by viewLifecycle()
    private var mFilterDialog: AllMessagesFilterDialogFragment? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mFirestore: FirebaseFirestore
    private var mQuery: Query? = null

    companion object {
        const val KEY_ALL_MESSAGES: String = "Admin - All Messages"
        private val TAG = AllMessagesFragment::class.simpleName
        const val LIMIT = 10000
        fun newInstance() = AllMessagesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AllMessagesViewModel::class.java)
        binding = FragmentAllMessagesBinding.inflate(inflater, container, false)
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.card_item_divider
            )!!
        )

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
            if (drawerLayout != null) {
                toggle.syncState()
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        binding.filterBarAllMessages.setOnClickListener(this)
        binding.buttonClearFilterAllMessages.setOnClickListener(this)

        // Filter Dialog
        mFilterDialog =
            AllMessagesFilterDialogFragment()
        mFilterDialog?.filterListener = this

        showLoadingScreen()
        initFirestore()
        initRecyclerView()
        handleBundle()

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

    private fun handleBundle() {
        if (AllMessagesFragmentArgs.fromBundle(requireArguments()).who != KEY_ALL_MESSAGES) {
            val filter = AllMessagesFilters.default
            filter.emailSender = AllMessagesFragmentArgs.fromBundle(requireArguments()).who
            viewModel.filters = filter
            onFilter(viewModel.filters)
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
        isStopped = true
        mAdapter.get()?.stopListening()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        isStopped = false
        updateUI(viewModel)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.filter_bar_all_messages -> onFilterClicked()
            R.id.button_clear_filter_all_messages -> onClearFilterClicked()
        }
    }

    private fun updateUI(viewState: AllMessagesViewModel) {
        // Update the UI
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.VISIBLE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
    }

    private fun showLoadingScreen() {
        binding.allMessagesProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen() {
        binding.allMessagesProgress.visibility = View.GONE
    }

    override fun onFilter(filters: AllMessagesFilters) {

        // Construct query basic query
        var query: Query = mFirestore.collection(MessageDao.COLLECTION_PATH)
        query.orderBy(Message.CREATION_DATE, Query.Direction.DESCENDING)

        // Message Type (equality filter)
        if (filters.hasMessageType()) {
            query = query.whereEqualTo(Message.TYPE, filters.type.toString())
        }

        // Read (equality filter)
        if (filters.hasRead()) {
            query = query.whereEqualTo(Message.READ, filters.read)
        }

        // EMail sender (equality filter)
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
        binding.textCurrentSearchAllMessages.text =
            HtmlCompat.fromHtml(filters.getSearchDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textCurrentSortByAllMessages.text = filters.getOrderDescription(requireContext())

        // Save filters
        viewModel.filters = filters
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
        binding.recyclerviewAllMessages.addItemDecoration(itemDecoration)

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = WeakReference(object :
            MessageAdapter(requireContext(), mQuery, this@AllMessagesFragment) {
            override fun onDataChanged() {
                hideLoadingScreen()
                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    binding.recyclerviewAllMessages.visibility = View.GONE
                    binding.allMessagesViewEmpty.visibility = View.VISIBLE
                } else {
                    binding.recyclerviewAllMessages.visibility = View.VISIBLE
                    binding.allMessagesViewEmpty.visibility = View.GONE
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
        binding.recyclerviewAllMessages.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewAllMessages.adapter = mAdapter.get()
    }

    private fun onFilterClicked() {
        binding.filterBarAllMessages.isEnabled = false
        // Show the dialog containing filter options
        mFilterDialog?.show(parentFragmentManager, AllMessagesFilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        mFilterDialog?.resetFilters()
        viewModel.filters = AllMessagesFilters.default
        onFilter(viewModel.filters)
    }

    override fun onMessageSelected(message: DocumentSnapshot?) {
        Log.d(TAG, "Message ${message?.id} selected")
    }

    override fun onDismiss(dialog: DialogInterface?) {
        binding.filterBarAllMessages.isEnabled = true
    }
}
