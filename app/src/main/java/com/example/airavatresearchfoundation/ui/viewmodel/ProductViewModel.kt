package com.example.airavatresearchfoundation.ui.viewmodel

import androidx.lifecycle.*
import com.example.airavatresearchfoundation.data.Product
import com.example.airavatresearchfoundation.data.repo.ProductRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    var skip = 0
    val limit = 20
    var isLoading = false
    var isLastPage = false

    private val productList = ArrayList<Product>()
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
    fun loadProducts() {

        if (isLoading || isLastPage) return

        isLoading = true

        viewModelScope.launch {

            try {

                val response = repository.getProductPaging(limit, skip)

                if (response.products.isNotEmpty()) {

                    productList.addAll(response.products)

                    _products.postValue(productList)

                    skip += limit
                }

                if (skip >= response.total) {
                    isLastPage = true
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            isLoading = false
        }

    }

    fun refreshProducts() {

        skip = 0
        isLastPage = false
        isLoading = false
        productList.clear()

//        loadProducts()
    }


}