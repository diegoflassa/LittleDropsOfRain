package app.web.diegoflassa_site.littledropsofrain.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.Query
import app.web.diegoflassa_site.littledropsofrain.MyApplication
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentMyMessagesFiltersBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.ui.messages.MessagesFragment

/**
 * Dialog Fragment containing filter form.
 */
open class MyMessagesFilterDialogFragment(fragment : MessagesFragment) : DialogFragment(),
    View.OnClickListener {

    companion object {
        val TAG = MyMessagesFilterDialogFragment::class.simpleName
    }

    interface FilterListener {
        fun onFilter(filters: MyMessagesFilters)
    }

    private var messagesFragment : MessagesFragment = fragment
    private var mSortSpinner: Spinner? = null
    var filterListener: FilterListener? = null
    var binding: FragmentMyMessagesFiltersBinding by viewLifecycle()
    private var mRootView : View?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyMessagesFiltersBinding.inflate(inflater, container, false)
        mSortSpinner = binding.spinnerSort
        //binding.iconUsers.setImageDrawable(IconDrawable(requireContext(), SimpleLineIconsIcons.icon_users))
        binding.buttonSearchMessages.setOnClickListener(this)
        binding.buttonCancelMessages.setOnClickListener(this)
        binding.checkBoxMsgRead.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            binding.switchMsgRead.isEnabled = checked
            if(!checked) {
                filters.read = null
            }
        }
        binding.switchMsgRead.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            filters.read = checked
        }
        mRootView= binding.root
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface){
        super.onDismiss(dialog)
        messagesFragment.binding.filterBarMyMessages.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_search_messages -> onSearchClicked()
            R.id.button_cancel_messages -> onCancelClicked()
        }
    }

    private fun onSearchClicked() {
        if (filterListener != null) {
            filterListener!!.onFilter(filters)
        }
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

    private val selectedSortBy: String?
        get() {
            if(mRootView!=null&&!isDetached) {
                return when (mSortSpinner!!.selectedItem as String) {
                    MyApplication.getContext()
                        .getString(R.string.sort_by_creation_date) -> {
                        return Message.CREATION_DATE
                    }
                    MyApplication.getContext()
                        .getString(R.string.sort_by_read) -> {
                        return Message.READ
                    }
                    else -> null
                }
            }
            return null
        }

    private val selectedRead: Boolean?
        get() {
            if(mRootView!=null&&!isDetached) {
                if(binding.checkBoxMsgRead.isChecked){
                    return binding.switchMsgRead.isChecked
                }
            }
            return null
        }

    private val sortDirection: Query.Direction?
        get() {
            if(mRootView!=null&&!isDetached) {
                return when (mSortSpinner!!.selectedItem as String) {
                    MyApplication.getContext()
                        .getString(R.string.sort_by_creation_date) -> {
                        return Query.Direction.DESCENDING
                    }
                    MyApplication.getContext()
                        .getString(R.string.sort_by_read) -> {
                        return Query.Direction.ASCENDING
                    }
                    else -> null
                }
            }
            return null
        }

    fun resetFilters() {
        if(mRootView!=null&&!isDetached) {
            mSortSpinner!!.setSelection(0)
        }
    }

    val filters: MyMessagesFilters
        get() {
            val filters =
                MyMessagesFilters()
            filters.read = selectedRead
            filters.sortBy = selectedSortBy
            filters.sortDirection = sortDirection
            return filters
        }
}