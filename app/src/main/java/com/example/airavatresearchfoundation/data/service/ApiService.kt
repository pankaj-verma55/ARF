package com.example.airavatresearchfoundation.data.service

import com.example.airavatresearchfoundation.data.ProductItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

//    @GET("products")
//    suspend fun getProductPaging(
//        @Query("limit") limit: Int? = null,
//        @Query("skip") skip: Int? = null
//    ): Response<ProductItem>

    @GET("products")
    suspend fun getProductPaging(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductItem

    @GET("products")
    suspend fun getProducts(): Response<ProductItem>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): Response<ProductItem>


}