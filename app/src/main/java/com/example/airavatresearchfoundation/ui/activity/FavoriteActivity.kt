package com.example.airavatresearchfoundation.ui.activity

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airavatresearchfoundation.AiravatApplication
import com.example.airavatresearchfoundation.R
import com.example.airavatresearchfoundation.data.Product
import com.example.airavatresearchfoundation.data.repo.ProductRepository
import com.example.airavatresearchfoundation.databinding.ActivityFavoriteBinding
import com.example.airavatresearchfoundation.ui.adapter.ProductAdapter
import com.example.airavatresearchfoundation.ui.uidataclas.FavoriteManager
import com.example.airavatresearchfoundation.ui.viewmodel.ProductViewModel
import com.example.airavatresearchfoundation.ui.viewmodel.ProductViewModelFactory
import javax.inject.Inject

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFavoriteBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: ProductViewModel
    private var selectedCategoryIndex = 0
    @Inject
    lateinit var repository: ProductRepository
    private var allProducts: List<Product> = listOf()
    private val categories = listOf(
        "All",
        "smartphones",
        "laptops",
        "fragrances",
        "beauty",
        "fragrances",
        "furniture",
        "motorcycle",
        "skin-care",
        "sunglasses",
        "groceries"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as AiravatApplication).appComponent.inject(this)
        val factory = ProductViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]
        viewModel.getProducts()

        adapter = ProductAdapter(onItemClick = { /* Handle click if needed */ },
            onFavoriteClick = { product ->
                val favoriteManager = FavoriteManager(this)
                if (favoriteManager.isFavorite(product.id)) {
                    favoriteManager.removeFavorite(product.id)
                } else {
                    favoriteManager.addFavorite(product.id)
                }
            })

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        viewModel.products.observe(this) {

            allProducts = it

            loadFavorites()

        }

        binding.filterIcon.setOnClickListener {
            showCategoryDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun loadFavorites() {

        val favoriteManager = FavoriteManager(this)

        val favoriteIds = favoriteManager.getFavorites()

        val favoriteProducts = allProducts.filter {
            favoriteIds.contains(it.id.toString())
        }

        adapter.setProducts(favoriteProducts.toMutableList())
    }

    private fun showCategoryDialog() {

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Select Category")

        builder.setSingleChoiceItems(
            categories.toTypedArray(),
            selectedCategoryIndex
        ) { _, position ->

            selectedCategoryIndex = position
        }

        builder.setPositiveButton("Apply") { dialog, _ ->

            val selectedCategory = categories[selectedCategoryIndex]

            if (selectedCategory == "All") {
                viewModel.getProducts()
            } else {
                viewModel.fetchProductsByCategory(selectedCategory)
            }

            dialog.dismiss()
        }

        builder.show()
    }
}