package io.github.diegoflassa.littledropsofrain.fragments

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import io.github.diegoflassa.littledropsofrain.MyApplication
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.Message
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.databinding.FragmentAllMessagesFiltersBinding
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.interfaces.OnUsersLoadedListener
import io.github.diegoflassa.littledropsofrain.ui.admin.AdminFragment

/**
 * Dialog Fragment containing filter form.
 */
open class AllMessagesFilterDialogFragment(fragment : AdminFragment) : DialogFragment(),
    View.OnClickListener, OnUsersLoadedListener {

    companion object {
        val TAG = AllMessagesFilterDialogFragment::class.simpleName
    }

    interface FilterListener {
        fun onFilter(filters: AllMessagesFilters)
    }

    private var adminFragment : AdminFragment = fragment
    private var mSortSpinner: Spinner? = null
    private var mUsersSpinner: Spinner? = null
    var filterListener: FilterListener? = null
    var binding: FragmentAllMessagesFiltersBinding by viewLifecycle()
    private var mRootView : View?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllMessagesFiltersBinding.inflate(inflater, container, false)
        mSortSpinner = binding.spinnerSort
        mUsersSpinner = binding.spinnerUsers
        binding.iconUsers.setImageDrawable(IconDrawable(requireContext(), SimpleLineIconsIcons.icon_users).colorRes(R.color.colorAccent))
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
        UserDao.loadAll(this)
        mRootView= binding.root
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface){
        super.onDismiss(dialog)
        adminFragment.binding.filterBarAllMessages.isEnabled = true
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

    private val selectedEmailSender: String?
        get() {
            if(mRootView!=null&&!isDetached) {
                return if (mUsersSpinner!!.selectedItemPosition > 0) {
                    return (mUsersSpinner!!.selectedItem as User).email
                } else null
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
            mUsersSpinner!!.setSelection(0)
            mSortSpinner!!.setSelection(0)
        }
    }

    val filters: AllMessagesFilters
        get() {
            val filters =
                AllMessagesFilters()
            filters.read = selectedRead
            filters.emailSender = selectedEmailSender
            filters.sortBy = selectedSortBy
            filters.sortDirection = sortDirection
            return filters
        }

    override fun onUsersLoaded(users: List<User>) {
        val usersWithDefault = ArrayList<User>(users.size+1)
        val user = User()
        user.name = "No Selection"
        user.email = "None"
        usersWithDefault.add(user)
        usersWithDefault.addAll(users)
        val dataAdapter: ArrayAdapter<User> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, usersWithDefault
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUsers.adapter= dataAdapter
        binding.spinnerUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                filters.emailSender = (binding.spinnerUsers.adapter.getItem(pos) as User).email
            }
            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                filters.emailSender = null
            }
        }
    }
}