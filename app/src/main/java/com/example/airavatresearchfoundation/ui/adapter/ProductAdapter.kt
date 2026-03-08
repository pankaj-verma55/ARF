package com.example.airavatresearchfoundation.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.airavatresearchfoundation.data.Product
import com.example.airavatresearchfoundation.databinding.ItemProductBinding

class ProductAdapter(private val onItemClick: (Product) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    private val productList = ArrayList<Product>()
    private val filteredList = ArrayList<Product>()
    fun setProducts(list: List<Product>) {
        Log.d("Adapter", "List size = ${list.size}")
        productList.clear()
        productList.addAll(list)
        filteredList.clear()
        filteredList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = filteredList[position]

        holder.binding.title.text = product.title
        holder.binding.price.text = "₹ ${product.price}"
        holder.binding.category.text = "Category: ${product.category}"
        holder.binding.rating.text = "${product.rating}"
        holder.binding.root.setOnClickListener {
            onItemClick(product)
        }

        Glide.with(holder.itemView.context)
            .load(product.thumbnail)
            .into(holder.binding.image)
    }

    class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun filter(query: String) {

        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(productList)
        } else {

            for (product in productList) {
                if (product.title.contains(query, ignoreCase = true)) {
                    filteredList.add(product)
                }
            }
        }

        notifyDataSetChanged()
    }
}