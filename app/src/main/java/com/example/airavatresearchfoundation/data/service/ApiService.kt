package com.example.airavatresearchfoundation.data.service

import com.example.airavatresearchfoundation.data.ProductItem
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("products")
    suspend fun getProducts(): Response<ProductItem>

}