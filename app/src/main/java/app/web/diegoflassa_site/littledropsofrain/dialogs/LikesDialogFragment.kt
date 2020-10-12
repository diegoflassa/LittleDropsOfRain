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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.adapters.LikesDialogAdapter
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.DialogLikesFragmentBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import java.lang.ref.WeakReference

class LikesDialogFragment(var product: Product) : DialogFragment() {

    companion object {
        val TAG = LikesDialogFragment::class.simpleName
    }

    var binding: DialogLikesFragmentBinding by viewLifecycle()
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mAdapter: WeakReference<LikesDialogAdapter>
    private var mQuery: Query? = null

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
    ): View? {
        binding = DialogLikesFragmentBinding.inflate(inflater, container, false)
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        Picasso.get().load(product.imageUrl).placeholder(R.drawable.image_placeholder)
            .into(binding.imgVwProduct)
        showLoadingScreen()
        initFirestore()
        initRecyclerView()
        return binding.root
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
            Log.w(MainActivity.TAG, "No query, not initializing RecyclerView")
        }

        mAdapter =
            WeakReference(object :
                LikesDialogAdapter(this@LikesDialogFragment, createQuery(product.likes), product) {
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

    fun createQuery(userIDs: List<String>): Query? {
        mQuery = mFirestore.collection(UserDao.COLLECTION_PATH).whereIn(User.UID, userIDs)
        return mQuery
    }

}