package com.example.airavatresearchfoundation.data.repo

import com.example.airavatresearchfoundation.data.ProductItem
import com.example.airavatresearchfoundation.data.service.ApiService
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getProducts() = apiService.getProducts()

    suspend fun getProductPaging(limit: Int, skip: Int): ProductItem {
        return apiService.getProductPaging(limit, skip)
    }
    suspend fun getProductsByCategory(category: String) =
        apiService.getProductsByCategory(category)

}