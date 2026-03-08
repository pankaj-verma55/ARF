package com.example.airavatresearchfoundation.ui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.airavatresearchfoundation.AiravatApplication
import com.example.airavatresearchfoundation.R
import com.example.airavatresearchfoundation.data.Product
import com.example.airavatresearchfoundation.data.repo.ProductRepository
import com.example.airavatresearchfoundation.databinding.ActivityMainBinding
import com.example.airavatresearchfoundation.databinding.DialogProductBinding
import com.example.airavatresearchfoundation.ui.viewmodel.ProductViewModel
import com.example.airavatresearchfoundation.ui.adapter.ProductAdapter
import com.example.airavatresearchfoundation.ui.viewmodel.ProductViewModelFactory
import java.util.Locale.Category
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ProductViewModel
    @Inject
    lateinit var repository: ProductRepository
    private var selectedCategoryIndex = 0

    private lateinit var adapter: ProductAdapter
    private val categories = listOf(
        "All",
        "smartphones",
        "laptops",
        "fragrances",
        "skincare",
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

        adapter = ProductAdapter{ product ->

            showProductDialog(product)

        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.fetchProducts()

        viewModel.products.observe(this) {
            adapter.setProducts(it)
        }

        binding.searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(enterText: String?): Boolean {

                adapter.filter(enterText ?: "")

                checkProductAvailable(enterText ?: "")

                return true
            }
        })

        binding.filterIcon.setOnClickListener {
            showCategoryDialog()
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
                viewModel.fetchProducts()
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