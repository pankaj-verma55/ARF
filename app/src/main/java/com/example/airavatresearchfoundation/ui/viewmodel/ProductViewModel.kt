package com.example.airavatresearchfoundation.ui.viewmodel

import androidx.lifecycle.*
import com.example.airavatresearchfoundation.data.Product
import com.example.airavatresearchfoundation.data.repo.ProductRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products


    fun fetchProducts() {

        viewModelScope.launch {

            val response = repository.getProducts()

            if (response.isSuccessful) {
                _products.value = response.body()?.products
            }

        }
    }
    fun fetchProductsByCategory(category: String) {

        viewModelScope.launch {

            val response = repository.getProductsByCategory(category)

            if (response.isSuccessful) {
                _products.value = response.body()?.products
            }

        }
    }

}