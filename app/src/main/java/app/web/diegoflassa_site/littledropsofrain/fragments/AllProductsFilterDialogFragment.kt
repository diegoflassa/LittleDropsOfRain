package app.web.diegoflassa_site.littledropsofrain.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.web.diegoflassa_site.littledropsofrain.MyApplication
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentAllProductsFiltersBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.models.AllProductsFilterDialogViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons


/**
 * Dialog Fragment containing filter form.
 */
open class AllProductsFilterDialogFragment : DialogFragment(),
    View.OnClickListener, OnDataChangeListener<List<Product>>,
    CompoundButton.OnCheckedChangeListener {

    companion object {
        val TAG = AllProductsFilterDialogFragment::class.simpleName
    }

    interface FilterListener {
        fun onFilter(filters: AllProductsFilters)
    }

    val viewModel: AllProductsFilterDialogViewModel by viewModels()
    var categories: LinkedHashSet<String> = LinkedHashSet()
    private lateinit var mCategoryChipGroup: ChipGroup
    private var mSortSpinner: Spinner? = null
    private var mPriceSpinner: Spinner? = null
    var filterListener: FilterListener? = null
    private var binding: FragmentAllProductsFiltersBinding by viewLifecycle()
    private var mRootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllProductsFiltersBinding.inflate(inflater, container, false)
        mCategoryChipGroup = binding.categoryChipGroup
        mSortSpinner = binding.spinnerSort
        mPriceSpinner = binding.spinnerPrice
        binding.buttonSearch.setOnClickListener(this)
        binding.buttonCancel.setOnClickListener(this)
        binding.iconLikes.setImageDrawable(IconDrawable(requireContext(), SimpleLineIconsIcons.icon_heart))
        ProductDao.loadAll(this)
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

    private val selectedLikes: Boolean
        get() {
            return if (mRootView != null && !isDetached) {
                binding.chkBxLikes.isChecked
            }else{
                false
            }
        }

    private val selectedPrice: MutableList<Int>
        get() {
            val priceRange = ArrayList<Int>()
            if (mRootView != null && !isDetached) {
                when (mPriceSpinner!!.selectedItem as String) {
                    MyApplication.getContext()
                        .getString(R.string.price_1) -> {
                        priceRange.add(0)
                        priceRange.add(5000)
                    }
                    MyApplication.getContext()
                        .getString(R.string.price_2) -> {
                        priceRange.add(5100)
                        priceRange.add(10000)
                    }
                    MyApplication.getContext()
                        .getString(R.string.price_3) -> {
                        priceRange.add(10100)
                        priceRange.add(100000)
                    }
                    else -> {
                        //Do nothing
                    }
                }
            }
            return priceRange
        }

    private val selectedSortBy: String?
        get() {
            if (mRootView != null && !isDetached) {
                val selected = mSortSpinner!!.selectedItem as String
                return if (MyApplication.getContext()
                        .getString(R.string.sort_by_price) == selected
                ) {
                    return Product.PRICE
                } else if (MyApplication.getContext()
                        .getString(R.string.sort_by_likes) == selected
                ) {
                    return Product.LIKES
                }else {
                    return null
                }
            }
            return null
        }

    private val sortDirection: Query.Direction?
        get() {
            if (mRootView != null && !isDetached) {
                val selected = mSortSpinner!!.selectedItem as String
                return if (MyApplication.getContext()
                        .getString(R.string.sort_by_price) == selected
                ) {
                    return Query.Direction.DESCENDING
                } else if (MyApplication.getContext()
                        .getString(R.string.sort_by_likes) == selected
                ) {
                    return Query.Direction.DESCENDING
                } else null
            }
            return null
        }

    fun resetFilters() {
        if (mRootView != null && !isDetached) {
            for (chip in mCategoryChipGroup.children) {
                chip.isSelected = false
            }
            viewModel.viewState.categories.clear()
            binding.chkBxLikes.isChecked= false
            categories.clear()
            mPriceSpinner!!.setSelection(0)
            mSortSpinner!!.setSelection(0)
        }
    }

    val filters: AllProductsFilters
        get() {
            val filters =
                AllProductsFilters()
            filters.categories.addAll(this.selectedCategories)
            filters.likes = selectedLikes
            filters.price = if (selectedPrice.isEmpty()) {
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