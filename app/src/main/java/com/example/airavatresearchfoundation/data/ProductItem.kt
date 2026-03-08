package com.example.airavatresearchfoundation.data

data class ProductItem(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)