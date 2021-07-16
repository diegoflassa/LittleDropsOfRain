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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.messages

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentMessagesBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.adapters.MessageAdapter
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.MyMessagesFilterDialog.MyMessagesFilterDialogFragment
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.MyMessagesFilterDialog.MyMessagesFilters
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import java.lang.ref.WeakReference

class MessagesFragment :
    Fragment(),
    MessageAdapter.OnMessageSelectedListener,
    MyMessagesFilterDialogFragment.FilterListener,
    View.OnClickListener,
    DialogInterface.OnDismissListener {

    private val viewModel: MessagesViewModel by viewModels(
        factoryProducer = {
            SavedStateViewModelFactory(
                this.requireActivity().application,
                this
            )
        }
    )
    private lateinit var mAdapter: WeakReference<MessageAdapter>
    var binding: FragmentMessagesBinding by viewLifecycle()
    private var mFilterDialog: MyMessagesFilterDialogFragment? = null
    private lateinit var mFirestore: FirebaseFirestore
    private var mQuery: Query? = null

    companion object {
        private val TAG = MessagesFragment::class.simpleName
        const val LIMIT = 10000
        fun newInstance() = MessagesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.saveState()
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.card_item_divider
            )!!
        )

        binding.filterBarMyMessages.setOnClickListener(this)
        binding.buttonClearFilterMyMessages.setOnClickListener(this)

        // Filter Dialog
        mFilterDialog = MyMessagesFilterDialogFragment()
        mFilterDialog?.filterListener = this

        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        return binding.root
    }

    override fun onDestroyView() {
        mFilterDialog = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()

        // Apply filters
        onFilter(viewModel.filters)

        // Start listening for Firestore updates
        mAdapter.get()?.startListening()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateUI()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.get()?.stopListening()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.filter_bar_my_messages -> onFilterClicked()
            R.id.button_clear_filter_my_messages -> onClearFilterClicked()
        }
    }

    private fun updateUI() {
        // Update the UI
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.GONE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
    }

    private fun showLoadingScreen() {
        binding.messagesProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen() {
        binding.messagesProgress.visibility = View.GONE
    }

    override fun onFilter(filters: MyMessagesFilters) {

        // Construct query basic query
        var query: Query = mFirestore.collection(MessageDao.COLLECTION_PATH)
        query.orderBy(Message.CREATION_DATE, Query.Direction.DESCENDING)
        val users = ArrayList<String>(1)
        users.add(app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser.userLiveData.value?.email!!)
        query = query.whereArrayContainsAny(Message.OWNERS, users)

        // Message Type (equality filter)
        if (filters.hasMessageType()) {
            query = query.whereEqualTo(Message.TYPE, filters.type.toString())
        }

        // Read (equality filter)
        if (filters.hasRead()) {
            query = query.whereEqualTo(Message.READ, filters.read)
        }

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
        binding.textCurrentSearchMyMessages.text =
            HtmlCompat.fromHtml(filters.getSearchDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textCurrentSortByMyMessages.text = filters.getOrderDescription(requireContext())

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
        binding.recyclerviewMessages.addItemDecoration(itemDecoration)

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter =
            WeakReference(object : MessageAdapter(requireContext(), mQuery, this@MessagesFragment) {
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
        mFilterDialog?.show(parentFragmentManager, MyMessagesFilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        mFilterDialog?.resetFilters()
        viewModel.filters = MyMessagesFilters.default
        onFilter(viewModel.filters)
    }

    override fun onMessageSelected(message: DocumentSnapshot?) {
        Log.d(TAG, "Message ${message?.id} selected")
    }

    override fun onDismiss(dialog: DialogInterface?) {
        binding.filterBarMyMessages.isEnabled = true
    }
}
