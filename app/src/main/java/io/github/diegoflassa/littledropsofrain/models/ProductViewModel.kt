package io.github.diegoflassa.littledropsofrain.models

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.diegoflassa.littledropsofrain.data.AppDatabase
import io.github.diegoflassa.littledropsofrain.data.ProductRepository
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : ViewModel() {
    private val repository: ProductRepository
    // Using LiveData and caching what allProducts returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allProducts: LiveData<List<Product>>

    init {
        val wordsDao = AppDatabase.getDatabase(
            application,
            viewModelScope
        ).productDao()
        repository =
            ProductRepository(
                wordsDao
            )
        allProducts = repository.allProducts
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(product)
    }
}
