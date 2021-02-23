/*
 * Copyright 2020 The Little Drops of Rain Project
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

package app.web.diegoflassa_site.littledropsofrain.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.adapters.LikesDialogAdapter
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.DialogLikesFragmentBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import java.lang.ref.WeakReference

class LikesDialogFragment(var product: Product?) : DialogFragment() {

    constructor() : this(null)

    companion object {
        val TAG = LikesDialogFragment::class.simpleName
    }

    var binding: DialogLikesFragmentBinding by viewLifecycle()
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mAdapter: WeakReference<LikesDialogAdapter>
    private var mQuery: Query? = null
    private val args: LikesDialogFragmentArgs by navArgs()

    /** The system calls this only when creating the layout in a dialog.  */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLikesFragmentBinding.inflate(inflater, container, false)
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        product = args.product
        binding.imgVwProduct.load(product!!.imageUrl) { placeholder(R.drawable.image_placeholder) }
        showLoadingScreen()
        initFirestore()
        initQuery()
        initRecyclerView()
        mAdapter.get()?.setQuery(createQuery(product!!.likes))
        mAdapter.get()!!.startListening()
        return binding.root
    }

    override fun onStop() {
        mAdapter.get()!!.stopListening()
        super.onStop()
    }

    private fun initQuery() {
        mQuery = createQuery(product!!.likes)
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
        binding.rcVwLikes.addItemDecoration(itemDecoration)

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter =
            WeakReference<LikesDialogAdapter>(object :
                    LikesDialogAdapter(this@LikesDialogFragment, mQuery, product!!) {
                    override fun onDataChanged() {
                        hideLoadingScreen()
                        // Show/hide content if the query returns empty.
                        if (itemCount == 0) {
                            binding.rcVwLikes.visibility = View.GONE
                            binding.likesDialogViewEmpty.visibility = View.VISIBLE
                        } else {
                            binding.rcVwLikes.visibility = View.VISIBLE
                            binding.likesDialogViewEmpty.visibility = View.GONE
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
        binding.rcVwLikes.layoutManager = LinearLayoutManager(activity)
        binding.rcVwLikes.adapter = mAdapter.get()
    }

    fun showLoadingScreen() {
        binding.likesDialogProgress.visibility = View.VISIBLE
    }

    fun hideLoadingScreen() {
        binding.likesDialogProgress.visibility = View.GONE
    }

    private fun createQuery(userIDs: List<String>): Query? {
        mQuery = mFirestore.collection(UserDao.COLLECTION_PATH).whereIn(User.UID, userIDs)
        return mQuery
    }
}
