package com.example.airavatresearchfoundation.ui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.airavatresearchfoundation.AiravatApplication
import com.example.airavatresearchfoundation.R
import com.example.airavatresearchfoundation.data.Product
import com.example.airavatresearchfoundation.data.repo.ProductRepository
import com.example.airavatresearchfoundation.databinding.ActivityMainBinding
import com.example.airavatresearchfoundation.databinding.DialogProductBinding
import com.example.airavatresearchfoundation.ui.viewmodel.ProductViewModel
import com.example.airavatresearchfoundation.ui.adapter.ProductAdapter
import com.example.airavatresearchfoundation.ui.uidataclas.FavoriteManager
import com.example.airavatresearchfoundation.ui.viewmodel.ProductViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ProductViewModel
    @Inject
    lateinit var repository: ProductRepository
    private var selectedCategoryIndex = 0

    private lateinit var adapter: ProductAdapter
    private var originalList: List<Product> = listOf()
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


    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as AiravatApplication).appComponent.inject(this)
        val factory = ProductViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        adapter = ProductAdapter(
            onItemClick = { product ->
                showProductDialog(product)
            },
            onFavoriteClick = { product ->
                val favoriteManager = FavoriteManager(this)
                if (favoriteManager.isFavorite(product.id)) {
                    favoriteManager.removeFavorite(product.id)
                } else {
                    favoriteManager.addFavorite(product.id)
                }
            }
        )


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // NEW: Check if we are currently searching or in a specific category
                val isSearching = binding.searchProduct.query.isNotEmpty()
                val isFilteredCategory = categories[selectedCategoryIndex] != "All"

                if (!viewModel.isLoading && !viewModel.isLastPage && !isSearching && !isFilteredCategory) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        viewModel.loadProducts()
                    }
                }
            }
        })

        viewModel.loadProducts()

        viewModel.products.observe(this) {
            originalList = it
            adapter.setProducts(it.toMutableList())
        }
        binding.searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                query?.let {
                    adapter.filter(it)
                    checkProductAvailable(it)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                adapter.filter(newText ?: "")
                checkProductAvailable(newText ?: "")

                return true
            }
        })
        var selectedId = -1

        binding.priceSort.setOnClickListener {

            if (selectedId == R.id.priceSort) {

                // unselect
                binding.radioGroupSort.clearCheck()
                selectedId = -1

                adapter.setProducts(originalList.toMutableList()) // restore original list

            } else {

                selectedId = R.id.priceSort

                val sorted = adapter.getCurrentList().sortedByDescending { it.price }
                adapter.setProducts(sorted.toMutableList())
            }
        }

        binding.ratingSort.setOnClickListener {

            if (selectedId == R.id.ratingSort) {

                binding.radioGroupSort.clearCheck()
                selectedId = -1

                adapter.setProducts(originalList.toMutableList())

            } else {

                selectedId = R.id.ratingSort

                val sorted = adapter.getCurrentList().sortedByDescending { it.rating }
                adapter.setProducts(sorted.toMutableList())
            }
        }

        binding.filterIcon.setOnClickListener {
            showCategoryDialog()
        }

        binding.favoriteBtn.setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            selectedCategoryIndex = 0

            viewModel.refreshProducts()
            viewModel.loadProducts()


            binding.swipeRefreshLayout.isRefreshing = false
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
                viewModel.loadProducts()
            } else {
                viewModel.fetchProductsByCategory(selectedCategory)
            }

            dialog.dismiss()
        }

        builder.show()
    }

    private fun showProductDialog(product: Product) {

        val dialog = Dialog(this)
        val binding = DialogProductBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)
        Glide.with(binding.image.context)
            .load(product.thumbnail)
            .into(binding.image)
        binding.title.text = product.title
        binding.price.text = "Price: ₹ ${product.price}"
        binding.rating.text = product.rating.toString()
        binding.brand.text = "Brand: ${product.brand}"
        binding.discount.text = "Discount: ${product.discountPercentage}%"
        binding.category.text = "Category: ${product.category}"
        binding.description.text = "Description: ${product.description}"
        binding.stockAvailable.text = "Stock Available: ${product.stock}"
        dialog.show()

    }

    private fun checkProductAvailable(query: String) {

        if (adapter.itemCount == 0) {
            binding.tvNoProduct.visibility = View.VISIBLE
            binding.tvNoProduct.text = "Product '$query' not available"
        } else {
            binding.tvNoProduct.visibility = View.GONE
        }
    }

}