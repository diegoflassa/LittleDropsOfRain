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

package app.web.diegoflassa_site.littledropsofrain.presentation.old.fragments.myMessagesFilterDialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentMyMessagesFiltersBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.MyApplication
import app.web.diegoflassa_site.littledropsofrain.presentation.old.fragments.myMessagesFilterDialog.model.MyMessagesFilterDialogViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.old.helper.viewLifecycle
import com.google.firebase.firestore.Query

import java.util.*

/**
 * Dialog Fragment containing filter form.
 */
@ExperimentalStdlibApi
open class MyMessagesFilterDialogFragment :
    DialogFragment(),
    View.OnClickListener {

    companion object {
        val TAG = MyMessagesFilterDialogFragment::class.simpleName
    }

    interface FilterListener {
        fun onFilter(filters: MyMessagesFilters)
    }

    val viewModel: MyMessagesFilterDialogViewModel by viewModels()
    private lateinit var mSpinnerSort: Spinner
    private lateinit var mSpinnerType: Spinner
    var filterListener: FilterListener? = null
    var binding: FragmentMyMessagesFiltersBinding by viewLifecycle()
    private var mRootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyMessagesFiltersBinding.inflate(inflater, container, false)
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
        mSpinnerSort = binding.spinnerSort
        mSpinnerType = binding.spinnerType
        binding.switchMsgRead.isEnabled = false
        binding.switchMsgRead.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            filters.read = checked
        }
        mRootView = binding.root
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        val fragment: Fragment = parentFragmentManager.fragments[0]
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

    @ExperimentalStdlibApi
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
                return when (binding.spinnerSort.selectedItem) {
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
            if (mRootView != null && !isDetached && binding.checkBoxMsgRead.isChecked) {
                return binding.switchMsgRead.isChecked
            }
            return null
        }

    @ExperimentalStdlibApi
    private val selectedType: MessageType?
        get() {
            if (mRootView != null && !isDetached) {
                return if (binding.spinnerType.selectedItemPosition > 0) {
                    MessageType.valueOf(
                        binding.spinnerType.selectedItem.toString()
                            .uppercase(Locale.ROOT)
                    )
                } else {
                    null
                }
            }
            return null
        }

    private val sortDirection: Query.Direction?
        get() {
            if (mRootView != null && !isDetached) {
                return when (binding.spinnerSort.selectedItem) {
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
            mSpinnerSort.setSelection(0)
            mSpinnerType.setSelection(0)
        }
    }

    @ExperimentalStdlibApi
    val filters: MyMessagesFilters
        get() {
            val filters =
                MyMessagesFilters()
            filters.read = selectedRead
            filters.type = selectedType
            filters.sortBy = selectedSortBy
            filters.sortDirection = sortDirection
            return filters
        }
}
