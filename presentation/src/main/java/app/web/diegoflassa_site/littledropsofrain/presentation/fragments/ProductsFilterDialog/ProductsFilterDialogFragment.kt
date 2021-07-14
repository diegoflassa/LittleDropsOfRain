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

package app.web.diegoflassa_site.littledropsofrain.presentation.fragments.ProductsFilterDialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentProductsFiltersBinding
import app.web.diegoflassa_site.littledropsofrain.presentation.MyApplication
import app.web.diegoflassa_site.littledropsofrain.presentation.fragments.ProductsFilterDialog.model.ProductsFilterDialogViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.Query
import org.koin.androidx.viewmodel.ext.android.stateViewModel

/**
 * Dialog Fragment containing filter form.
 */
open class ProductsFilterDialogFragment :
    DialogFragment(),
    View.OnClickListener,
    OnDataChangeListener<List<Product>>,
    CompoundButton.OnCheckedChangeListener {

    companion object {
        val TAG = ProductsFilterDialogFragment::class.simpleName
    }

    interface FilterListener {
        fun onFilter(filters: ProductsFilters)
    }

    val viewModel: ProductsFilterDialogViewModel by stateViewModel()
    var categories: LinkedHashSet<String> = LinkedHashSet()
    private lateinit var mCategoryChipGroup: ChipGroup
    private var mSortSpinner: Spinner? = null
    private var mPriceSpinner: Spinner? = null
    var filterListener: FilterListener? = null
    private var binding: FragmentProductsFiltersBinding by viewLifecycle()
    private var mRootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductsFiltersBinding.inflate(inflater, container, false)
        mCategoryChipGroup = binding.categoryChipGroup
        mSortSpinner = binding.spinnerSort
        mPriceSpinner = binding.spinnerPrice
        mPriceSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    mSortSpinner!!.isEnabled = false
                    binding.spinnerSort.setSelection(0)
                } else {
                    mSortSpinner!!.isEnabled = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { /*Unused*/ }
        }
        binding.buttonSearch.setOnClickListener(this)
        binding.buttonCancel.setOnClickListener(this)
        ProductDao.loadAllPublished(this)
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

    override fun onStop() {
        viewModel.viewState.categories = categories
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        categories = viewModel.viewState.categories
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_search -> onSearchClicked()
            R.id.button_cancel -> onCancelClicked()
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

    private val selectedCategories: LinkedHashSet<String>
        get() {
            return categories
        }

    private val selectedPrice: Pair<Int, Int>?
        get() {
            var priceRange: Pair<Int, Int>? = null
            if (mRootView != null && !isDetached) {
                when (mPriceSpinner!!.selectedItem as String) {
                    MyApplication.getContext()
                        .getString(R.string.price_1) -> {
                        priceRange = Pair(0, 5000)
                    }
                    MyApplication.getContext()
                        .getString(R.string.price_2) -> {
                        priceRange = Pair(5100, 10000)
                    }
                    MyApplication.getContext()
                        .getString(R.string.price_3) -> {
                        priceRange = Pair(10100, 100000)
                    }
                    else -> {
                        priceRange = null
                        // Do nothing
                    }
                }
            }
            return priceRange
        }

    private val selectedSortBy: String?
        get() {
            if (mRootView != null && !isDetached) {
                return when (mSortSpinner!!.selectedItem as String) {
                    MyApplication.getContext()
                        .getString(R.string.sort_by_price) -> {
                        Product.PRICE
                    }
                    MyApplication.getContext()
                        .getString(R.string.sort_by_likes) -> {
                        Product.LIKES
                    }
                    else -> {
                        null
                    }
                }
            }
            return null
        }

    private val sortDirection: Query.Direction?
        get() {
            if (mRootView != null && !isDetached) {
                return when (mSortSpinner!!.selectedItem as String) {
                    MyApplication.getContext()
                        .getString(R.string.sort_by_price) -> {
                        return Query.Direction.DESCENDING
                    }
                    MyApplication.getContext()
                        .getString(R.string.sort_by_likes) -> {
                        return Query.Direction.DESCENDING
                    }
                    else -> null
                }
            }
            return null
        }

    fun resetFilters() {
        if (mRootView != null && !isDetached) {
            for (chip in mCategoryChipGroup.children) {
                chip.isSelected = false
            }
            viewModel.viewState.categories.clear()
            categories.clear()
            mPriceSpinner!!.setSelection(0)
            mSortSpinner!!.setSelection(0)
        }
    }

    val filters: ProductsFilters
        get() {
            val filters =
                ProductsFilters()
            filters.categories.addAll(this.selectedCategories)
            filters.price = if (selectedPrice == null) {
                null
            } else {
                selectedPrice
            }
            filters.sortBy = selectedSortBy
            filters.sortDirection = sortDirection
            return filters
        }

    private fun setSelectedChips() {
        for (child in mCategoryChipGroup.children) {
            val chip: Chip = child as Chip
            chip.isChecked = categories.contains(chip.text)
        }
    }

    override fun onDataChanged(item: List<Product>) {
        if (isVisible) {
            val hashSet = LinkedHashSet<String>()
            for (product in item) {
                for (category in product.categories) {
                    hashSet.add(category)
                }
            }
            mCategoryChipGroup.removeAllViews()
            for (category in hashSet) {
                if (category.trim().isNotEmpty()) {
                    val chipCategory = Chip(requireContext())
                    chipCategory.isCheckable = true
                    chipCategory.isChecked = categories.contains(category)
                    chipCategory.text = category
                    chipCategory.setOnCheckedChangeListener(this)
                    mCategoryChipGroup.addView(chipCategory)
                }
            }
            setSelectedChips()
        }
    }

    override fun onCheckedChanged(compoundButton: CompoundButton?, checked: Boolean) {
        if (checked) {
            categories.add(compoundButton?.text.toString())
        } else {
            categories.remove(compoundButton?.text)
        }
    }
}
