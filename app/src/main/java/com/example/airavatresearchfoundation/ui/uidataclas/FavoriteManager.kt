package com.example.airavatresearchfoundation.ui.uidataclas

import android.content.Context

class FavoriteManager(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun addFavorite(productId: Int) {

        val set = sharedPreferences.getStringSet("fav_products", mutableSetOf())!!.toMutableSet()
        set.add(productId.toString())

        sharedPreferences.edit().putStringSet("fav_products", set).apply()
    }

    fun removeFavorite(productId: Int) {

        val set = sharedPreferences.getStringSet("fav_products", mutableSetOf())!!.toMutableSet()
        set.remove(productId.toString())

        sharedPreferences.edit().putStringSet("fav_products", set).apply()
    }

    fun isFavorite(productId: Int): Boolean {

        val set = sharedPreferences.getStringSet("fav_products", mutableSetOf())!!
        return set.contains(productId.toString())
    }

    fun getFavorites(): Set<String> {
        return sharedPreferences.getStringSet("fav_products", mutableSetOf())!!
    }
}