package com.example.airavatresearchfoundation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airavatresearchfoundation.data.repo.ProductRepository
import com.example.airavatresearchfoundation.databinding.ActivityMainBinding
import com.example.airavatresearchfoundation.ui.viewmodel.ProductViewModel
import com.example.airavatresearchfoundation.ui.adapter.ProductAdapter
import com.example.airavatresearchfoundation.ui.viewmodel.ProductViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ProductViewModel
    @Inject
    lateinit var repository: ProductRepository

    private lateinit var adapter: ProductAdapter


    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as AiravatApplication).appComponent.inject(this)
        val factory = ProductViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        adapter = ProductAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.fetchProducts()

        viewModel.products.observe(this) {
            adapter.setProducts(it)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}