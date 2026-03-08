package com.example.airavatresearchfoundation.data.repo

import com.example.airavatresearchfoundation.data.service.ApiService
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getProducts() = apiService.getProducts()

}