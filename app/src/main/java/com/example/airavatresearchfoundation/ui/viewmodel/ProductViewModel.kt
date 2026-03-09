package com.example.airavatresearchfoundation.ui.viewmodel

import androidx.lifecycle.*
import com.example.airavatresearchfoundation.data.Product
import com.example.airavatresearchfoundation.data.repo.ProductRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    var isRatingSort = false
    var isPriceSort = false
    var skip = 0
    val limit = 20
    var isLoading = false
    var isLastPage = false

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val productList = ArrayList<Product>()
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    fun fetchProductsByCategory(category: String) {

        viewModelScope.launch {

            val response = repository.getProductsByCategory(category)

            if (response.isSuccessful) {
                _products.value = response.body()?.products
            }

        }
    }


    fun getProducts(isPagination: Boolean = false) {

        if (isPagination && (isLoading || isLastPage)) return

        isLoading = true
        _loading.postValue(true)

        viewModelScope.launch {

            try {

                val response = repository.getProducts(limit, skip)

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body?.products?.isNotEmpty() == true) {

                        if (!isPagination) {
                            productList.clear()
                        }

                        productList.addAll(body.products)

                        if (isRatingSort) {
                            productList.sortByDescending { it.rating }
                        }
                        if (isPriceSort) {
                            productList.sortByDescending { it.price }
                        }

                        _products.postValue(productList)

                        skip += limit
                    }

                    if (skip >= (body?.total ?: 0)) {
                        isLastPage = true
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            isLoading = false
            _loading.postValue(false)
        }
    }

    fun refreshProducts() {

        skip = 0
        isLastPage = false
        isLoading = false
        productList.clear()
    }
    fun sortByRating() {
        isRatingSort = true
        isPriceSort = false

        productList.sortByDescending { it.rating }
        _products.postValue(ArrayList(productList))
    }

    fun sortByPrice() {
        isPriceSort = true
        isRatingSort = false

        productList.sortByDescending { it.price }
        _products.postValue(ArrayList(productList))
    }


}