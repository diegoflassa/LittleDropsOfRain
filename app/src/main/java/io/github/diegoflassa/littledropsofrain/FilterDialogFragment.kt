package io.github.diegoflassa.littledropsofrain

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.Query
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.FontAwesomeIcons
import io.github.diegoflassa.littledropsofrain.data.DataChangeListener
import io.github.diegoflassa.littledropsofrain.data.dao.ProductDao
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import io.github.diegoflassa.littledropsofrain.databinding.FragmentFiltersBinding
import io.github.diegoflassa.littledropsofrain.ui.home.HomeFragment

/**
 * Dialog Fragment containing filter form.
 */
open class FilterDialogFragment(fragment : HomeFragment) : DialogFragment(),
    View.OnClickListener, DataChangeListener<List<Product>>,
    CompoundButton.OnCheckedChangeListener {
    interface FilterListener {
        fun onFilter(filters: Filters)
    }

    private var homeFragment : HomeFragment= fragment
    var mCategories: LinkedHashSet<String> = LinkedHashSet()
    private lateinit var mCategoryChipGroup: ChipGroup
    private var mSortSpinner: Spinner? = null
    private var mPriceSpinner: Spinner? = null
    var filterListener: FilterListener? = null
    private lateinit var binding: FragmentFiltersBinding
    private var mRootView : View?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFiltersBinding.inflate(inflater, container, false)
        mCategoryChipGroup = binding.categoryChipGroup
        mSortSpinner = binding.spinnerSort
        mPriceSpinner = binding.spinnerPrice
        binding.iconCategory.setImageDrawable(IconDrawable(context, FontAwesomeIcons.fa_empire))
        binding.buttonSearch.setOnClickListener(this)
        binding.buttonCancel.setOnClickListener(this)
        ProductDao.loadAll(this)
        mRootView= binding.root
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface){
        super.onDismiss(dialog)
        homeFragment.binding.filterBar.isEnabled = true
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
            return mCategories
        }

    private val selectedPrice: MutableList<Int>
        get() {
            val priceRange = ArrayList<Int>()
            if(mRootView!=null) {
                when (mPriceSpinner!!.selectedItem as String) {
                    getString(R.string.price_1) -> {
                        priceRange.add(0)
                        priceRange.add(5000)
                    }
                    getString(R.string.price_2) -> {
                        priceRange.add(5100)
                        priceRange.add(10000)
                    }
                    getString(R.string.price_3) -> {
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
            if(mRootView!=null){
                val selected = mSortSpinner!!.selectedItem as String
                return if (getString(R.string.sort_by_price) == selected) {
                    return Product.PRICE
                } else null
            }
            return null
        }

    private val sortDirection: Query.Direction?
        get() {
            if(mRootView!=null) {
                val selected = mSortSpinner!!.selectedItem as String
                return if (getString(R.string.sort_by_price) == selected) {
                    return Query.Direction.DESCENDING
                } else null
            }
            return null
        }

    fun resetFilters() {
        if(mRootView!=null) {
            for (chip in mCategoryChipGroup.children) {
                chip.isSelected = false
            }
            mCategories.clear()
            mPriceSpinner!!.setSelection(0)
            mSortSpinner!!.setSelection(0)
        }
    }

    val filters: Filters
        get() {
            val filters = Filters()
            filters.categories.addAll(this.selectedCategories)
            filters.price = if(selectedPrice.isEmpty()){null}else{selectedPrice}
            filters.sortBy = selectedSortBy
            filters.sortDirection = sortDirection
            return filters
        }

    companion object {
        const val TAG = "FilterDialog"
    }

    override fun onDataLoaded(item: List<Product>) {
        val hashSet = LinkedHashSet<String>()
        for(product in item) {
            for(category in product.categories) {
                hashSet.add(category)
            }
        }
        mCategoryChipGroup.removeAllViews()
        for(category in hashSet)
            if(category.trim().isNotEmpty()) {
            val chipCategory = Chip(context)
            chipCategory.isCheckable = true
            chipCategory.isChecked = mCategories.contains(category)
            chipCategory.text = category
            chipCategory.setOnCheckedChangeListener(this)
            mCategoryChipGroup.addView(chipCategory)
        }
    }

    override fun onCheckedChanged(compoundButton : CompoundButton?, checked: Boolean) {
        if(checked) {
            mCategories.add(compoundButton?.text.toString())
        }else{
            mCategories.remove(compoundButton?.text)
        }
    }
}