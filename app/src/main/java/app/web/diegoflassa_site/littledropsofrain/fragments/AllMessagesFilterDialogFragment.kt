package app.web.diegoflassa_site.littledropsofrain.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.web.diegoflassa_site.littledropsofrain.MyApplication
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentAllMessagesFiltersBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnUsersLoadedListener
import app.web.diegoflassa_site.littledropsofrain.models.AllMessagesFilterDialogViewModel
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import java.util.*
import kotlin.collections.ArrayList


/**
 * Dialog Fragment containing filter form.
 */
open class AllMessagesFilterDialogFragment : DialogFragment(),
    View.OnClickListener, OnUsersLoadedListener {

    companion object {
        val TAG = AllMessagesFilterDialogFragment::class.simpleName
    }

    interface FilterListener {
        fun onFilter(filters: AllMessagesFilters)
    }

    private lateinit var mSpinnerUsers: Spinner
    private lateinit var mSpinnerSort: Spinner
    private lateinit var mSpinnerType: Spinner
    var filterListener: FilterListener? = null
    val viewModel: AllMessagesFilterDialogViewModel by viewModels()
    var binding: FragmentAllMessagesFiltersBinding by viewLifecycle()
    private var mSavedInstanceState: Bundle? = null
    private var mRootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mSavedInstanceState = savedInstanceState
        binding = FragmentAllMessagesFiltersBinding.inflate(inflater, container, false)
        binding.iconUsers.setImageDrawable(
            IconDrawable(
                requireContext(),
                SimpleLineIconsIcons.icon_users
            ).colorRes(R.color.colorAccent)
        )
        mSpinnerUsers = binding.spinnerUsers
        mSpinnerSort = binding.spinnerSort
        mSpinnerType = binding.spinnerType
        binding.buttonSearchMessages.setOnClickListener(this)
        binding.buttonCancelMessages.setOnClickListener(this)
        binding.checkBoxMsgRead.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            binding.switchMsgRead.isEnabled = checked
            if (!checked) {
                filters.read = null
            } else {
                binding.spinnerSort.setSelection(0)
            }
            binding.spinnerSort.isEnabled = !checked
        }
        binding.switchMsgRead.isEnabled = false
        binding.switchMsgRead.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            filters.read = checked
        }
        UserDao.loadAll(this)
        mRootView = binding.root
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        val fragment: Fragment? = parentFragmentManager.fragments[0]
        if (fragment is DialogInterface.OnDismissListener) {
            (fragment as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
        super.onDismiss(dialog)
        Log.d(TAG, "Dialog dismissed")
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
            if (mRootView != null && !isDetached) {
                return when (binding.spinnerSort.selectedItem as String) {
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

    private val selectedType: MessageType?
        get() {
            if (mRootView != null && !isDetached) {
                return if (binding.spinnerType.selectedItemPosition > 0) {
                    MessageType.valueOf(
                        binding.spinnerType.selectedItem.toString()
                            .toUpperCase(Locale.ROOT)
                    )
                } else {
                    null
                }
            }
            return null
        }

    private val selectedRead: Boolean?
        get() {
            if (mRootView != null && !isDetached) {
                if (binding.checkBoxMsgRead.isChecked) {
                    return binding.switchMsgRead.isChecked
                }
            }
            return null
        }

    private val selectedEmailSender: String?
        get() {
            if (mRootView != null && !isDetached) {
                return if (binding.spinnerUsers.selectedItemPosition > 0) {
                    return (binding.spinnerUsers.selectedItem as User).email
                } else null
            }
            return null
        }

    private val sortDirection: Query.Direction?
        get() {
            if (mRootView != null && !isDetached) {
                return when (binding.spinnerSort.selectedItem as String) {
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
        if (mRootView != null && !isDetached) {
            mSpinnerUsers.setSelection(0)
            mSpinnerSort.setSelection(0)
            mSpinnerType.setSelection(0)
        }
    }

    val filters: AllMessagesFilters
        get() {
            val filters =
                AllMessagesFilters()
            filters.read = selectedRead
            filters.emailSender = selectedEmailSender
            filters.type = selectedType
            filters.sortBy = selectedSortBy
            filters.sortDirection = sortDirection
            return filters
        }

    override fun onUsersLoaded(users: List<User>) {
        val usersWithDefault = ArrayList<User>(users.size + 1)
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
        binding.spinnerUsers.adapter = dataAdapter
        binding.spinnerUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                filters.emailSender = (binding.spinnerUsers.adapter.getItem(pos) as User).email
                viewModel.viewState.selectedUserEmail = filters.emailSender.toString()
            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                filters.emailSender = null
            }
        }
        if (viewModel.viewState.selectedUserEmail.isNotEmpty()) {
            for (index in 0 until binding.spinnerUsers.adapter.count) {
                val userAdapter = binding.spinnerUsers.adapter.getItem(index) as User
                if (userAdapter.email == viewModel.viewState.selectedUserEmail) {
                    binding.spinnerUsers.setSelection(index)
                    break
                }
            }
        }
    }
}