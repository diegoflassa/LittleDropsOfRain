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

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentUsersBinding
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser
import app.web.diegoflassa_site.littledropsofrain.presentation.adapters.UsersAdapter
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.home.HomeFragment
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.send_message.SendMessageFragment
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.users.model.UsersViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.users.model.UsersViewState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query

import java.lang.ref.WeakReference

@ExperimentalStdlibApi
class UsersFragment :
    Fragment(),
    UsersAdapter.OnUserSelectedListener {

    companion object {
        val TAG = UsersFragment::class.simpleName
        fun newInstance() = UsersFragment()
    }

    val viewModel: UsersViewModel by viewModels()
    var binding: FragmentUsersBinding by viewLifecycle()
    private lateinit var mAdapter: WeakReference<UsersAdapter>
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var toggle: ActionBarDrawerToggle
    private var mQuery: Query? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        viewModel.viewStateLiveData.observe(
            viewLifecycleOwner
        ) {
            updateUI(it)
        }
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
            if (drawerLayout != null)
                toggle.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
        showLoadingScreen()
        initFirestore()
        initRecyclerView()

        return binding.root
    }

    override fun onDestroyView() {
        if (this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()

        // Apply filters
        onFilter()

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

    private fun updateUI(viewState: UsersViewState) {
        // Update the UI
        viewState.text = ""
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.VISIBLE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
    }

    fun showLoadingScreen() {
        binding.usersProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen() {
        binding.usersProgress.visibility = View.GONE
    }

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
    }

    private fun onFilter() {
        // Construct query basic query
        var query: Query = mFirestore.collection(UserDao.COLLECTION_PATH)
        query.orderBy("creationDate", Query.Direction.ASCENDING)
        // Limit items
        query = query.limit(HomeFragment.LIMIT.toLong())

        // Update the query
        mQuery = query
        mAdapter.get()?.setQuery(query)
    }

    private fun initRecyclerView() {
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.card_item_divider
            )!!
        )
        binding.recyclerview.addItemDecoration(itemDecoration)

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter =
            WeakReference(object : UsersAdapter(this@UsersFragment, mQuery, this@UsersFragment) {
                override fun onDataChanged() {
                    hideLoadingScreen()
                    // Show/hide content if the query returns empty.
                    if (itemCount == 0) {
                        binding.recyclerview.visibility = View.GONE
                        binding.usersViewEmpty.visibility = View.VISIBLE
                    } else {
                        binding.recyclerview.visibility = View.VISIBLE
                        binding.usersViewEmpty.visibility = View.GONE
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
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = mAdapter.get()
    }

    override fun onUserSelected(user: DocumentSnapshot?) {
        val parsedUser = user?.toObject(User::class.java)
        showSendMessageTo(parsedUser!!)
    }

    private fun showSendMessageTo(user: User) {
        val message = Message()
        message.emailSender = LoggedUser.userLiveData.value!!.email
        message.senderId = LoggedUser.userLiveData.value!!.uid
        message.sender = user.email
        message.message = ""
        val bundle = Bundle()
        bundle.putString(SendMessageFragment.ACTION_SEND_KEY, SendMessageFragment.ACTION_SEND)
        bundle.putParcelable(SendMessageFragment.KEY_MESSAGE, message)
        findNavController().navigate(R.id.send_message_fragment, bundle)
    }

    fun showToastUnableToChangeUser() {
        Toast.makeText(
            requireContext(),
            getString(R.string.failure_changing_user),
            Toast.LENGTH_LONG
        ).show()
    }
}
